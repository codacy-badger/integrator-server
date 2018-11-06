package com.github.jonpereiradev.integrator.server.service;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.springframework.stereotype.Component;

@Component
public class Request {

    public GetRequest get(String url) {
        return Unirest.get(url);
    }

    public HttpRequestWithBody post(String url) {
        return Unirest.post(url);
    }

    public HttpRequestWithBody delete(String url) {
        return Unirest.delete(url);
    }

    public HttpRequestWithBody patch(String url) {
        return Unirest.patch(url);
    }

    public HttpRequestWithBody put(String url) {
        return Unirest.put(url);
    }
}
