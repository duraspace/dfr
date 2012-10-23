package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Splits the incoming create object message into a message for the objects
 * needed for subsequent processing.  In this case, one DuraCloud message is
 * made for the content and one for each original collection item in the
 * original order (i.e. duplicating the original directory/file system).
 */
public class DuraCloudObjectCreatedMessageSplitter {

    private static final Logger logger = LoggerFactory.getLogger(
        DuraCloudObjectCreatedMessageSplitter.class);

    /**
     * The split body method returns something that is iteratable of
     * <code>StorageObjectEvent</code> including the original plus one
     * for every collection object name in parsed from the path section
     * of the DuraCloud object name.
     *
     * @param body the storage object event event from the incoming message
     * @return a list containing each part split
     */
    public List<StorageObjectEvent> splitBody(StorageObjectEvent body) {

        logger.debug("Splitting the event: " + body);

        // A list of events for objects to create
        List<StorageObjectEvent> answer = new ArrayList<StorageObjectEvent>();

        // The original storage object from the event
        DuraCloudStorageObject dcso =
            (DuraCloudStorageObject) body.getStorageObject();
        String contentId = dcso.getId();

        // The original body is always returned as the the first split.
        answer.add(body);

        // In DuraCloud the splits are delimited by slashes.
        String[] splits = contentId.split("/");
        int size = splits.length;

        // If there are at least two level (n level collection and one original)?
        if (size >= 2) {

            // Tell the original object about its collection
            dcso.getMessageMetadata().put("collectionId", splits[size - 2]);

            // For each collection level
            for (int i = 0; i < (size - 1); i++) {

                String collectionId = splits[i];
                logger.info("collectionId: " + collectionId);

                // Copy the original event and make the needed modifications.
                ContentStore contentStore = dcso.getContentStore();
                String spaceId = dcso.getSpaceId();
                Map<String, String> messageMetadata = dcso.getMessageMetadata();
                Map<String, String> messageMetadataCopy =
                    new HashMap<String, String>();
                messageMetadataCopy.putAll(messageMetadata);

                DuraCloudStorageObject splitObject =
                    new DuraCloudStorageObject(contentStore, spaceId, contentId,
                        messageMetadataCopy, dcso.getType());
                splitObject.getMessageMetadata().put("collectionId", "si:importedObjects");
                splitObject.getMessageMetadata().put("objectId", collectionId);
                splitObject.getMessageMetadata().put("objectType", "collection");
                StorageObjectEvent splitBody =
                    new StorageObjectEvent(body.getId(), body.getType(), splitObject);
                answer.add(splitBody);

            }

        } else { // else there is only one level (no collection),

            // add the content object to the default collection
            dcso.getMessageMetadata().put("collectionId", "si:importedObjects");

        }

        return answer;
    }

}
