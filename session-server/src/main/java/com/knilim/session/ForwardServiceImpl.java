package com.knilim.session;

import com.knilim.service.ForwardService;
import com.knilim.data.utils.Tuple;
import com.knilim.session.utils.HostManager;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

@Service
@Component
public class ForwardServiceImpl implements ForwardService {
    @Value("${com.knilim.session.port}")
    private Integer port;

    @Override
    public Tuple<String, Integer> getAvailableSession() {
        return new Tuple<>(HostManager.INSTANCE.getHost(), port);
    }
}
