package com.nhn.was;

import com.nhn.was.constant.HttpStatusCode;
import com.nhn.was.exception.GeneralException;
import com.nhn.was.request.HttpRequest;
import com.nhn.was.response.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class ConnectorProcess implements Runnable {
    private final Host host;
    private final Socket socket;
    private InputStream fileInputStream = null;
    private OutputStream raw = null;
    private Writer out = null;

    protected ConnectorProcess(Host host, Socket socket) {
        this.host = host;
        this.socket = socket;
    }

    public static ConnectorProcess of(Host host, Socket socket) {
        return new ConnectorProcess(host, socket);
    }

    @Override
    public void run() {
        try {
            raw = new BufferedOutputStream(socket.getOutputStream());
            out = new OutputStreamWriter(raw);
            Reader in = new InputStreamReader(new BufferedInputStream(socket.getInputStream()), StandardCharsets.UTF_8);
            StringBuilder requestLine = new StringBuilder();
            while (true) {
                int c = in.read();
                if (c == '\r' || c == '\n')
                    break;
                requestLine.append((char) c);
            }
            String get = requestLine.toString();
            String[] tokens = get.split("\\s+");
            String method = tokens[0];
            String protocol = "";
            String fileName = tokens[1];
            if (tokens.length > 2) {
                protocol = tokens[2];
            }
            if (httpValidation(method, protocol, fileName)) {

                if (fileName.endsWith("/") && fileName.length() <= 1) {
                    fileName += host.getIndexPage();
                } else if (!fileName.endsWith(".html")) {
                    String classPath = fileName.replaceFirst("/", "");
                    if (classPath.contains("?")) {
                        classPath = classPath.substring(0, classPath.indexOf("?"));
                    }

                    HttpRequest req = HttpRequest.of(fileName);
                    HttpResponse res = HttpResponse.of(null, null, null, out);
                    switch (classPath) {
                        case "Hello" -> {
                            Hello hello = new Hello();
                            hello.service(req, res);
                        }
                        case "service.Hello" -> {
                            com.nhn.was.service.Hello serviceHello = new com.nhn.was.service.Hello();
                            serviceHello.service(req, res);
                        }
                        default -> {
                            fileInputStream =
                                    ClassLoader.getSystemResourceAsStream(host.getRootDirectory() + host.getErrors().get("404"));
                            if (fileInputStream != null) {
                                byte[] htmlData = fileInputStream.readAllBytes();
                                HttpResponse.of(HttpStatusCode.NOT_FOUND,
                                        htmlData, "text/html; charset=utf-8", out).printWriter();
                            }
                        }
                    }

                }
                String contentType =
                        URLConnection.getFileNameMap().getContentTypeFor(fileName);

                fileInputStream = ClassLoader.getSystemResourceAsStream(
                        host.getRootDirectory() + fileName.substring(1));
                if (fileInputStream != null) {
                    byte[] htmlData = fileInputStream.readAllBytes();
                    HttpResponse.of(HttpStatusCode.OK,
                            htmlData, contentType, out).printWriter();
                } else {
                    fileInputStream =
                            ClassLoader.getSystemResourceAsStream(host.getRootDirectory() + host.getErrors().get("404"));
                    if (fileInputStream != null) {
                        byte[] htmlData = fileInputStream.readAllBytes();
                        HttpResponse.of(HttpStatusCode.NOT_FOUND,
                                htmlData, "text/html; charset=utf-8", out).printWriter();
                    }
                }
            }
        } catch (IOException ex) {
            throw new GeneralException("page connect error", ex, HttpStatusCode.UNAUTHORIZED);
        } finally {
            try {
                socket.close();
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException ex) {
                throw new GeneralException("socket close, InputStream close error", ex, HttpStatusCode.UNAUTHORIZED);
            }
        }
    }

    public boolean httpValidation(String method, String protocol, String path) {
        try {
            // method check
            if (!host.getMethod().contains(method)) {
                fileInputStream =
                        ClassLoader.getSystemResourceAsStream(host.getRootDirectory() + host.getErrors().get("403"));
                if (fileInputStream != null) {
                    byte[] htmlData = fileInputStream.readAllBytes();
                    HttpResponse.of(HttpStatusCode.METHOD_NOT_ALLOWED,
                            htmlData, "text/html; charset=utf-8", out).printWriter();
                }
                throw new GeneralException(HttpStatusCode.BAD_REQUEST, "method is not supported");
            }

            // protocol check
            if (!protocol.endsWith("1.1")) {
//            res.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
                throw new GeneralException(HttpStatusCode.BAD_REQUEST, "protocol is not supported");
            }

            // path check
            if (path.endsWith(".exe") || path.contains("..")) {
                fileInputStream =
                        ClassLoader.getSystemResourceAsStream(host.getRootDirectory() + host.getErrors().get("403"));
                if (fileInputStream != null) {
                    byte[] htmlData = fileInputStream.readAllBytes();
                    HttpResponse.of(HttpStatusCode.METHOD_NOT_ALLOWED,
                            htmlData, "text/html; charset=utf-8", out).printWriter();
                }
                throw new GeneralException(HttpStatusCode.BAD_REQUEST, "file or directory is not supported");
            }
        } catch (IOException e) {
            throw new GeneralException("httpValidation IOException error", e, HttpStatusCode.FORBIDDEN);
        }
        return true;
    }

}
