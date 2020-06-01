package com.knilim.relationship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class RelationshipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelationshipServiceApplication.class, args);
    }

}
