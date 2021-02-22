package com.gzqylc.utils;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;

public class HttpTool {

    public static String postJson(String dockerId, String httpUrl, String json) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(URI.create(httpUrl));
            httpPost.setEntity(new StringEntity(json));

            httpPost.setHeader("Host", dockerId);
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");


            CloseableHttpResponse response = httpClient.execute(httpPost);

            int code = response.getCode();

            Assert.state(code == 200, "请求失败");

            return response.getEntity().toString();
        }

    }
}
