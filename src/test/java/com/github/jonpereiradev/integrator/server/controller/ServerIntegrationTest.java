package com.github.jonpereiradev.integrator.server.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.model.ApplicationRequest;
import com.github.jonpereiradev.integrator.server.model.Resource;
import com.github.jonpereiradev.integrator.server.repository.ApplicationRepository;
import com.github.jonpereiradev.integrator.server.repository.ResourceRepository;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    private ApplicationRequest request;

    @Before
    public void beforeTest() {
        request = new ApplicationRequest();
        request.setApplication(new Application());
        request.getApplication().setId("app");
        request.getApplication().setName("request");
        request.getApplication().setVersion("v1");
        request.getApplication().setHost("https://example.com");
        request.getApplication().setDescription("description");
        request.getResources().add(new Resource());
        request.getResources().get(0).setIdentifier("resource-app");
        request.getResources().get(0).setPath("/app/v1");
        request.setSecret("secret");
    }

    @Test
    public void testMustValidateApplicationNotNull() {
        request.setApplication(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationIdNotNull() {
        request.getApplication().setId(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.id", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationIdMaxLength10() {
        request.getApplication().setId("12345678901");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.id", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("size must be between 1 and 10", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationNameNotNull() {
        request.getApplication().setName(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.name", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationNameMaxLength20() {
        request.getApplication().setName("012345678901234567890");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.name", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("size must be between 1 and 20", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationDescriptionNotNull() {
        request.getApplication().setDescription(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.description", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationDescriptionMaxLength100() {
        request.getApplication().setDescription("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.description", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("size must be between 1 and 100", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationVersionNotNull() {
        request.getApplication().setVersion(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.version", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationVersionMaxLength20() {
        request.getApplication().setVersion("012345678901234567890");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.version", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("size must be between 1 and 20", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationHostNotNull() {
        request.getApplication().setHost(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.host", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateApplicationHostPattern() {
        request.getApplication().setHost("1");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("application.host", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must match \"(http:\\/\\/|https:\\/\\/)+([\\w\\.\\d]+)+\\:?(\\d{0,5}).*\"", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateSecretNotNull() {
        request.setSecret(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("secret", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateResourceIdentifierNotNull() {
        request.getResources().get(0).setIdentifier(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("resources[0].identifier", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateResourceIdentifierMaxLength200() {
        request.getResources().get(0).setIdentifier("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("resources[0].identifier", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("size must be between 1 and 200", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateResourcePathNotNull() {
        request.getResources().get(0).setPath(null);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("resources[0].path", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("must not be null", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustValidateResourcePathMaxLength200() {
        request.getResources().get(0).setPath("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("errors");

        assertEquals(1, jsonArray.length());
        assertEquals("resources[0].path", jsonArray.getJSONObject(0).getString("field"));
        assertEquals("size must be between 1 and 200", jsonArray.getJSONObject(0).getString("defaultMessage"));
    }

    @Test
    public void testMustDeployApplicationWithoutResources() {
        request.getResources().clear();
        applicationRepository.deleteAll();
        resourceRepository.deleteAll();

        assertFalse(applicationRepository.findById(request.getApplication().getId()).isPresent());
        assertTrue(resourceRepository.findAllByApplication(request.getApplication().getId()).isEmpty());

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);
        String token = responseEntity.getHeaders().getFirst(HttpHeaders.PROXY_AUTHORIZATION);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        assertNotNull(token);

        DecodedJWT decode = JWT.decode(token);

        assertEquals(request.getApplication().getId(), decode.getSubject());

        Optional<Application> application = applicationRepository.findById(request.getApplication().getId());

        assertTrue(application.isPresent());
        assertTrue(resourceRepository.findAllByApplication(request.getApplication().getId()).isEmpty());

        assertEquals(request.getApplication().getId(), application.get().getId());
        assertEquals(request.getApplication().getHost(), application.get().getHost());
        assertEquals(request.getApplication().getDescription(), application.get().getDescription());
        assertEquals(request.getApplication().getName(), application.get().getName());
        assertEquals(request.getApplication().getVersion(), application.get().getVersion());
    }

    @Test
    public void testMustDeployApplicationWithResources() {
        applicationRepository.deleteAll();
        resourceRepository.deleteAll();

        assertFalse(applicationRepository.findById(request.getApplication().getId()).isPresent());
        assertTrue(resourceRepository.findAllByApplication(request.getApplication().getId()).isEmpty());

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/deploy", request, String.class);
        String token = responseEntity.getHeaders().getFirst(HttpHeaders.PROXY_AUTHORIZATION);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        assertNotNull(token);

        DecodedJWT decode = JWT.decode(token);

        assertEquals(request.getApplication().getId(), decode.getSubject());

        Optional<Application> application = applicationRepository.findById(request.getApplication().getId());
        List<Resource> resources = resourceRepository.findAllByApplication(request.getApplication().getId());

        assertTrue(application.isPresent());
        assertFalse(resources.isEmpty());

        assertEquals(request.getApplication().getId(), application.get().getId());
        assertEquals(request.getApplication().getHost(), application.get().getHost());
        assertEquals(request.getApplication().getDescription(), application.get().getDescription());
        assertEquals(request.getApplication().getName(), application.get().getName());
        assertEquals(request.getApplication().getVersion(), application.get().getVersion());
        assertEquals(request.getResources().get(0).getIdentifier(), resources.get(0).getIdentifier());
        assertEquals(request.getResources().get(0).getPath(), resources.get(0).getPath());
    }
}
