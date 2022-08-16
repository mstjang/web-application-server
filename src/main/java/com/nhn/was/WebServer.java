package com.nhn.was;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhn.was.constant.HttpStatusCode;
import com.nhn.was.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final String configFilePath = "config/server.json";
    private final Connector connector;
    public static void main(String[] args) {
        init();
    }

    public WebServer(Connector connector){
        this.connector = connector;
    }

    private static void init() {
        ClassLoader classLoader = WebServer.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(configFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode jsonNode = objectMapper.readValue(inputStream, JsonNode.class);
            JsonNode hostsJsonNode = jsonNode.get("hosts");
            Iterator<String> it = hostsJsonNode.fieldNames();
            Map<String, Host> hosts = new HashMap<>();
            while (it.hasNext()){
                String key = it.next();
                Map<String, String> errors = objectMapper.readValue(
                        hostsJsonNode.get(key).get("errors").traverse(),
                        new TypeReference<>() {
                        }
                );

                hosts.put(key, Host.of(
                                    hostsJsonNode.get(key).get("root_directory").textValue(),
                                    hostsJsonNode.get(key).get("index_page").textValue(),
                                    hostsJsonNode.get(key).get("method").textValue(),
                                    errors
                ));
            }
            WebServer webServer =
                    new WebServer(
                            Connector.of(
                                    jsonNode.get("port").intValue(),
                                    jsonNode.get("protocol").textValue(),
                                    hosts
                            ));
            webServer.start();
        } catch (IOException e) {
            throw new GeneralException(HttpStatusCode.METHOD_NOT_ALLOWED, "WebServer Init json parsing");
        }finally {
            try {
                if(inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(50);
        try (ServerSocket serverSocket = new ServerSocket(connector.getPort())) {
            Socket requestSocket;
            while ((requestSocket = serverSocket.accept()) != null) {
                Optional<Host> optionalHost = Optional.ofNullable(
                        connector.getHosts().get(requestSocket.getInetAddress().getHostName())
                );

                Runnable r = new ConnectorProcess(
                        optionalHost.orElseThrow(() -> new GeneralException(HttpStatusCode.BAD_REQUEST)),
                        requestSocket
                );
                pool.submit(r);
            }
        }
    }

}
