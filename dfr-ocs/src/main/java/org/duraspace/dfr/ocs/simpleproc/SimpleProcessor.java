package org.duraspace.dfr.ocs.simpleproc;

import com.github.cwilper.fcrepo.dto.core.ContentDigest;
import com.github.cwilper.fcrepo.dto.core.ControlGroup;
import com.github.cwilper.fcrepo.dto.core.Datastream;
import com.github.cwilper.fcrepo.dto.core.DatastreamVersion;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.duracloud.client.ContentStore;
import org.duraspace.dfr.ocs.core.FedoraObjectStore;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventProcessor;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * A {@link org.duraspace.dfr.ocs.core.StorageObjectEventProcessor} that
 * produces a simple wrapper Fedora object for each storage object. New
 * objects will be ingested into the given {@link FedoraObjectStore} and deleted
 * objects will be purged from it.
 * <p>
 * An external Fedora datastream with id <code>CONTENT</code> will be created
 * in the Fedora object. This will reference the content inside DuraCloud
 * via URL.
 * <p>
 * This implementation requires that all {@link StorageObject}s sent to it
 * have the following DuraCloud-specific metadata defined:
 * <ul>
 *   <li> <code>store-id</code> - the DuraCloud store id</li>
 *   <li> <code>space-id</code> - the DuraCloud space id</li>
 * </ul>
 * In addition, for all non-DELETE events, the following must be defined:
 * <ul>
 *   <li> <code>content-checksum</code> - the hexadecimal md5 checksum of the content</li>
 *   <li> <code>content-mimetype</code> - the internet media type of the content</li>
 *   <li> <code>content-size</code> - the size of the content in bytes</li>
 *   <li> <code>content-modified</code> - the content modified date in RFC822 format</li>
 * </ul>
 * If any expected metadata is not found, an {@link IllegalArgumentException}
 * will be thrown from
 * {@link #process(org.duraspace.dfr.ocs.core.StorageObjectEvent)}.
 * <p>
 * If any additional metadata is provided, it will be ignored.
 */
public class SimpleProcessor implements StorageObjectEventProcessor {
    private static final String RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String STORE_ID = "store-id";
    private static final String SPACE_ID = "space-id";

    private final String pidPrefix;
    private final String duraStoreURL;
    
    private FedoraObjectStore fedoraObjectStore;

    /**
     * Creates an instance.
     *
     * @param pidPrefix the Fedora PID prefix to use, which must start with a
     *                  namespace followed by a colon. It may include
     *                  additional PID-safe characters as well. This, followed
     *                  by an md5 hex encoding of the DuraCloud URL of the
     *                  content will be used to determine the PID of the
     *                  Fedora object.
     * @param duraStoreURL the base URL of the DuraStore service, e.g.
     *                     <code>https://example.org/durastore</code>.
     * @throws NullPointerException if pidPrefix or duraStoreURL is
     *                              <code>null</code>.
     * @throws IllegalArgumentException if pidPrefix or duraStoreURL is
     *                                  malformed.
     * @see <a href="https://wiki.duraspace.org/display/FEDORA36/Fedora+Identifiers">
     *        Fedora Identifiers Reference Documentation
     *      </a>
     */
    public SimpleProcessor(String pidPrefix, String duraStoreURL) {
        if (pidPrefix == null || duraStoreURL == null) {
            throw new NullPointerException();
        }
        if (pidPrefix.indexOf(":") < 1) {
            throw new IllegalArgumentException("Malformed pidPrefix: " +
                    pidPrefix);
        }
        this.pidPrefix = pidPrefix;
        try {
            new URL(duraStoreURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed duraStoreURL: " +
                    duraStoreURL);
        }
        this.duraStoreURL = duraStoreURL;
    }

    @Override
    public void setFedoraObjectStore(FedoraObjectStore fedoraObjectStore) {
        this.fedoraObjectStore = fedoraObjectStore;
    }

    @Override
    public void process(StorageObjectEvent event) throws OCSException {
        StorageObject storageObject = event.getStorageObject();
        String contentId = storageObject.getId();
        Map<String, String> metadata = storageObject.getMetadata();
        String contentURL = duraStoreURL + "/" +
                metadata.get(SPACE_ID) + "/" + contentId + "?storeID=" +
                metadata.get(STORE_ID);
        String pid = pidPrefix + DigestUtils.md5Hex(contentURL);
        requireValues(metadata, STORE_ID, SPACE_ID);
        if (event.getType() == StorageObjectEvent.Type.CREATED) {
            requireValues(metadata,
                    ContentStore.CONTENT_CHECKSUM,
                    ContentStore.CONTENT_MIMETYPE,
                    ContentStore.CONTENT_MODIFIED,
                    ContentStore.CONTENT_SIZE);
            fedoraObjectStore.ingest(getFedoraObject(
                    event.getId(), pid, contentURL, metadata));
        } else {
            fedoraObjectStore.purge(pid);
        }
    }

    protected FedoraObject getFedoraObject(String eventId, String pid,
            String contentURL, Map<String, String> metadata) {
        FedoraObject fedoraObject = new FedoraObject();
        fedoraObject.pid(pid);
        fedoraObject.label(contentURL);
        fedoraObject.putDatastream(getContentDatastream(contentURL, metadata));
        return fedoraObject;
    }
    
    protected Datastream getContentDatastream(String contentURL,
            Map<String, String> metadata) {
        Datastream datastream = new Datastream("CONTENT");
        datastream.controlGroup(ControlGroup.REDIRECT);
        Date date = parseRFC822Date(metadata.get(
                ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("CONTENT.0", date);
        version.contentDigest(new ContentDigest()
                .type("MD5")
                .hexValue(metadata.get(ContentStore.CONTENT_CHECKSUM)));
        version.contentLocation(URI.create(contentURL));
        int i = contentURL.lastIndexOf("/");
        String label = contentURL.substring(i + 1);
        label = label.substring(0, label.indexOf("?"));
        version.label(label);
        version.size(Long.parseLong(metadata.get(ContentStore.CONTENT_SIZE)));
        version.mimeType(metadata.get(ContentStore.CONTENT_MIMETYPE));
        datastream.versions().add(version);
        return datastream;
    }

    private static void requireValues(Map<String, String> map, String...keys) {
        for (String key : keys) {
            String value = map.get(key);
            if (value == null || value.length() == 0) {
                throw new IllegalArgumentException("Value not specified for '" +
                        key + "'");
            }
        }
    }

    private static Date parseRFC822Date(String contentDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(RFC822_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(contentDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date is malformed: " +
                    contentDate);
        }
    }
}
