package com.comsysto.movie.jetty;

public class JettyStart {

    public static void main(final String[] args) {

        int port = 8080;

        if (args.length < 1) {
            System.out.println("JettyStart <httpport>");
        }
        else {
            port = Integer.valueOf(args[0]);
        }


        System.setProperty("spring.profiles.active", "default");

        JettyStarterApplication jettyStarter = new JettyStarterApplication(port);
        jettyStarter.startServer();
    }
}
