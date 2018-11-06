package com.github.jonpereiradev.integrator.server.controller;

import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.model.ApplicationRequest;
import com.github.jonpereiradev.integrator.server.model.Resource;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private ApplicationRequest request;

    @Before
    public void beforeTest() {
        request = new ApplicationRequest();

        request.setApplication(new Application());
        request.getApplication().setId("app-v3");
        request.getApplication().setName("app-v3-api");
        request.getApplication().setVersion("v1");
        request.getApplication().setHost("http://app-v3.com");
        request.getApplication().setDescription("description");

        request.getResources().add(new Resource());
        request.getResources().get(0).setIdentifier("resource-app");
        request.getResources().get(0).setPath("/app/v1");

        request.setSecret("secret");
    }

    @Test
    public void testFindAllApplicationsMustReturnEmptyWhenNoRecordFound() {
        ResponseEntity<List> response = restTemplate.getForEntity("/applications", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testFindAllApplicationsMustReturnNotEmptyWhenRecordFound() {
        ResponseEntity<List> deployResponse = restTemplate.postForEntity("/deploy", request, List.class);
        ResponseEntity<List> applicationResponse = restTemplate.getForEntity("/applications", List.class);
        ResponseEntity<String> undeployResponse = restTemplate.postForEntity("/undeploy", request, String.class);

        assertEquals(HttpStatus.OK, deployResponse.getStatusCode());
        assertEquals(HttpStatus.OK, applicationResponse.getStatusCode());
        assertNotNull(applicationResponse.getBody());
        assertNotNull(deployResponse.getHeaders().get(HttpHeaders.PROXY_AUTHORIZATION));
        assertFalse(applicationResponse.getBody().isEmpty());
        assertEquals(HttpStatus.OK, undeployResponse.getStatusCode());

        JSONArray jsonArray = new JSONArray(applicationResponse.getBody());

        assertEquals(1, jsonArray.length());
        assertEquals("app-v3", jsonArray.getJSONObject(0).getString("id"));
    }

    @Test
    public void testFindAllResourcesMustReturnNotEmptyWhenRecordFound() {
        ResponseEntity<List> deployResponse = restTemplate.postForEntity("/deploy", request, List.class);
        ResponseEntity<List> resourceResponse = restTemplate.getForEntity("/applications/app-v3/resources", List.class);
        ResponseEntity<String> undeployResponse = restTemplate.postForEntity("/undeploy", request, String.class);

        assertEquals(HttpStatus.OK, deployResponse.getStatusCode());
        assertEquals(HttpStatus.OK, resourceResponse.getStatusCode());
        assertNotNull(resourceResponse.getBody());
        assertNotNull(deployResponse.getHeaders().get(HttpHeaders.PROXY_AUTHORIZATION));
        assertFalse(resourceResponse.getBody().isEmpty());
        assertEquals(HttpStatus.OK, undeployResponse.getStatusCode());

        JSONArray jsonArray = new JSONArray(resourceResponse.getBody());

        assertEquals(1, jsonArray.length());
        assertEquals("/app/v1", jsonArray.getJSONObject(0).getString("path"));
    }

    @Test
    public void testFindResourceByIdentifierReturnNotEmptyWhenRecordFound() {
        ResponseEntity<List> deployResponse = restTemplate.postForEntity("/deploy", request, List.class);
        assertEquals(HttpStatus.OK, deployResponse.getStatusCode());
        assertNotNull(deployResponse.getHeaders().get(HttpHeaders.PROXY_AUTHORIZATION));

        ResponseEntity<Resource> resourceResponse = restTemplate.getForEntity("/applications/resources/resource-app", Resource.class);
        assertEquals(HttpStatus.OK, resourceResponse.getStatusCode());
        assertNotNull(resourceResponse.getBody());

        ResponseEntity<String> undeployResponse = restTemplate.postForEntity("/undeploy", request, String.class);
        assertEquals(HttpStatus.OK, undeployResponse.getStatusCode());
    }
}
