package org.duraspace.dfr.ocs.simpleproc;

import com.github.cwilper.fcrepo.dto.core.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.duracloud.client.ContentStore;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.duracloud.DuraCloudStorageObject;
import org.duraspace.dfr.ocs.fedora.FedoraObjectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Creates a basic, rather hard coded, Fedora object based on an incoming
 * <code>StorageObjectEvent</code>. It creates a simple wrapper Fedora object
 * for each storage bytestream. The new <code>FedoraObject</code> is returned
 * for subsequent handling.
 * <p>
 * An external Fedora datastream with id <code>CONTENT</code> will be created
 * in the Fedora object. This will reference the content inside DuraCloud
 * via URL. This is for the content object only.
 * <p>
 * <p>
 * A RELS-EXT datastream is also created. For all objects it points to its
 * collection object up to a pre-defined default. A relationship to a content
 * model is asserted depending on the type of object.
 * </p>
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
public class SimpleProcessor {

    private static final Logger logger =
        LoggerFactory.getLogger(SimpleProcessor.class);

    private static final String RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String STORE_ID = "store-id";
    private static final String SPACE_ID = "space-id";

    /** The Fedora PID namespace to use. */
    private String pidPrefix;

    /** The URL of the DuraStore service containing the content. */
    private String duraStoreURL;

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

