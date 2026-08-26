package com.walking.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FudlyMerchantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FudlyMerchantServiceApplication.class, args);
    }
}
