package org.duraspace.dfr.sync;

import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DfrSyncDriver {
    private static int port;
    private static String contextPath;
    private static Logger log = LoggerFactory.getLogger(DfrSyncDriver.class);

    public static void main(String[] args) throws Exception {
        try {

            port = Integer.parseInt(System.getProperty("dfr.port", "8888"));
            contextPath = System.getProperty("dfr.contextPath", "/dfr-sync");

            Server srv = new Server(port);
            
            ProtectionDomain protectionDomain = DfrSyncDriver.class.getProtectionDomain();
            String warFile = protectionDomain.getCodeSource().getLocation().toExternalForm();
            log.debug("warfile: {}", warFile);
            WebAppContext context = new WebAppContext();
            context.setContextPath(contextPath);
            context.setWar(warFile);
            srv.setHandler(context);
        
            srv.start();
            srv.join();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}