        logger.debug("Constructing a simple processor");
    }

    public FedoraObjectEvent process(StorageObjectEvent event) throws OCSException {
        logger.debug("Processing a storage event");

        FedoraObject fedoraObject;

        DuraCloudStorageObject storageObject =
            (DuraCloudStorageObject) event.getStorageObject();
        String collectionPID = "si:importedObjects";
        Map<String, String> messageMetadata = event.getMetadata();

        requireValues(messageMetadata, "objectId", "objectType", "collectionId");
        String objectId = messageMetadata.get("objectId");
        // Note: objectType is no longer used in processor code. DWD
        //String objectType = messageMetadata.get("objectType");
        String collectionId = messageMetadata.get("collectionId");

        if (!collectionId.equals("si:importedObjects")) {
            String collectionURL = duraStoreURL + "/" +
                messageMetadata.get(SPACE_ID) + "/" + collectionId + "?storeID=" +
                messageMetadata.get(STORE_ID);
            logger.info("Collection URL: " + collectionURL);
            collectionPID = pidPrefix + DigestUtils.md5Hex(collectionURL);
        }

        requireValues(messageMetadata, STORE_ID, SPACE_ID);
        String contentURL = duraStoreURL + "/" +
                messageMetadata.get(SPACE_ID) + "/" + objectId + "?storeID=" +
                messageMetadata.get(STORE_ID);
        logger.info("Content URL: " + contentURL);
        String objectPID = pidPrefix + DigestUtils.md5Hex(contentURL);

        // Create the Fedora object.
        Map<String, String> metadata = storageObject.getMetadata();
        fedoraObject =
            getFedoraObject(objectPID, collectionPID, contentURL, metadata, messageMetadata);

        // Note: Using the PID as an eventID is not ideal but until we have
        //       an ID generator but it will suffice for the moment. DWD
        FedoraObjectEvent fdoEvent =
            new FedoraObjectEvent(objectPID, fedoraObject);
        String logMessage = "Requested by DFR Object Creation Service." +
                " Event ID: " + event.getEventID();
        fdoEvent.getMetadata().put("log-message", logMessage);
        event.getRelatedEvents().add(fdoEvent);

        return fdoEvent;
    }

    private FedoraObject getFedoraObject(String pid, String collectionPID,
                                         String contentURL,
                                         Map<String, String> metadata,
                                         Map<String, String> messageMetadata) {
        String label = prettyLabel(contentURL);
        FedoraObject fedoraObject = new FedoraObject();
        fedoraObject.pid(pid);
        String objectType = messageMetadata.get("objectType");
        fedoraObject.label(label);
        if (objectType.equals("content")) {
            fedoraObject.putDatastream(getContentDatastream(contentURL, metadata, messageMetadata));
        }
        fedoraObject.putDatastream(getRelsDataStream(pid, collectionPID, metadata, messageMetadata));
        //fedoraObject.putDatastream(getNcdDataStream(metadata));
        return fedoraObject;
    }

    private Datastream getContentDatastream(String contentURL,
            Map<String, String> metadata, Map<String, String> messageMetadata) {

        requireValues(metadata,
            ContentStore.CONTENT_CHECKSUM,
            ContentStore.CONTENT_MIMETYPE,
            ContentStore.CONTENT_MODIFIED,
            ContentStore.CONTENT_SIZE);

        Datastream datastream = new Datastream("CONTENT");
        datastream.controlGroup(ControlGroup.REDIRECT);
        Date date = parseRFC822Date(metadata.get(
                ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("CONTENT.0", date);
        // TODO: Activate this after Fedora has been updated to either
        //       not require auto-validating checksums for externals
        //       datastreams or to allow configuration of credentials for
        //       actually getting remote datastreams.
        //       See https://jira.duraspace.org/browse/FCREPO-752
        //       and https://jira.duraspace.org/browse/FCREPO-748
        //version.contentDigest(new ContentDigest()
        //        .type("MD5")
        //        .hexValue(metadata.get(ContentStore.CONTENT_CHECKSUM)));
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

    private Datastream getRelsDataStream(String pid,
                                         String collectionPID,
                                         Map<String, String> metadata,
                                         Map<String, String> messageMetadata) {

        requireValues(metadata,
            ContentStore.CONTENT_MODIFIED);

        Datastream datastream = new Datastream("RELS-EXT");
        datastream.controlGroup(ControlGroup.INLINE_XML);
        Date date = parseRFC822Date(metadata.get(
            ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("RELS-EXT.0", date);
        // TODO: Activate this after Fedora has been updated to either
        //       not require auto-validating checksums for externals
        //       datastreams or to allow configuration of credentials for
        //       actually getting remote datastreams.
        //       See https://jira.duraspace.org/browse/FCREPO-752
        //       and https://jira.duraspace.org/browse/FCREPO-748
        //version.contentDigest(new ContentDigest()
        //        .type("MD5")
        //        .hexValue(metadata.get(ContentStore.CONTENT_CHECKSUM)));
        String inlineXML =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
            "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" " +
            "xmlns:fedora=\"info:fedora/fedora-system:def/relations-external#\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
            "xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" " +
            "xmlns:fedora-model=\"info:fedora/fedora-system:def/model#\">\n";
        inlineXML = inlineXML +
            "<rdf:Description rdf:about=\"info:fedora/" + pid + "\">\n";
        inlineXML = inlineXML +
            "<fedora:isMemberOfCollection rdf:resource=\"info:fedora/" + collectionPID + "\"></fedora:isMemberOfCollection>\n" +
            "<fedora-model:hasModel rdf:resource=\"info:fedora/si:projectCModel\"></fedora-model:hasModel>\n" +
            "</rdf:Description>\n" +
            "</rdf:RDF>";

        logger.debug(inlineXML);

        try {
            version.inlineXML(new InlineXML(inlineXML));
            version.label("RDF Statements about this Object");
            version.size((long) inlineXML.length());
            version.mimeType("application/rdf+xml");
            datastream.versions().add(version);
        } catch (IOException e) {
            logger.info("Could not create XML for RELS-EXT");
        }

        return datastream;
    }

    private Datastream getNcdDataStream(Map<String, String> metadata) {

        Datastream datastream = new Datastream("NCD");
        datastream.controlGroup(ControlGroup.INLINE_XML);
        Date date = parseRFC822Date(metadata.get(
            ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("NCD.0", date);

        String inlineXML =
            "<RecordSet xmlns=\"http://rs.tdwg.org/ncd/0.70\" " +
            "xmlns:ns0=\"http://rs.tdwg.org/ncd/0.70\" " +
            "xmlns:ncd=\"http://rs.tdwg.org/ncd/0.70\" " +
            "xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n";
        inlineXML = inlineXML +
            "<Collections>\n" +
            "  <Collection>\n" +
            "    <CollectionId/>\n" +
            "    <AboutThisRecord>\n" +
            "      <dc_source/>\n" +
            "      <dc_identifier/>\n" +
            "      <dct_created/>\n" +
            "      <dct_modified/>\n" +
            "      <dct_harvested/>\n" +
            "    </AboutThisRecord>\n" +
            "    <AlternativeIds>\n" +
            "      <Identifier id=\"\" source=\"Smithsonian\"/>\n" +
            "    </AlternativeIds>\n" +
            "    <DescriptiveGroup>\n" +
            "      <dc_title>Still more stuff 1</dc_title>\n" +
            "      <dc_alternative_title/>\n" +
            "      <dc_description/>\n" +
            "      <dc_extent/>\n" +
            "      <Notes/>\n" +
            "    </DescriptiveGroup>\n" +
            "    <AdministrativeGroup>\n" +
            "      <FormationPeriod/>\n" +
            "      <dc_format/>\n" +
            "      <dc_medium/>\n" +
            "      <UsageRestriction/>\n" +
            "      <Contact/>\n" +
            "      <Owner/>\n" +
            "      <PhysicalLocation xlink:href=\"\"/>\n" +
            "    </AdministrativeGroup>\n" +
            "    <KeywordsGroup>\n" +
            "      <KingdomCoverage/>\n" +
            "      <TaxonCoverage/>\n" +
            "      <CommonNameCoverage/>\n" +
            "      <GeospatialCoverage/>\n" +
            "      <ExpeditionName/>\n" +
            "      <Collector/>\n" +
            "      <AssociatedAgent/>\n" +
            "      <dc_title/>\n" +
            "    </KeywordsGroup>\n" +
            "    <RelatedMaterialsGroup>\n" +
            "      <dc_relation/>\n" +
            "      <RelatedCollection/>\n" +
            "    </RelatedMaterialsGroup>\n" +
            "  </Collection>\n" +
            "</Collections>\n" +
            "</RecordSet>";

        try {
            version.inlineXML(new InlineXML(inlineXML));
            version.label("NCD Record");
            version.size((long) inlineXML.length());
            version.mimeType("application/rdf+xml");
            datastream.versions().add(version);
        } catch (IOException e) {
            logger.info("Could not create XML for RELS-EXT");
        }

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

    public static String prettyLabel(String inLabel) {
        // Don't care if they send in a null
        if (inLabel == null) { return inLabel; }
        String label = inLabel;
        try {
            // If its a URL, we only want the last part.
            URL labelURL = new URL(inLabel);
            String labelPath = labelURL.getPath();
            int lastSlash = labelPath.lastIndexOf("/") + 1;
            label = labelPath.substring(lastSlash);
        } catch (MalformedURLException e) {
            // Don't care
        }

        return label;
    }

}
