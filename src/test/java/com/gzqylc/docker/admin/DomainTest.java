package com.gzqylc.docker.admin;

import com.github.kevinsawicki.http.HttpRequest;

public class DomainTest {

    public static void main(String[] args) {


        String domainURL = "http://test.docker-admin.com:7280";
        String ipURL = "http://127.0.0.1:7280";
        String body = HttpRequest.get(ipURL)
                .header("host", "test.docker-admin.com:7280")
                .header("user-agent","PostmanRuntime/7.26.8")
                .header("accept","*/*")
                .header("cache-control","no-cache")
                .header("connection","keep-alive")
                .body();
        System.out.println(body);
    }
}
