package com.knilim.service;

import com.knilim.utils.Tuple;

import java.util.UUID;

/**
 * 转发服务接口
 * 通过依赖注入生成实例 forwardService
 *
 * e.g.
 * <p><pre>{@code
 *     class Handler {
 *         \@Reference
 *         ForwardService forwardService;
 *
 *         public void foo() {
 *             forwardService.getAvailableSession();
 *         }
 *     }
 * }</pre></p>
 *
 */
public interface ForwardService {

    /**
     * 获取一个正在运行的 Session Server 的 ip 和 port
     *
     * 利用 Nacos 的负载均衡算法调用 Session Server 上的方法返回客户端要连接的 ip 和 port
     *
     * @return 包含 ip 和 port 的 {@link Tuple} 对象
     *         其中 ip 是 {@link String} 类型，port 是 {@link Integer} 类型
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

    /**
     * 将数据字节流 {@code data} 转发给 userId 为 {@code rcvId} 的用户
     *
     * @param rcvId 接受消息的用户对应的 userId
     * @param data 转发的数据字节流
     *
     * e.g.
     * <p><pre>{@code
     *     UUID rcv = getUser();
     *     Byte[] data = getData();
     *
     *     forwardService.forward(rcv, data);
     * }</pre></p>
     *
     */
    void forward(UUID rcvId, Byte[] data);
}
