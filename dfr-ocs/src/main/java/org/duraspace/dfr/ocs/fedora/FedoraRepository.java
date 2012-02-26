package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.Export;
import com.yourmediashelf.fedora.client.request.FedoraRequest;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import org.duraspace.dfr.ocs.core.FedoraObjectStore;
import org.duraspace.dfr.ocs.core.OCSException;

/**
 * Implementation of {@link FedoraObjectStore} that works with an actual
 * Fedora repository.
 */
public class FedoraRepository implements FedoraObjectStore {
    private static final String FOXML11_FORMAT =
            "info:fedora/fedora-system:FOXML-1.1";

    private final FedoraClient fedoraClient;

    /**
     * Creates an instance.
     *
     * @param fedoraClient the Fedora client to use, never <code>null</code>.
     */
    public FedoraRepository(FedoraClient fedoraClient) {
        if (fedoraClient == null) throw new NullPointerException();
        this.fedoraClient = fedoraClient;
    }

    @Override
    public boolean ingest(FedoraObject object, String logMessage) throws OCSException {
        FedoraResponse response = execute(new Ingest().format(FOXML11_FORMAT)
                .logMessage(logMessage).content(Util.toFOXML(object)));
        return response != null;
    }

    @Override
    public void purge(String pid, String logMessage) throws OCSException {
        execute(new PurgeObject(pid).logMessage(logMessage));
    }
    
    @Override
    public FedoraObject export(String pid) throws OCSException {
        FedoraResponse response = execute(
                new Export(pid).format(FOXML11_FORMAT).context("migrate"));
        if (response == null) {
            return null;
        } else {
            return Util.fromFOXML(response.getEntityInputStream());
        }
    }
    
    private FedoraResponse execute(FedoraRequest request) throws OCSException {
        try {
            return fedoraClient.execute(request);
        } catch (FedoraClientException e) {
            if (e.getStatus() == 404
                    || e.getMessage().contains("ObjectExistsException")) {
                return null;
            } else {
                throw new OCSException("Error executing Fedora request", e);
            }
        }
    }
}
