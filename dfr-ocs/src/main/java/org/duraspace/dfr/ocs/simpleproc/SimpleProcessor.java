/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.simpleproc;

import com.github.cwilper.fcrepo.dto.core.*;
import eu.medsea.mimeutil.MimeUtil2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.duracloud.client.ContentStore;
import org.duracloud.common.util.DateUtil;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.duracloud.DuraCloudStorageObject;
import org.duraspace.dfr.ocs.fedora.FedoraObjectStoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
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
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd";
    private static final String STORE_ID = "store-id";
    private static final String SPACE_ID = "space-id";

    /** The Fedora PID namespace to use. */
    private String pidPrefix;

    /** The URL of the DuraStore service containing the content. */
    private String duraStoreURL;

    /** MIME Test Utility */
    // Move to Spring when this class gets refactored. DWD
    private final MimeUtil2 mimeUtil = new MimeUtil2();

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

        // Initialize the MIME type detector
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");

        logger.debug("Constructing a simple processor");
    }

    /**
     * Processor for the incoming <code>StorageObjectEvent</code> called by
     * the execution environment to create a destination
     * <code>FedoraObjectStoreEvent</code> to be consumed by subsequent
     * processing. In this case a new FedoraObject is created and later
     * ingested into a Fedora object store.
     *
     * @param event an incoming <code>StorageObjectEvent</code>
     * @return an outgoing <code>FedoraObjectStoreEvent</code>
     * @throws OCSException if the processing failed
     */
    public FedoraObjectStoreEvent process(StorageObjectEvent event) throws OCSException {
        logger.debug("Processing a storage event");

        FedoraObject fedoraObject;

        DuraCloudStorageObject storageObject =
            (DuraCloudStorageObject) event.getStorageObject();
        String collectionPID = "si:importedObjects";
        Map<String, String> messageMetadata = event.getMetadata();

        requireValues(messageMetadata, "objectId", "objectType", "collectionId");
        String objectId = messageMetadata.get("objectId");
        String collectionId = messageMetadata.get("collectionId");

        if (!collectionId.equals("si:importedObjects")) {
            String collectionURL = duraStoreURL + "/" +
                messageMetadata.get(SPACE_ID) + "/" + collectionId + "?storeID=" +
                messageMetadata.get(STORE_ID);
            logger.info("Collection URL: " + collectionURL);
            collectionPID = pidPrefix + DigestUtils.md5Hex(collectionURL);
        }

        // The encoding of the DuraCloud object and the Fedora external
        // reference URL need to be compatible since Sidora does not
        // use the StoreClient to access DuraCloud content.
        String encodedObjectID;
        try {
            encodedObjectID = URIUtil.encodePath(objectId);
        }
        catch (URIException e) {
            String msg = "Unable to encode the duracloud content location: " + objectId;
            logger.info(msg);
            throw new OCSException(msg + e);
        }

        requireValues(messageMetadata, STORE_ID, SPACE_ID);
        String contentURL = duraStoreURL + "/" +
                messageMetadata.get(SPACE_ID) +
                "/" + encodedObjectID + "?storeID=" +
                messageMetadata.get(STORE_ID);
        logger.debug("Content URL: " + contentURL);
        String objectPID = pidPrefix + DigestUtils.md5Hex(contentURL);

        // Note: It is complaining that is needs the InputStream to support
        // mark() and close() and as far as I know it does. So we will just
        // check MIME based on name. DWD
        //String mimeType =
        //    MimeUtil2.getMostSpecificMimeType(mimeUtil.getMimeTypes(storageObject.getContent())).toString();
        String mimeType =
            MimeUtil2.getMostSpecificMimeType(mimeUtil.getMimeTypes(objectId)).toString();
        logger.debug("Tested MIME - " + mimeType);
        messageMetadata.put("checkedMIMEType", mimeType);

        // Create the Fedora object.
        Map<String, String> metadata = storageObject.getMetadata();
        fedoraObject =
            getFedoraObject(objectPID, collectionPID, contentURL, metadata, messageMetadata);

        // Note: Using the PID as an eventID is not ideal but until we have
        //       an ID generator but it will suffice for the moment. DWD
        FedoraObjectStoreEvent fdoEvent =
            new FedoraObjectStoreEvent(objectPID);
        fdoEvent.setFedoraObject(fedoraObject);
        String logMessage = "Requested by DFR Object Creation Service." +
                " Event ID: " + event.getEventID();
        fdoEvent.getMetadata().put("log-message", logMessage);
        event.getRelatedEvents().add(fdoEvent);

        return fdoEvent;
    }

    private FedoraObject getFedoraObject(String pid,
                                         String collectionPID,
                                         String contentURL,
                                         Map<String, String> metadata,
                                         Map<String, String> messageMetadata) {

        String label = messageMetadata.get("objectId");
        int i = label.lastIndexOf("/");
        label = label.substring(i + 1);
        messageMetadata.put("objectLabel", label);

        FedoraObject fedoraObject = new FedoraObject();
        fedoraObject.pid(pid);
        String objectType = messageMetadata.get("objectType");
        fedoraObject.label(label);
        if (objectType.equals("content")) {
            fedoraObject.putDatastream(getContentDatastream(contentURL,
                metadata, messageMetadata));
        }

        // Note: For demonstration purposes we are only doing the Lido content
        //       model. This is hard coded crap but good enough for the demo.
        //       When this is broken into a Camel path we can add business
        //       logic to support other Fedora (Sidora) objects.
        //       DWD.
        messageMetadata.put("contentModel", "info:fedora/si:lidoCollectionCModel");

        fedoraObject.putDatastream(getRelsDataStream(pid, collectionPID,
            metadata, messageMetadata));
        //fedoraObject.putDatastream(getNcdDataStream(metadata));
        fedoraObject.putDatastream(getLidoDataStream(metadata, messageMetadata));
        fedoraObject.putDatastream(getCollectionPolicyDataStream(metadata));
        fedoraObject.putDatastream(getDuracloudDataStream(metadata, messageMetadata));

        return fedoraObject;
    }

    private Datastream getContentDatastream(String contentURL,
            Map<String, String> metadata, Map<String, String> messageMetadata) {

        requireValues(metadata,
            ContentStore.CONTENT_CHECKSUM,
            ContentStore.CONTENT_MIMETYPE,
            ContentStore.CONTENT_MODIFIED,
            ContentStore.CONTENT_SIZE);

        Datastream datastream = new Datastream("OBJ");
        datastream.controlGroup(ControlGroup.REDIRECT);
        //Date date = parseRFC822Date(metadata.get(
        //        ContentStore.CONTENT_MODIFIED));
        Date date = parseISO8601Date(metadata.get(
            ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("OBJ.0", date);
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

        requireValues(messageMetadata, "objectLabel");
        String label = messageMetadata.get("objectLabel");
        version.label(label);
        version.size(Long.parseLong(metadata.get(ContentStore.CONTENT_SIZE)));
        // Note: This is only as good as what DuraCloud contains.  Since the
        //       JRE content.properties is used for sync, and its weak we
        //       use our own mime characterization. DWD
        //version.mimeType(metadata.get(ContentStore.CONTENT_MIMETYPE));
        version.mimeType(messageMetadata.get("checkedMIMEType"));
        datastream.versions().add(version);

        return datastream;
    }

    private Datastream getRelsDataStream(String pid,
                                         String collectionPID,
                                         Map<String, String> metadata,
                                         Map<String, String> messageMetadata) {

        requireValues(metadata,
            ContentStore.CONTENT_MODIFIED);

        requireValues(messageMetadata, "contentModel");
        String contentModel = messageMetadata.get("contentModel");

        Datastream datastream = new Datastream("RELS-EXT");
        datastream.controlGroup(ControlGroup.INLINE_XML);
        //Date date = parseRFC822Date(metadata.get(
        //    ContentStore.CONTENT_MODIFIED));
        Date date = parseISO8601Date(metadata.get(
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
            "    <fedora:isMemberOfCollection rdf:resource=\"info:fedora/" + collectionPID + "\"></fedora:isMemberOfCollection>\n" +
            "    <fedora-model:hasModel rdf:resource=\"" + contentModel + "\"></fedora-model:hasModel>\n" +
            "    <orginal_metadata xmlns=\"http://islandora.org/ontologies/metadata#\">TRUE</orginal_metadata>\n" +
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

    private Datastream getCollectionPolicyDataStream(Map<String, String> metadata) {

        Datastream datastream = new Datastream("COLLECTION_POLICY");
        datastream.controlGroup(ControlGroup.INLINE_XML);
        //Date date = parseRFC822Date(metadata.get(ContentStore.CONTENT_MODIFIED));
        Date date = parseISO8601Date(metadata.get(
            ContentStore.CONTENT_MODIFIED));

        DatastreamVersion version = new DatastreamVersion("COLLECTION_POLICY.0", date);

        String inlineXML =
            "<collection_policy xmlns=\"http://www.islandora.ca\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "name=\"\" xsi:schemaLocation=\"http://www.islandora.ca http://syn.lib.umanitoba.ca/collection_policy.xsd\">\n" +
                "  <content_models>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Animal or plant images\" namespace=\"si:\" pid=\"si:imageCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Digitized text, with page images\" namespace=\"si:\" pid=\"si:fieldbookCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Tabular datasets\" namespace=\"si:\" pid=\"si:datasetCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"General image\" namespace=\"si:\" pid=\"si:generalImageCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"General image\" namespace=\"si:\" pid=\"si:simpleImageCModel\"></content_model>\n" +
                "  </content_models>\n" +
                "  <concept_models>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Project or sub-project\" namespace=\"si:\" pid=\"si:projectCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Natural history collection\" namespace=\"si:\" pid=\"si:ncdCollectionCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Research site, plot or area\" namespace=\"si:\" pid=\"si:ctPlotCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Person\" namespace=\"si:\" pid=\"si:peopleCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Organization or Institution\" namespace=\"si:\" pid=\"si:organizationCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Expedition\" namespace=\"si:\" pid=\"si:expeditionCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Camera trap\" namespace=\"si:\" pid=\"si:cameraTrapCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Animal or plant species\" namespace=\"si:\" pid=\"si:dwcCModel\"></content_model>\n" +
                "    <content_model dsid=\"ISLANDORACM\" name=\"Cultural Heritage Entity or Object\" namespace=\"si:\" pid=\"si:lidoCollectionCModel\"></content_model>\n" +
                "  </concept_models>\n" +
                "  <search_terms>\n" +
                "    <term field=\"dc.title\">dc.title</term>\n" +
                "    <term field=\"dc.creator\">dc.creator</term>\n" +
                "    <term default=\"true\" field=\"dc.description\">dc.description</term>\n" +
                "    <term field=\"dc.date\">dc.date</term>\n" +
                "    <term field=\"dc.identifier\">dc.identifier</term>\n" +
                "    <term field=\"dc.language\">dc.language</term>\n" +
                "    <term field=\"dc.publisher\">dc.publisher</term>\n" +
                "    <term field=\"dc.rights\">dc.rights</term>\n" +
                "    <term field=\"dc.subject\">dc.subject</term>\n" +
                "    <term field=\"dc.relation\">dc.relation</term>\n" +
                "    <term field=\"dcterms.temporal\">dcterms.temporal</term>\n" +
                "    <term field=\"dcterms.spatial\">dcterms.spatial</term>\n" +
                "    <term field=\"fgs.DS.first.text\">Full Text</term>\n" +
                "  </search_terms>\n" +
                "  <relationship>isMemberOfCollection</relationship>\n" +
                "</collection_policy>";

        try {

            version.inlineXML(new InlineXML(inlineXML));
            version.label("Collection Policy");
            version.size((long) inlineXML.length());
            version.mimeType("text/xml");
            datastream.versions().add(version);

        } catch (IOException e) {
            logger.info("Could not create XML for Collection Policy");
        }

        return datastream;

    }

    private Datastream getNcdDataStream(Map<String, String> metadata) {

        Datastream datastream = new Datastream("NCD");
        datastream.controlGroup(ControlGroup.INLINE_XML);
        //Date date = parseRFC822Date(metadata.get(ContentStore.CONTENT_MODIFIED));
        Date date = parseISO8601Date(metadata.get(
            ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("NCD.0", date);

        String inlineXML =
            "<RecordSet xmlns=\"http://rs.tdwg.org/ncd/0.70\" " +
            "xmlns:ns0=\"http://rs.tdwg.org/ncd/0.70\" " +
            "xmlns:ncd=\"http://rs.tdwg.org/ncd/0.70\" " +
            "xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
            "  <Collections>\n" +
            "    <Collection>\n" +
            "      <CollectionId/>\n" +
            "      <AboutThisRecord>\n" +
            "        <dc_source/>\n" +
            "        <dc_identifier/>\n" +
            "        <dct_created/>\n" +
            "        <dct_modified/>\n" +
            "        <dct_harvested/>\n" +
            "      </AboutThisRecord>\n" +
            "      <AlternativeIds>\n" +
            "        <Identifier id=\"\" source=\"Smithsonian\"/>\n" +
            "      </AlternativeIds>\n" +
            "      <DescriptiveGroup>\n" +
            "        <dc_title>Still more stuff 1</dc_title>\n" +
            "        <dc_alternative_title/>\n" +
            "        <dc_description/>\n" +
            "        <dc_extent/>\n" +
            "        <Notes/>\n" +
            "      </DescriptiveGroup>\n" +
            "      <AdministrativeGroup>\n" +
            "        <FormationPeriod/>\n" +
            "        <dc_format/>\n" +
            "        <dc_medium/>\n" +
            "        <UsageRestriction/>\n" +
            "        <Contact/>\n" +
            "        <Owner/>\n" +
            "        <PhysicalLocation xlink:href=\"\"/>\n" +
            "      </AdministrativeGroup>\n" +
            "      <KeywordsGroup>\n" +
            "        <KingdomCoverage/>\n" +
            "        <TaxonCoverage/>\n" +
            "        <CommonNameCoverage/>\n" +
            "        <GeospatialCoverage/>\n" +
            "        <ExpeditionName/>\n" +
            "        <Collector/>\n" +
            "        <AssociatedAgent/>\n" +
            "        <dc_title/>\n" +
            "      </KeywordsGroup>\n" +
            "      <RelatedMaterialsGroup>\n" +
            "        <dc_relation/>\n" +
            "        <RelatedCollection/>\n" +
            "      </RelatedMaterialsGroup>\n" +
            "    </Collection>\n" +
            "  </Collections>\n" +
            "</RecordSet>";

        try {

            version.inlineXML(new InlineXML(inlineXML));
            version.label("NCD Record");
            version.size((long) inlineXML.length());
            version.mimeType("text/xml");
            datastream.versions().add(version);

        } catch (IOException e) {
            logger.info("Could not create XML for NCD Record");
        }

        return datastream;

    }

    private Datastream getLidoDataStream(Map<String, String> metadata,
                                         Map<String, String> messageMetadata) {

        Datastream datastream = new Datastream("LIDO");
        datastream.controlGroup(ControlGroup.INLINE_XML);
        //Date date = parseRFC822Date(metadata.get(ContentStore.CONTENT_MODIFIED));
        Date date = parseISO8601Date(metadata.get(
            ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("LIDO.0", date);

        requireValues(messageMetadata, "objectLabel");
        String label = messageMetadata.get("objectLabel");

        String inlineXML =
            "<lido xmlns=\"http://www.lido-schema.org\" " +
                  "xmlns:lido=\"http://www.lido-schema.org\" " +
                  "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n " +
            "  <lidoRecID type=\"local\">BLANK</lidoRecID>\n" +
            "  <descriptiveMetadata xml:lang=\"eng\">\n" +
            "    <objectClassificationWrap>\n" +
            "      <objectWorkTypeWrap>\n" +
            "        <objectWorkType>\n" +
            "          <term>import</term>\n" +
            "        </objectWorkType>\n" +
            "      </objectWorkTypeWrap>\n" +
            "      <classificationWrap>\n" +
            "        <classification>\n" +
            "          <term/>\n" +
            "        </classification>\n" +
            "      </classificationWrap>\n" +
            "    </objectClassificationWrap>\n" +
            "    <objectIdentificationWrap>\n" +
            "      <titleWrap>\n" +
            "        <titleSet>\n" +
            "          <appellationValue>" + label + "</appellationValue>\n" +
            "        </titleSet>\n" +
            "        <titleSet/>\n" +
            "        <titleSet/>\n" +
            "      </titleWrap>\n" +
            "      <inscriptionsWrap>\n" +
            "        <inscriptions>\n" +
            "          <inscriptionTranscription/>\n" +
            "            <inscriptionDescription>\n" +
            "              <descriptiveNoteValue/>\n" +
            "            </inscriptionDescription>\n" +
            "        </inscriptions>\n" +
            "      </inscriptionsWrap>\n" +
            "      <repositoryWrap>\n" +
            "        <repositorySet>\n" +
            "          <repositoryName>\n" +
            "            <legalBodyName>\n" +
            "              <appellationValue/>\n" +
            "            </legalBodyName>\n" +
            "          </repositoryName>\n" +
            "          <workID/>\n" +
            "          <repositoryLocation>\n" +
            "            <placeID type=\"local\"/>\n" +
            "            <namePlaceSet>\n" +
            "              <appellationValue/>\n" +
            "            </namePlaceSet>\n" +
            "          </repositoryLocation>\n" +
            "        </repositorySet>\n" +
            "      </repositoryWrap>\n" +
            "      <objectDescriptionWrap>\n" +
            "        <objectDescriptionSet type=\"General\">\n" +
            "          <descriptiveNoteValue/>\n" +
            "        </objectDescriptionSet>\n" +
            "        <objectDescriptionSet type=\"context\">\n" +
            "          <descriptiveNoteValue/>\n" +
            "        </objectDescriptionSet>\n" +
            "        <objectDescriptionSet type=\"materials\">\n" +
            "          <descriptiveNoteValue/>\n" +
            "         </objectDescriptionSet>\n" +
            "         <objectDescriptionSet type=\"ornament\">\n" +
            "           <descriptiveNoteValue/>\n" +
            "         </objectDescriptionSet>\n" +
            "         <objectDescriptionSet type=\"symbolism\">\n" +
            "           <descriptiveNoteValue/>\n" +
            "        </objectDescriptionSet>\n" +
            "      </objectDescriptionWrap>\n" +
            "      <objectMeasurementsWrap>\n" +
            "        <objectMeasurementsSet>\n" +
            "          <displayObjectMeasurements/>\n" +
            "          <objectMeasurements>\n" +
            "            <measurementsSet>\n" +
            "              <measurementType>height</measurementType>\n" +
            "              <measurementUnit>centimeter</measurementUnit>\n" +
            "              <measurementValue/>\n" +
            "            </measurementsSet>\n" +
            "          </objectMeasurements>\n" +
            "        </objectMeasurementsSet>\n" +
            "      </objectMeasurementsWrap>\n" +
            "    </objectIdentificationWrap>\n" +
            "      <eventWrap>\n" +
            "        <eventSet>\n" +
            "          <event>\n" +
            "          <eventType>\n" +
            "            <term>Production</term>\n" +
            "          </eventType>\n" +
            "          <eventPlace>\n" +
            "            <place>\n" +
            "              <placeID type=\"local\"/>\n" +
            "              <namePlaceSet>\n" +
            "                <appellationValue/>\n" +
            "              </namePlaceSet>\n" +
            "              <placeClassification>\n" +
            "                <term/>\n" +
            "              </placeClassification>\n" +
            "            </place>\n" +
            "          </eventPlace>\n" +
            "        </event>\n" +
            "      </eventSet>\n" +
            "    </eventWrap>\n" +
            "  </descriptiveMetadata>\n" +
            "  <administrativeMetadata xml:lang=\"eng\">\n" +
            "    <recordWrap>\n" +
            "      <recordID type=\"local\">BLANK</recordID>\n" +
            "      <recordType>\n" +
            "        <term>BLANK</term>\n" +
            "      </recordType>\n" +
            "      <recordSource>\n" +
            "        <legalBodyName>\n" +
            "          <appellationValue>BLANK</appellationValue>\n" +
            "        </legalBodyName>\n" +
            "      </recordSource>\n" +
            "    </recordWrap>\n" +
            "  </administrativeMetadata>\n" +
            "</lido>";

        try {

            version.inlineXML(new InlineXML(inlineXML));
            version.label("LIDO Record");
            version.size((long) inlineXML.length());
            version.mimeType("text/xml");
            datastream.versions().add(version);

        } catch (IOException e) {
            logger.info("Could not create XML for LIDO Record");
        }

        return datastream;

    }

    private Datastream getDuracloudDataStream(Map<String, String> metadata,
                                              Map<String, String> messageMetadata) {

        Datastream datastream = new Datastream("DCOM");
        datastream.controlGroup(ControlGroup.INLINE_XML);
        //Date date = parseRFC822Date(metadata.get(ContentStore.CONTENT_MODIFIED));
        Date date = parseISO8601Date(metadata.get(
            ContentStore.CONTENT_MODIFIED));
        DatastreamVersion version = new DatastreamVersion("DCOM.0", date);

        String inlineXML = "<dcom>\n";

        for (Map.Entry<String, String> entry : metadata.entrySet())
        {
            inlineXML =
                inlineXML +
                    "  <" + entry.getKey() + ">" +
                    entry.getValue() +
                    "</" + entry.getKey() + ">\n";
        }

        inlineXML = inlineXML + "</dcom>";

        try {

            version.inlineXML(new InlineXML(inlineXML));
            version.label("DCOM Record");
            version.size((long) inlineXML.length());
            version.mimeType("text/xml");
            datastream.versions().add(version);

        } catch (IOException e) {
            logger.info("Could not create XML for Duracloud Metadata Record");
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

    private static Date parseISO8601Date(String contentDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(ISO8601_DATE_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(contentDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date is malformed: " +
                contentDate);
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

//    public static String prettyLabel(String inLabel) {
//        // Don't care if they send in a null
//        if (inLabel == null) { return inLabel; }
//        String label = inLabel;
//        try {
//            // If its a URL, we only want the last part.
//            URL labelURL = new URL(inLabel);
//            String labelPath = labelURL.getPath();
//            int lastSlash = labelPath.lastIndexOf("/") + 1;
//            label = labelPath.substring(lastSlash);
//            //int extension = label.lastIndexOf(".");
//            //label = label.substring(0, extension);
//        } catch (MalformedURLException e) {
//            // Don't care
//        }
//
//        return label;
//    }
//
//    private static String encodeDuraCloudID(String objectID) {
//
//        String encodedObjectID = "";
//
//        // In DuraCloud the splits are delimited by slashes.
//        String[] splits = objectID.split("/");
//        int size = splits.length;
//
//        // For each collection level
//        for (int i = 0; i < (size); i++) {
//
//            // Each split (in this loop) represents a collection
//            String level = splits[i];
//            logger.info("level: " + level);
//
//            // Add an unencoded delimiter after each collection level.
//            if (i > 0) { encodedObjectID = encodedObjectID + "/"; }
//
//            try {
//                encodedObjectID =
//                    encodedObjectID + URLEncoder.encode(level, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                String msg = "Unable to encode the content location: " + level;
//                logger.info(msg);
//                throw new OCSException(msg + e);
//            }
//
//        }
//
//       return encodedObjectID;
//    }

}
