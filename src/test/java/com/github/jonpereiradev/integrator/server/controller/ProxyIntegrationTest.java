package com.github.jonpereiradev.integrator.server.controller;

import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.model.ApplicationRequest;
import com.github.jonpereiradev.integrator.server.model.Resource;
import com.github.jonpereiradev.integrator.server.service.Request;
import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings("unchecked")
public class ProxyIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private Request request;

    private HttpHeaders headers;
    private ApplicationRequest applicationRequest;

    @Before
    public void beforeTest() {
        applicationRequest = new ApplicationRequest();

        applicationRequest.setApplication(new Application());
        applicationRequest.getApplication().setId("app");
        applicationRequest.getApplication().setName("app-api");
        applicationRequest.getApplication().setVersion("1.0.0");
        applicationRequest.getApplication().setHost("http://localhost:8080/app");
        applicationRequest.getApplication().setDescription("description");

        applicationRequest.getResources().add(new Resource());
        applicationRequest.getResources().get(0).setIdentifier("resource-id");
        applicationRequest.getResources().get(0).setPath("/api/resource");

        applicationRequest.setSecret("secret");

        ResponseEntity<List> deployResponse = restTemplate.postForEntity("/deploy", applicationRequest, List.class);

        assertEquals(HttpStatus.OK, deployResponse.getStatusCode());
        assertNotNull(deployResponse.getHeaders().get(HttpHeaders.PROXY_AUTHORIZATION));

        headers = new HttpHeaders();

        headers.put(HttpHeaders.PROXY_AUTHORIZATION, deployResponse.getHeaders().get(HttpHeaders.PROXY_AUTHORIZATION));
        headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("authorization"));
    }

    @After
    public void afterTest() {
        ResponseEntity<String> deployResponse = restTemplate.postForEntity("/undeploy", applicationRequest, String.class);
        assertEquals(HttpStatus.OK, deployResponse.getStatusCode());
    }

    @Test
    public void mustValidateRequiredProxyAuthenticationHeaderWhenGetRequest() {
        ResponseEntity<String> forEntity = mustValidateRequiredProxyAuthenticationHeaderWhenRequest(HttpMethod.GET);
        assertEquals(HttpStatus.PROXY_AUTHENTICATION_REQUIRED, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateRequiredProxyAuthenticationHeaderWhenPostRequest() {
        ResponseEntity<String> forEntity = mustValidateRequiredProxyAuthenticationHeaderWhenRequest(HttpMethod.POST);
        assertEquals(HttpStatus.PROXY_AUTHENTICATION_REQUIRED, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateRequiredProxyAuthenticationHeaderWhenPutRequest() {
        ResponseEntity<String> forEntity = mustValidateRequiredProxyAuthenticationHeaderWhenRequest(HttpMethod.PUT);
        assertEquals(HttpStatus.PROXY_AUTHENTICATION_REQUIRED, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateRequiredProxyAuthenticationHeaderWhenDeleteRequest() {
        ResponseEntity<String> forEntity = mustValidateRequiredProxyAuthenticationHeaderWhenRequest(HttpMethod.DELETE);
        assertEquals(HttpStatus.PROXY_AUTHENTICATION_REQUIRED, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateRequiredProxyAuthenticationHeaderWhenPatchRequest() {
        ResponseEntity<String> forEntity = mustValidateRequiredProxyAuthenticationHeaderWhenRequest(HttpMethod.PATCH);
        assertEquals(HttpStatus.PROXY_AUTHENTICATION_REQUIRED, forEntity.getStatusCode());
    }

    /**
     * Validates the proxy authorization header for a http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private ResponseEntity<String> mustValidateRequiredProxyAuthenticationHeaderWhenRequest(HttpMethod httpMethod) {
        headers.remove(HttpHeaders.PROXY_AUTHORIZATION);
        return restTemplate.exchange("/proxy/example/request", httpMethod, new HttpEntity<>(headers), String.class);
    }

    @Test
    public void mustValidateFormatTokenProxyAuthorizationWhenGetRequest() {
        ResponseEntity<String> forEntity = mustValidateFormatTokenProxyAuthorizationWhenRequest(HttpMethod.GET);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateFormatTokenProxyAuthorizationWhenPostRequest() {
        ResponseEntity<String> forEntity = mustValidateFormatTokenProxyAuthorizationWhenRequest(HttpMethod.POST);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateFormatTokenProxyAuthorizationWhenPutRequest() {
        ResponseEntity<String> forEntity = mustValidateFormatTokenProxyAuthorizationWhenRequest(HttpMethod.PUT);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateFormatTokenProxyAuthorizationWhenDeleteRequest() {
        ResponseEntity<String> forEntity = mustValidateFormatTokenProxyAuthorizationWhenRequest(HttpMethod.DELETE);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateFormatTokenProxyAuthorizationWhenPatchRequest() {
        ResponseEntity<String> forEntity = mustValidateFormatTokenProxyAuthorizationWhenRequest(HttpMethod.PATCH);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    /**
     * Validates the proxy authorization header format for a http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private ResponseEntity<String> mustValidateFormatTokenProxyAuthorizationWhenRequest(HttpMethod httpMethod) {
        headers.put(HttpHeaders.PROXY_AUTHORIZATION, Collections.singletonList("format-invalid"));
        return restTemplate.exchange("/proxy/example/request", httpMethod, new HttpEntity<>(headers), String.class);
    }

    @Test
    public void mustValidateClientClaimTokenProxyAuthenticationWhenGetRequest() {
        ResponseEntity<String> forEntity = mustValidateClientClaimTokenProxyAuthenticationWhenRequest(HttpMethod.GET);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateClientClaimTokenProxyAuthenticationWhenPostRequest() {
        ResponseEntity<String> forEntity = mustValidateClientClaimTokenProxyAuthenticationWhenRequest(HttpMethod.POST);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateClientClaimTokenProxyAuthenticationWhenPutRequest() {
        ResponseEntity<String> forEntity = mustValidateClientClaimTokenProxyAuthenticationWhenRequest(HttpMethod.PUT);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateClientClaimTokenProxyAuthenticationWhenDeleteRequest() {
        ResponseEntity<String> forEntity = mustValidateClientClaimTokenProxyAuthenticationWhenRequest(HttpMethod.DELETE);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateClientClaimTokenProxyAuthenticationWhenPatchRequest() {
        ResponseEntity<String> forEntity = mustValidateClientClaimTokenProxyAuthenticationWhenRequest(HttpMethod.PATCH);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, forEntity.getStatusCode());
    }

    /**
     * Validates the existence of client claim on proxy authentication header for a http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private ResponseEntity<String> mustValidateClientClaimTokenProxyAuthenticationWhenRequest(HttpMethod httpMethod) {
        headers.put(HttpHeaders.PROXY_AUTHORIZATION, Collections.singletonList("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"));
        return restTemplate.exchange("/proxy/example/request", httpMethod, new HttpEntity<>(headers), String.class);
    }

    @Test
    public void mustValidateApplicationNotExistsWhenGetRequest() {
        ResponseEntity<String> forEntity = mustValidateApplicationNotExistsWhenRequest(HttpMethod.GET);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateApplicationNotExistsWhenPostRequest() {
        ResponseEntity<String> forEntity = mustValidateApplicationNotExistsWhenRequest(HttpMethod.POST);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateApplicationNotExistsWhenPutRequest() {
        ResponseEntity<String> forEntity = mustValidateApplicationNotExistsWhenRequest(HttpMethod.PUT);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateApplicationNotExistsWhenDeleteRequest() {
        ResponseEntity<String> forEntity = mustValidateApplicationNotExistsWhenRequest(HttpMethod.DELETE);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateApplicationNotExistsWhenPatchRequest() {
        ResponseEntity<String> forEntity = mustValidateApplicationNotExistsWhenRequest(HttpMethod.PATCH);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    /**
     * Validates the existence of an application on server for a http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private ResponseEntity<String> mustValidateApplicationNotExistsWhenRequest(HttpMethod httpMethod) {
        return restTemplate.exchange("/proxy/example/request", httpMethod, new HttpEntity<>(headers), String.class);
    }

    @Test
    public void mustValidateResourceNotExistsWhenGetRequest() {
        ResponseEntity<String> forEntity = mustValidateResourceNotExistsWhenRequest(HttpMethod.GET);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateResourceNotExistsWhenPostRequest() {
        ResponseEntity<String> forEntity = mustValidateResourceNotExistsWhenRequest(HttpMethod.POST);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateResourceNotExistsWhenPutRequest() {
        ResponseEntity<String> forEntity = mustValidateResourceNotExistsWhenRequest(HttpMethod.PUT);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateResourceNotExistsWhenDeleteRequest() {
        ResponseEntity<String> forEntity = mustValidateResourceNotExistsWhenRequest(HttpMethod.DELETE);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    @Test
    public void mustValidateResourceNotExistsWhenPatchRequest() {
        ResponseEntity<String> forEntity = mustValidateResourceNotExistsWhenRequest(HttpMethod.PATCH);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());
    }

    /**
     * Validates the existence of a resource on server for a http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private ResponseEntity<String> mustValidateResourceNotExistsWhenRequest(HttpMethod httpMethod) {
        return restTemplate.exchange("/proxy/app/request", httpMethod, new HttpEntity<>(headers), String.class);
    }

    @Test
    public void mustValidateRequestNotFoundWhenGetRequest() throws UnirestException {
        GetRequest getRequest = mock(GetRequest.class);
        HttpResponse<JsonNode> response = mock(HttpResponse.class);

        when(request.get("http://localhost:8080/app/api/resource")).thenReturn(getRequest);
        when(getRequest.headers(any())).thenReturn(getRequest);
        when(getRequest.asJson()).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.NOT_FOUND.value());

        ResponseEntity<String> forEntity = restTemplate.exchange("/proxy/app/api/resource", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());

        verify(request).get("http://localhost:8080/app/api/resource");
        verify(getRequest).headers(any());
        verify(getRequest).asJson();
        verify(response).getStatus();
    }

    @Test
    public void mustValidateRequestNotFoundWhenPostRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.post("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustValidateRequestNotFoundWhenRequest(requestWithBody, HttpMethod.POST);

        verify(request).post("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustValidateRequestNotFoundWhenPutRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.put("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustValidateRequestNotFoundWhenRequest(requestWithBody, HttpMethod.PUT);

        verify(request).put("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustValidateRequestNotFoundWhenDeleteRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.delete("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustValidateRequestNotFoundWhenRequest(requestWithBody, HttpMethod.DELETE);

        verify(request).delete("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustValidateRequestNotFoundWhenPatchRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.patch("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustValidateRequestNotFoundWhenRequest(requestWithBody, HttpMethod.PATCH);

        verify(request).patch("http://localhost:8080/app/api/resource");
    }

    /**
     * Validates request not found when request for a offline resource for http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private void mustValidateRequestNotFoundWhenRequest(HttpRequestWithBody requestWithBody, HttpMethod httpMethod) throws UnirestException {
        HttpResponse<JsonNode> response = mock(HttpResponse.class);

        when(requestWithBody.headers(any())).thenReturn(requestWithBody);
        when(requestWithBody.asJson()).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.NOT_FOUND.value());

        ResponseEntity<String> forEntity = restTemplate.exchange("/proxy/app/api/resource", httpMethod, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.NOT_FOUND, forEntity.getStatusCode());

        verify(requestWithBody).headers(any());
        verify(requestWithBody).asJson();
        verify(response).getStatus();
    }

    @Test
    public void mustExecuteWithSuccessWhenGetRequest() throws UnirestException {
        GetRequest getRequest = mock(GetRequest.class);
        HttpResponse<JsonNode> response = mock(HttpResponse.class);

        when(request.get("http://localhost:8080/app/api/resource")).thenReturn(getRequest);
        when(getRequest.headers(any())).thenReturn(getRequest);
        when(getRequest.asJson()).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.OK.value());
        when(response.getHeaders()).thenReturn(new Headers());
        when(response.getBody()).thenReturn(new JsonNode("{}"));

        ResponseEntity<String> forEntity = restTemplate.exchange("/proxy/app/api/resource", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, forEntity.getStatusCode());

        verify(request).get("http://localhost:8080/app/api/resource");
        verify(getRequest).headers(any());
        verify(getRequest).asJson();
        verify(response).getStatus();
        verify(response).getHeaders();
        verify(response).getBody();
    }

    @Test
    public void mustExecuteWithSuccessWhenPostRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.post("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequest(requestWithBody, HttpMethod.POST);

        verify(request).post("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustExecuteWithSuccessWhenPutRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.put("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequest(requestWithBody, HttpMethod.PUT);

        verify(request).put("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustExecuteWithSuccessWhenDeleteRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.delete("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequest(requestWithBody, HttpMethod.DELETE);

        verify(request).delete("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustExecuteWithSuccessWhenPatchRequest() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.patch("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequest(requestWithBody, HttpMethod.PATCH);

        verify(request).patch("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustExecuteWithSuccessWhenPostRequestWithBody() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.post("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequestWithBody(requestWithBody, HttpMethod.POST);

        verify(request).post("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustExecuteWithSuccessWhenPutRequestWithBody() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.put("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequestWithBody(requestWithBody, HttpMethod.PUT);

        verify(request).put("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustExecuteWithSuccessWhenDeleteRequestWithBody() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.delete("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequestWithBody(requestWithBody, HttpMethod.DELETE);

        verify(request).delete("http://localhost:8080/app/api/resource");
    }

    @Test
    public void mustExecuteWithSuccessWhenPatchRequestWithBody() throws UnirestException {
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);

        when(request.patch("http://localhost:8080/app/api/resource")).thenReturn(requestWithBody);

        mustExecuteWithSuccessWhenRequestWithBody(requestWithBody, HttpMethod.PATCH);

        verify(request).patch("http://localhost:8080/app/api/resource");
    }

    /**
     * Validates request not found when request for a offline resource for http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private void mustExecuteWithSuccessWhenRequest(HttpRequestWithBody requestWithBody, HttpMethod httpMethod) throws UnirestException {
        ResponseEntity<String> forEntity = mustExecuteWithSuccessWhenRequestWithBody(requestWithBody, httpMethod, null, "{}");

        assertEquals(HttpStatus.OK, forEntity.getStatusCode());

        verify(requestWithBody).headers(any());
        verify(requestWithBody).asJson();
    }

    /**
     * Validates request not found when request for a offline resource for http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private void mustExecuteWithSuccessWhenRequestWithBody(HttpRequestWithBody requestWithBody, HttpMethod httpMethod) throws UnirestException {
        ResponseEntity<String> forEntity = mustExecuteWithSuccessWhenRequestWithBody(requestWithBody, httpMethod, "{\"id\": 1}", "{\"id\": 1}");

        assertEquals(HttpStatus.OK, forEntity.getStatusCode());

        verify(requestWithBody).headers(any());
        verify(requestWithBody).asJson();
    }

    /**
     * Validates request not found when request for a offline resource for http method.
     *
     * @param httpMethod the http method execution on request.
     */
    private ResponseEntity<String> mustExecuteWithSuccessWhenRequestWithBody(HttpRequestWithBody requestWithBody, HttpMethod httpMethod, String body, String responseBody) throws UnirestException {
        HttpResponse<JsonNode> response = mock(HttpResponse.class);

        when(requestWithBody.headers(any())).thenReturn(requestWithBody);
        when(requestWithBody.asJson()).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.OK.value());
        when(response.getHeaders()).thenReturn(new Headers());
        when(response.getBody()).thenReturn(new JsonNode(responseBody));

        ResponseEntity<String> forEntity = restTemplate.exchange("/proxy/app/api/resource", httpMethod, new HttpEntity<>(body, headers), String.class);

        verify(response).getStatus();
        verify(response).getHeaders();
        verify(response).getBody();

        return forEntity;
    }
}
