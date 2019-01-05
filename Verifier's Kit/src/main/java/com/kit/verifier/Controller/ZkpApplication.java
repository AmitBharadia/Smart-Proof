package com.kit.verifier.Controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class ZkpApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ZkpApplication.class);
        app.setDefaultProperties(Collections.singletonMap("Server.port", "8080"));
        app.run(args);
    }

}
