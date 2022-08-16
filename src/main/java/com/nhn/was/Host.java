package com.nhn.was;

import java.util.Map;

public class Host {
    private final String rootDirectory;
    private final String indexPage;
    private final String method;
    private final Map<String, String> errors;

    protected Host(String rootDirectory, String indexPage, String method,
                   Map<String, String> errors) {
        this.rootDirectory = rootDirectory;
        this.indexPage = indexPage;
        this.method = method;
        this.errors = errors;
    }

    public static Host of(String rootDirectory, String indexPage, String method,
                          Map<String, String> errors) {
        return new Host(rootDirectory, indexPage, method, errors);
    }

    public String getRootDirectory() {
        return this.rootDirectory;
    }

    public String getIndexPage() {
        return this.indexPage;
    }

    public Map<String, String> getErrors() {
        return this.errors;
    }

    public String getMethod() {
        return method;
    }
}
