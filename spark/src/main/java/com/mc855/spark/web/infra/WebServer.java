package com.mc855.spark.web.infra;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebServer {

    private Server server;
    private Class<?>[] springConfigClasses;
    private final Integer threadPoolMinThreads;
    private final Integer threadPoolMaxThreads;
    private final Integer httpPort;
    private final Integer maxIdleTime;
    private final Integer acceptQueueSize;
    private AnnotationConfigWebApplicationContext webApplicationContext;

    public WebServer(Class... springConfigClasses) {
        this.springConfigClasses = springConfigClasses;
        this.threadPoolMinThreads = 1;
        this.threadPoolMaxThreads = 20;
        this.httpPort = 17071;
        this.maxIdleTime = 30000;
        this.acceptQueueSize = 20;
    }

    public WebServer start() throws Exception {
        this.server = new Server(this.buildThreadPool());
        this.server.setConnectors(this.buildConnectors(this.server));
        this.server.setHandler(this.buildServletHandler());
        this.server.start();
        return this;
    }

    private ThreadPool buildThreadPool() {
        return new QueuedThreadPool(this.threadPoolMaxThreads, this.threadPoolMinThreads);
    }

    public void join() throws Exception {
        this.server.join();
    }

    public ApplicationContext getApplicationContext() {
        return this.webApplicationContext;
    }

    public void stop() {
        try {
            this.server.stop();
        } catch (Exception var2) {
            throw new RuntimeException("Error stopping WebServer", var2);
        }
    }

    private ServletContextHandler buildServletHandler() {
        ServletContextHandler context = this.buildServletContextHandler();
        this.setupSpringMvc(context);
        return context;
    }

    protected ServletContextHandler buildServletContextHandler() {
        ServletContextHandler context = new ServletContextHandler(1);
        context.setResourceBase("/");
        context.setContextPath("/");
        return context;
    }

    protected void setupSpringMvc(ServletContextHandler servletContextHandler) {
        this.webApplicationContext = new AnnotationConfigWebApplicationContext();
        this.webApplicationContext.register(this.springConfigClasses);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(this.webApplicationContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        servletContextHandler.addServlet(new ServletHolder(dispatcherServlet), "/*");
    }

    private Connector[] buildConnectors(Server server) {
        Integer acceptorsSize = Runtime.getRuntime().availableProcessors();
        ServerConnector connector = this.createServerConnector(server, acceptorsSize);
        connector.setPort(this.httpPort);
        connector.setIdleTimeout((long) this.maxIdleTime);
        connector.setAcceptQueueSize(this.acceptQueueSize);
        return new Connector[] {connector};
    }

    protected ServerConnector createServerConnector(Server server, Integer acceptorsSize) {
        return new ServerConnector(server, acceptorsSize, -1);
    }

}
