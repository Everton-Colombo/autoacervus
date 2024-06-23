package com.example.autoacervus.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;

import java.util.logging.Logger;

public class HttpUtils {

    private static final Logger logger = Logger.getLogger(HttpUtils.class.getName());

    @SuppressWarnings("deprecation")
    private static ClassicHttpResponse doRequest(ClassicHttpRequest request, CookieStore cookieStore, HttpEntity... entities) {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build()) {
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            for (HttpEntity entity : entities) {
                request.setEntity(entity);
            }

            return httpClient.execute(request);
        } catch (Exception e) {
            logger.severe("[doRequest()] Request " + request + " failed: " + e.getMessage());
        }

        return null;
    }

    public static ClassicHttpResponse doPost(String url, CookieStore cookieStore, HttpEntity... entities) {
        HttpPost httpPost = new HttpPost(url);
        return doRequest(httpPost, cookieStore, entities);
    }

    public static ClassicHttpResponse doGet(String url, CookieStore cookieStore, HttpEntity... entities) {
        HttpGet httpGet = new HttpGet(url);
        return doRequest(httpGet, cookieStore, entities);
    }
}
