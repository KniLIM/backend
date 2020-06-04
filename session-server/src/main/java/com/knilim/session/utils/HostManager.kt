package com.knilim.session.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.client.RestTemplate

object HostManager {
    @Value("\${com.knilim.session.isLocal}")
    private val isLocal : Boolean = false

    val host by lazy {
        if (isLocal) {
            "127.0.0.1"
        } else {
            getPublicAddress()
        }
    }

    /**
     * 获取公网ip
     * 额,这个方法有点取巧,但很好用
     * 如果有更好的方法请告知......
     *
     * @return  返回ip的字符串形式
     */
    private fun getPublicAddress(): String {
        val restTemplate = RestTemplate()
        val url = "http://members.3322.org/dyndns/getip"
        val address = restTemplate.getForObject(url, String::class.java)
        return address?.replace('\n', ' ')?.trim { it <= ' ' } ?: ""
    }
}