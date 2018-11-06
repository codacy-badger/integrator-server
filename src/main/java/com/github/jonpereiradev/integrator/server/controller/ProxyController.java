package com.github.jonpereiradev.integrator.server.controller;


import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.service.IntegratorServer;
import com.github.jonpereiradev.integrator.server.service.Request;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private static final List<String> IGNORE_HEADERS = Arrays.asList(HttpHeaders.CONTENT_LENGTH, HttpHeaders.HOST);

    private final IntegratorServer integratorServer;
    private final HttpServletRequest httpServletRequest;
    private final Request request;

    @Autowired
    public ProxyController(IntegratorServer integratorServer, HttpServletRequest httpServletRequest, Request request) {
        this.integratorServer = integratorServer;
        this.httpServletRequest = httpServletRequest;
        this.request = request;
    }

    @RequestMapping(path = "/{apiKey}/**", method = {GET, POST, PUT, DELETE, PATCH})
    public ResponseEntity<String> execute(@PathVariable String apiKey, @RequestBody(required = false) String body) throws JSONException, UnirestException {
        String requestURI = httpServletRequest.getRequestURI();
        int indexOfExpose = requestURI.indexOf(apiKey);
        String resource = requestURI.substring(indexOfExpose).replaceFirst(apiKey, "");
        String proxyAuthorization = httpServletRequest.getHeader(HttpHeaders.PROXY_AUTHORIZATION);

        if (proxyAuthorization == null) {
            return ResponseEntity.status(HttpStatus.PROXY_AUTHENTICATION_REQUIRED).build();
        }

        integratorServer.validateProxyAuthorization(proxyAuthorization);

        Application application = integratorServer.findOneApplicationByKey(apiKey);

        if (application == null || !integratorServer.existsResourceByApplication(apiKey, resource)) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        String uri = application.getHost() + resource;
        HttpMethod method = HttpMethod.resolve(httpServletRequest.getMethod());
        Enumeration<?> headerNames = httpServletRequest.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement().toString();

            if (!IGNORE_HEADERS.contains(nextElement.toLowerCase())) {
                httpHeaders.set(nextElement, httpServletRequest.getHeader(nextElement));
            }
        }

        return execute(uri, Objects.requireNonNull(method), httpHeaders, body);
    }

    private ResponseEntity<String> execute(String uri, HttpMethod method, HttpHeaders httpHeaders, String body) throws UnirestException {
        Map<String, String> headers = httpHeaders.toSingleValueMap();

        switch (method) {
            case GET:
                return executeGet(uri, headers);
            case POST:
                return executePost(uri, headers, body);
            case PUT:
                return executePut(uri, headers, body);
            case DELETE:
                return executeDelete(uri, headers, body);
            default:
                return executePatch(uri, headers, body);
        }
    }

    private ResponseEntity<String> executeGet(String uri, Map<String, String> httpHeaders) throws UnirestException {
        HttpResponse<JsonNode> get = request.get(uri).headers(httpHeaders).asJson();
        HttpStatus getStatus = HttpStatus.valueOf(get.getStatus());

        if (getStatus == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();

        get.getHeaders().forEach(headers::put);

        return new ResponseEntity<>(get.getBody().toString(), headers, getStatus);
    }

    private ResponseEntity<String> executePost(String uri, Map<String, String> httpHeaders, String body) throws UnirestException {
        return executeRequest(body, request.post(uri).headers(httpHeaders));
    }

    private ResponseEntity<String> executePut(String uri, Map<String, String> httpHeaders, String body) throws UnirestException {
        return executeRequest(body, request.put(uri).headers(httpHeaders));
    }

    private ResponseEntity<String> executePatch(String uri, Map<String, String> httpHeaders, String body) throws UnirestException {
        return executeRequest(body, request.patch(uri).headers(httpHeaders));
    }

    private ResponseEntity<String> executeDelete(String uri, Map<String, String> httpHeaders, String body) throws UnirestException {
        return executeRequest(body, request.delete(uri).headers(httpHeaders));
    }

    private ResponseEntity<String> executeRequest(String body, HttpRequestWithBody request) throws UnirestException {
        if (body != null) {
            request.body(new JsonNode(body));
        }

        HttpResponse<JsonNode> post = request.asJson();
        HttpStatus postStatus = HttpStatus.valueOf(post.getStatus());

        return executeOptionalBody(postStatus, post);
    }

    private ResponseEntity<String> executeOptionalBody(HttpStatus status, HttpResponse<JsonNode> response) {
        if (status == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        JsonNode jsonBody = response.getBody();

        response.getHeaders().forEach(headers::put);

        if (jsonBody.toString().equals("{}")) {
            return new ResponseEntity<>("", headers, status);
        }

        return new ResponseEntity<>(jsonBody.toString(), headers, status);
    }
}
