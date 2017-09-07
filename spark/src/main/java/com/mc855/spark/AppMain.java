package com.mc855.spark;

import com.mc855.spark.web.infra.WebServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan
@EnableWebMvc
public class AppMain {

    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.println("Uncaught error from application!");
                e.printStackTrace();
            }
        });

        try {
            WebServer server = new WebServer(AppMain.class).start();

            addShutdownHook(server);

            System.out.println("WebServer started!");

            server.join();

        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static void addShutdownHook(final WebServer server) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.stop();
            }
        }));
    }

}