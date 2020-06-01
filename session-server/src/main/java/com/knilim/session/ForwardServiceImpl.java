package com.knilim.session;

import com.knilim.service.ForwardService;
import com.knilim.data.utils.Tuple;
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

    @Value("${com.knilim.session.isLocal}")
    private Boolean isLocal;

    @Override
    public Tuple<String, Integer> getAvailableSession() {
        String host;
        try {
            if(isLocal) {
                host = "127.0.0.1";
            } else {
                host = getPublicAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
            host = "";
        }
        return new Tuple<>(host, port);
    }

    /**
     * 获取公网ip
     * 额,这个方法有点取巧,但很好用
     * 如果有更好的方法请告知......
     *
     * @return  返回ip的字符串形式
     */
    private String getPublicAddress() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://members.3322.org/dyndns/getip";
        String address =  restTemplate.getForObject(url, String.class);
        if(address != null) {
            return address.replace('\n', ' ').trim();
        } else {
            return "";
        }
    }
}
