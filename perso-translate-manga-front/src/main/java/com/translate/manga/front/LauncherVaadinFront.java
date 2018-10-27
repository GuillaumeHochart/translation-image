package com.translate.manga.front;

import com.translate.manga.front.context.ApplicationContextProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class LauncherVaadinFront extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LauncherVaadinFront.class, args);
    }
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(LauncherVaadinFront.class);
    }
    @Bean
    public ApplicationContextProvider getAppplicationContext() {
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider();
        return applicationContextProvider;
    }

}