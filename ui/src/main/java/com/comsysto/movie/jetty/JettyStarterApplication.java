package com.comsysto.movie.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyStarterApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyStarterApplication.class);

    private final Server server;

    public JettyStarterApplication(int port){

        server = new Server(port);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/movie");
        context.setResourceBase("src/main/webapp/");
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
    }

    public void startServer(){
        try {
            LOGGER.info("JETTY SERVER STARTING NOW ...");
            System.out.println("JETTY SERVER STARTING NOW ...");
            server.start();
            server.join();
        } catch (Exception e) {
            LOGGER.error("Jetty Server could not be started", e);
            System.exit(100);
        }
    }

    public void stopServer(){
        server.destroy();
    }

    public boolean isServerStarted(){
        return server.isStarted();
    }

    public boolean isServerFailed(){
        return server.isFailed();
    }
}
