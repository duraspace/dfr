package org.duraspace.dfr.sync.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.duracloud.sync.config.SyncToolConfig;

import com.thoughtworks.xstream.XStream;

public class SyncToolConfigSerializer {

    /**
     * 
     * @param syncToolConfigXmlPath
     * @return deserialized config if the xml file is available and valid,
     *         otherwise null
     */
    public static SyncToolConfig deserialize(String syncToolConfigXmlPath)
        throws IOException {
        File f = new File(syncToolConfigXmlPath);
        InputStream fis = new FileInputStream(f);
        XStream xstream = new XStream();
        configure(xstream);
        SyncToolConfig c = (SyncToolConfig)xstream.fromXML(fis);
        return c;
    }
    private static void configure(XStream xstream){
        xstream.alias("syncToolConfig", SyncToolConfig.class);
    }
    public static void serialize(SyncToolConfig syncToolConfig,
                                 String syncToolConfigXmlPath)
        throws IOException {
        XStream xstream = new XStream();
        configure(xstream);
        Writer w = new FileWriter(new File(syncToolConfigXmlPath));
        xstream.toXML(syncToolConfig, w);
    }

}