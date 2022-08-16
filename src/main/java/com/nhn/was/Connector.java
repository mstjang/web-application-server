package com.nhn.was;

import java.util.Map;

public class Connector {
    private final Integer port;
    private final String protocol;
    private final Map<String, Host> hosts;

    protected Connector(Integer port, String protocol, Map<String, Host> hosts){
        this.port = port;
        this.protocol = protocol;
        this.hosts = hosts;
    }

    public static Connector of(Integer port, String protocol, Map<String, Host> hosts) {
        return new Connector(port, protocol, hosts);
    }

    public Integer getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, Host> getHosts() {
        return hosts;
    }
}
