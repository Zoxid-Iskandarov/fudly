package com.walking.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class FudlyDiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FudlyDiscoveryServiceApplication.class, args);
    }
}
