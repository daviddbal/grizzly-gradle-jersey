package net.balsoftware.attendance;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
@Slf4j
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/myapp/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.dekses.jersey.docker.demo package
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        final ResourceConfig rc = new ResourceConfig().packages("net.balsoftware.attendance.student");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), new JerseyApplication(), locator);
//        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Initiliazing Grizzly server..");
        // instantiate server

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("Stopping server..");
                server.shutdown();
            }
        }, "shutdownHook"));

        // run
        try {
            server.start();
            log.info("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            log.error("There was an error while starting Grizzly HTTP server.", e);
        }

        // Instead using shutdown hook
//        System.in.read();
//        server.shutdown();
    }
}

