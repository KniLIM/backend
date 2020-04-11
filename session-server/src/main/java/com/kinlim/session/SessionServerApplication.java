package com.kinlim.session;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class SessionServerApplication {

    @Value("${knilim.session.host}")
    private String host;

    @Value("${knilim.session.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration configuration = new Configuration();
        configuration.setHostname(host);
        configuration.setPort(port);

        SocketIOServer server = new SocketIOServer(configuration);
        server.addNamespace("/sockets");
        return server;
    }

    public static void main(String[] args) {
        SpringApplication.run(SessionServerApplication.class, args);
    }
}
