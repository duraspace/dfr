package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.*;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import org.duraspace.dfr.ocs.core.OCSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * Implementation of {@link FedoraObjectStore} that works with an actual
 * Fedora repository.
 *
 * Note: We need to move the strings to constants or properties. Also, since
 *       Fedora is an endpoint this could be a Camel component (or hook into
 *       one) since it interfaces with an endpoint.
 */
public class FedoraObjectStoreClient implements FedoraObjectStore {

    private static final Logger logger =
        LoggerFactory.getLogger(FedoraObjectStoreClient.class);

    private static final String FOXML11_FORMAT =
            "info:fedora/fedora-system:FOXML-1.1";

    /** A DFR client used to make it easy to interoperate with Fedora */
    private final FedoraClient fedoraClient;

    /**
     * Creates an instance.
     *
     * @param fedoraClient the Fedora client to use, never <code>null</code>.
     */
    public FedoraObjectStoreClient(FedoraClient fedoraClient) {
        if (fedoraClient == null) throw new NullPointerException();
        this.fedoraClient = fedoraClient;
        logger.debug("Constructing a Fedora Object Store Client");
    }

    @Override
    public boolean ingest(FedoraObject object, String logMessage) throws OCSException {

        boolean success = false;

        // This is ugly, ugly, ugly and I take full responsibility. Since we
        // generate the PID, and FDO's are immutable (for now) as far as DfR is
        // concerned.  If the object exists, nothing needs to be done.  Later,
        // we will want to do something better.  DWD.
        if (!checkExistence(object.pid())) {
            logger.debug("Ingesting an object into the repository");
            FedoraResponse response = execute(new Ingest().format(FOXML11_FORMAT)
                .logMessage(logMessage).content(Util.toFOXML(object)));
            logger.debug("Ingest response - " + response);
            success = response != null;
        }

        return success;
    }

    @Override
    public void purge(String pid, String logMessage) throws OCSException {
        logger.debug("Purging an object from the repository");
        execute(new PurgeObject(pid).logMessage(logMessage));
    }
    
    @Override
    public FedoraObject export(String pid) throws OCSException {
        logger.debug("Exporting an object from the repository");
        FedoraResponse response = execute(
                new Export(pid).format(FOXML11_FORMAT).context("migrate"));
        if (response == null) {
            return null;
        } else {
            return Util.fromFOXML(response.getEntityInputStream());
        }
    }

    /**
     * Provides a DfR specific initialization the Fedora Repository.  This
     * consists of loading objects that are required by the DfR if they don't
     * already exist in it.
     */
    public void initialize() {
        logger.debug("Initializing the Fedora Repository");
        if (!checkExistence("si:importedObjects")) {
            logger.debug("The imported objects root does not exist");

            String logMessage = "Initializing the parent for imported objects.";
            Resource contentResource =
                new ClassPathResource("root-imported-objects-collection.xml");
            StringBuffer contentBuffer = new StringBuffer();
            try {
                InputStream is = contentResource.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    contentBuffer.append(line);
                }
                br.close();
            } catch (IOException e) {
                logger.debug("Unable to get imported objects FOXML");
            }

            FedoraResponse response = execute(new Ingest().format(FOXML11_FORMAT)
                .logMessage(logMessage).content(contentBuffer.toString()));
            if (response == null) {
                logger.info("Unable to initialize the repository");
            }
        }
    }

    private boolean checkExistence(String pid) {
        logger.debug("Getting the objects\' profile");
        FedoraResponse response = execute(new GetObjectProfile(pid));
        logger.debug("Response is: " + response);
        return (response != null);
    }

    private FedoraResponse execute(FedoraRequest request) throws OCSException {
        logger.debug("Executing the repository client request");
        try {
            return fedoraClient.execute(request);
        } catch (FedoraClientException e) {
            if (e.getStatus() == 404
                    || e.getMessage().contains("ObjectExistsException")) {
                logger.debug("The Fedora object does not exist");
                return null;
            } else {
                logger.debug("Cannot execute Fedora request");
                throw new OCSException("Error executing Fedora request", e);
            }
        }
    }
}
