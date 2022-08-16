package com.nhn.was.servlet;

import com.nhn.was.request.HttpRequest;
import com.nhn.was.response.HttpResponse;

import java.io.IOException;

public interface SimpleServlet{
    public void service(HttpRequest req, HttpResponse res) throws IOException;
    public String getServletInfo();
    public void destroy();
}
