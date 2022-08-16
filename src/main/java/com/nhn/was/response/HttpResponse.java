package com.nhn.was.response;

import com.nhn.was.constant.HttpStatusCode;
import com.nhn.was.exception.GeneralException;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

public class HttpResponse {
    private final Writer writer;
    private HttpStatusCode statusCode;
    private String contentsType;
    private byte[] body;

    protected HttpResponse(HttpStatusCode statusCode, byte[] body, String contentsType, Writer writer){
        this.statusCode = statusCode;
        this.body = body;
        this.contentsType = contentsType;
        this.writer = writer;
    }

    public static HttpResponse of(HttpStatusCode statusCode, byte[] body, String contentsType, Writer writer){
        return new HttpResponse(statusCode, body, contentsType, writer);
    }


    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getContentsType() {
        return contentsType;
    }

    public void setContentsType(String contentsType) {
        this.contentsType = contentsType;
    }

    public void printWriter(Writer output){
        try{
            Date now = new Date();
            int bodyLength = output.toString().length();
            output.write("Http/1.1" + this.statusCode.getCode() + this.statusCode.getMessage() + "\r\n");
            output.write("Date: " + now + "\r\n");
            output.write("Server: test was\r\n");
            output.write("Content-length: " + bodyLength + "\r\n");
            output.write("Content-type: " + this.contentsType + "\r\n\r\n");
            output.flush();
            output.close();
        }catch(IOException e){
            throw new GeneralException("HttpResponse printWriter error", e, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

    }

    public void printWriter(){
        Date now = new Date();
        try{
            this.writer.write("Http/1.1" + this.statusCode.getCode() + this.statusCode.getMessage() + "\r\n");
            this.writer.write("Date: " + now + "\r\n");
            this.writer.write("Server: test was\r\n");
            this.writer.write("Content-length: " + this.body.length + "\r\n");
            this.writer.write("Content-type: " + this.contentsType + "\r\n\r\n");
            this.writer.write(new String(this.body));
            this.writer.flush();
        }catch(IOException e){
            throw new GeneralException("HttpResponse printWriter error", e, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

    }

    public Writer getWriter() {
        return writer;
    }
}
