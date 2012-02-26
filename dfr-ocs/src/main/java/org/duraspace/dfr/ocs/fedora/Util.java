package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLReader;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLWriter;
import org.duraspace.dfr.ocs.core.OCSException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Package-private utility methods. */
final class Util {
    private Util() { }

    static String toFOXML(FedoraObject o) {
        FOXMLWriter writer = new FOXMLWriter();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.writeObject(o, out);
            out.close();
            return new String(out.toByteArray(), "UTF-8");
        } catch (IOException wontHappen) {
            throw new RuntimeException(wontHappen);
        } finally {
            writer.close();
        }
    }
    
    static FedoraObject fromFOXML(InputStream in) throws OCSException {
        FOXMLReader reader = new FOXMLReader();
        try {
            return reader.readObject(in);
        } catch (IOException e) {
            throw new OCSException("Error reading FOXML stream", e);
        }
    }
}
