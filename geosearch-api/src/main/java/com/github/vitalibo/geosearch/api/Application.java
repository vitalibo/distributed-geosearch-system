package com.github.vitalibo.geosearch.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .properties("spring.config.name=default-application,application")
            .sources(Application.class)
            .run(args);
    }

}
