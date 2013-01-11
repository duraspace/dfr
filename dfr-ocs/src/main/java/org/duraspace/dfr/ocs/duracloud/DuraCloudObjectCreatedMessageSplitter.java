/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.duracloud;

import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
     * @param event the storage object event event from the incoming message
     * @return a list containing each part split
     */
    public List<StorageObjectEvent> splitBody(StorageObjectEvent event) {

        logger.debug("Splitting the event: " + event);

        // A list of events for objects to create
        List<StorageObjectEvent> answer = new ArrayList<>();

        // The original storage object from the event
        DuraCloudStorageObject storageObject =
            (DuraCloudStorageObject) event.getStorageObject();

        // The original event is always returned as the the first split.
        answer.add(event);

        // In DuraCloud the splits are delimited by slashes.
        String[] splits = storageObject.getId().split("/");
        int size = splits.length;

        // Record the rightmost split, equivalent to the original filename.
        event.getMetadata().put("objectBase", splits[size - 1]);
        System.out.println("Object Base - " + splits[size - 1]);

        // If there are at least two level (n level collection and one original)?
        if (size >= 2) {

            // Tell the original event about its collection
            event.getMetadata().put("collectionId", splits[size - 2]);

            // For each collection level
            for (int i = 0; i < (size - 1); i++) {

                // Each split (in this loop) represents a collection
                String collectionId = splits[i];
                logger.info("collectionId: " + collectionId);

                // Defensively copy the original event and make the needed
                // modifications.
                StorageObjectEvent splitEvent =
                    new StorageObjectEvent(event.getEventID(),
                        event.getEventType(), storageObject);
                Map<String, String> eventMetadata = event.getMetadata();
                splitEvent.getMetadata().putAll(eventMetadata);
                splitEvent.getMetadata().put("collectionId", "si:importedObjects");
                splitEvent.getMetadata().put("objectId", collectionId);
                splitEvent.getMetadata().put("objectType", "collection");

                answer.add(splitEvent);

            }

        } else { // else there is only one level (no collection),

            // Add the storage object to the default collection
            event.getMetadata().put("collectionId", "si:importedObjects");

        }

        return answer;
    }

}
