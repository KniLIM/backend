package com.knilim.service;

import com.knilim.data.model.Notification;
import com.knilim.data.utils.Tuple;

import java.util.UUID;

/**
 * 转发服务接口
 */
public interface ForwardService {

    /**
     * 获取一个正在运行的 Session Server 的 host 和 port
     *
     * 利用 Nacos 的负载均衡算法调用 Session Server 上的方法返回客户端要连接的 host 和 port
     *
     * @return 包含 host 和 port 的 {@link Tuple} 对象
     *         其中 host 是 {@link String} 类型，port 是 {@link Integer} 类型
     *
     * e.g.
     * <p><pre>{@code
     *     Tuple<String, Integer> result = forwardService.getAvailableSession();
     *     String line = String.format("ip: %s, port: %d", result.getFirst(), result.getSecond());
     *     System.out.println(line);
     * }
     * </pre></p>
     *
     */
    Tuple<String, Integer> getAvailableSession();

    void addNotification(String userId, Notification notification);
}
