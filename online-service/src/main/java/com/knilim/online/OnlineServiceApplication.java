package com.knilim.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OnlineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineServiceApplication.class, args);
    }
}
