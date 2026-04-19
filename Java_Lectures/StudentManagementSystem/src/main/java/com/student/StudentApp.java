package com.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MySpringBootApp:
 * ----------------
 * Entry point for a Spring Boot application.
 *
 * @SpringBootApplication is a meta-annotation that combines:
 *  1. @SpringBootConfiguration → Specialized form of @Configuration, marks this class as a source of bean definitions
 *  2. @EnableAutoConfiguration → Enables Spring Boot’s auto-configuration based on classpath and properties.
 *  3. @ComponentScan → Scans the package and subpackages for Spring components.
 */
@SpringBootApplication
public class StudentApp {

    private static final Logger logger = LogManager.getLogger(StudentApp.class);

    public static void main(String[] args) {
        logger.info("Starting StudentApp Spring Boot application...");
        ConfigurableApplicationContext ctx = SpringApplication.run(StudentApp.class, args);

        // Log Tomcat URL
        if (ctx instanceof ServletWebServerApplicationContext) {
            ServletWebServerApplicationContext serverCtx = (ServletWebServerApplicationContext) ctx;
            int port = serverCtx.getWebServer().getPort();
            Environment env = ctx.getEnvironment();
            String contextPath = env.getProperty("server.servlet.context-path", "");
            logger.info("Application started at: http://localhost:{}{}", port, contextPath);
        }

        /*
         * BeanLifecycleDemoExample be =  new BeanLifecycleDemoExample();
         * be.destroy();
         */
        /*
         * ConfigurableApplicationContext is interface represent Spring IoC container
         * AnnotationConfigApplicationContext Spring Container concrete implementation 
         */
        logger.debug("Initializing AnnotationConfigApplicationContext with BeanLifecycleDemoExample");
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(BeanLifecycleDemoExample.class);
        logger.info("AnnotationConfigApplicationContext initialized");
        context.close();
        logger.info("ApplicationContext closed. BeanLifecycle shutdown complete.");
    }
}
