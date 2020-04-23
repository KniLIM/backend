package com.knilim.session;

import com.knilim.service.ForwardService;
import com.knilim.data.utils.Tuple;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Service
@Component
public class ForwardServiceImpl implements ForwardService {

    private String host;

    @Value("${com.knilim.session.port}")
    private Integer port;

    public ForwardServiceImpl() {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            host = "";
        }
    }

    @Override
    public Tuple<String, Integer> getAvailableSession() {
        return new Tuple<>(host, port);
    }
}
