package com.nhn.was.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
    private final Map<String, String> parameter;

    protected HttpRequest(String parameter){
        this.parameter = setParameter(parameter);
    }

    public static HttpRequest of(String parameter){
        return new HttpRequest(parameter);
    }

    public String getParameter(String key) {
        return parameter.get(key);
    }

    private Map<String, String> setParameter(String url){
        Map<String, String> parameterMap = new HashMap<>();
        Pattern pattern = Pattern.compile("[?&]+([^=&]+)=([^&]*)");
        Matcher matcher = pattern.matcher(url);

        while ( matcher.find() ) {
            String matchString = URLDecoder.decode(matcher.group(), StandardCharsets.UTF_8);
            matchString = matchString.replaceFirst("\\?","");
            parameterMap.put(matchString.split("=")[0],
                    matchString.split("=")[1]);
        }
        return parameterMap;
    }
}

