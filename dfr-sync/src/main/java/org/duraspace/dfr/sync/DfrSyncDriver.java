package org.duraspace.dfr.sync;

import java.io.IOException;
import org.eclipse.jetty.server.Server;

public class DfrSyncDriver
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8888);
        server.setHandler(new DfrSync());

        server.start();
        server.join();
    }
}