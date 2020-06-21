package com.knilim.session.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class HostManager {
    private HostManager() {}
    private static class LazyHolder {
        private static final HostManager INSTANCE = new HostManager();
    }
    public static HostManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Value("${com.knilim.session.isLocal}")
    private Boolean isLocal = false;

    private String host = null;

    public String getHost() {
        if(host == null) host = getPublicAddress();
        return host;
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
        String address = restTemplate.getForObject(url, String.class);
        if(address == null) return null;
        return address.replace('\n', ' ').trim();
    }
}
