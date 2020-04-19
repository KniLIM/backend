package com.knilim.data.utils;

public class Socket {

    private String ip;
    private int port;

    public Socket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Socket(Tuple<String,Integer> socket) {
        this.ip = socket.getFirst();
        this.port = socket.getSecond();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Socket{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
