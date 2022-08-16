package com.nhn.was;

import com.nhn.was.constant.HttpStatusCode;
import com.nhn.was.exception.GeneralException;
import com.nhn.was.request.HttpRequest;
import com.nhn.was.response.HttpResponse;
import com.nhn.was.servlet.SimpleServlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Hello implements SimpleServlet {
    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        try{
            String body = "Hello, ";
            if(req.getParameter("name") == null)
                body = body + "파라미터에 이름을 셋팅해주세요. ex) /Hello?name=이름";
            else
                body = body + req.getParameter("name");


            res.setStatusCode(HttpStatusCode.OK);
            res.setContentsType("text/html; charset=utf-8");
            res.setBody(body.getBytes(StandardCharsets.UTF_8));
            res.printWriter();
        }catch (Exception e){
            throw new GeneralException("Hello service exception error", e, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
