package com.github.jonpereiradev.integrator.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.model.ApplicationRequest;
import com.github.jonpereiradev.integrator.server.model.Resource;
import com.github.jonpereiradev.integrator.server.repository.ApplicationRepository;
import com.github.jonpereiradev.integrator.server.repository.ResourceRepository;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IntegratorServerTest {

    @Mock
    private Request request;

    @Mock
    private PingServer pingServer;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    private ApplicationRequest applicationRequest;
    private IntegratorServer integratorServer;

    @Before
    public void beforeTest() {
        applicationRequest = new ApplicationRequest();

        applicationRequest.setSecret("secret");
        applicationRequest.setApplication(new Application());
        applicationRequest.getApplication().setId("app");
        applicationRequest.getApplication().setName("app");
        applicationRequest.getApplication().setVersion("v1");
        applicationRequest.getApplication().setHost("http://app.com");
        applicationRequest.getApplication().setDescription("description");

        applicationRequest.getResources().add(new Resource());
        applicationRequest.getResources().get(0).setIdentifier("resource-app");
        applicationRequest.getResources().get(0).setPath("/app/v1");


        integratorServer = new IntegratorServer(request, pingServer, resourceRepository, applicationRepository);
    }

    @Test
    public void testMustLoadApplicationsWhenServerCreated() {
        when(applicationRepository.findAll()).thenReturn(Collections.singletonList(applicationRequest.getApplication()));

        integratorServer.onPostConstruct();

        verify(applicationRepository).findAll();
        assertEquals(applicationRequest.getApplication(), integratorServer.findOneApplicationByKey("app"));
    }

    @Test
    public void testMustDeployApplicationForFirstTime() {
        applicationRequest.getResources().clear();

        when(applicationRepository.save(any())).thenReturn(applicationRequest.getApplication());

        DecodedJWT token = JWT.decode(integratorServer.deployApplication(applicationRequest));

        verify(applicationRepository).save(any());
        verifyNoMoreInteractions(resourceRepository);

        assertEquals(applicationRequest.getApplication().getId(), token.getSubject());
    }

    @Test
    public void testMustDeployApplicationForSecondTime() {
        applicationRequest.getResources().clear();

        when(applicationRepository.findAll()).thenReturn(Collections.singletonList(applicationRequest.getApplication()));
        when(applicationRepository.save(any())).thenReturn(applicationRequest.getApplication());
        when(applicationRepository.findById(applicationRequest.getApplication().getId())).thenReturn(Optional.of(applicationRequest.getApplication()));

        integratorServer.onPostConstruct();
        integratorServer.deployApplication(applicationRequest);

        verify(applicationRepository).findById(applicationRequest.getApplication().getId());
        verify(resourceRepository).findAllByApplication(applicationRequest.getApplication().getId());
        verify(resourceRepository).deleteAll(any());
        verify(applicationRepository).delete(applicationRequest.getApplication());
        verify(applicationRepository).save(any());
    }

    @Test
    public void testMustDeployApplicationWithDependencies() {
        when(applicationRepository.save(any())).thenReturn(applicationRequest.getApplication());

        integratorServer.deployApplication(applicationRequest);

        verify(applicationRepository).save(any());
        verify(resourceRepository).save(any());
    }

    @Test
    public void testMustThrowStateExceptionWhenUndeployInvalidApplication() {
        try {
            integratorServer.undeployApplication("app-not-exists");
            fail("Must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Application app-not-exists not deployed.", e.getMessage());
        }
    }

    @Test
    public void testMustUndeployApplicationsWhenPingServerOffline() {
        when(pingServer.ping(anyString())).thenReturn(false);
        when(applicationRepository.save(applicationRequest.getApplication())).thenReturn(applicationRequest.getApplication());
        when(applicationRepository.findById(applicationRequest.getApplication().getId())).thenReturn(Optional.of(applicationRequest.getApplication()));

        integratorServer.deployApplication(applicationRequest);
        assertFalse(integratorServer.findAllApplications().isEmpty());

        integratorServer.undeployOfflineApplications();
        assertTrue(integratorServer.findAllApplications().isEmpty());

        verify(pingServer).ping(anyString());
        verify(applicationRepository).save(applicationRequest.getApplication());
        verify(applicationRepository).findById(applicationRequest.getApplication().getId());
    }

    @Test
    public void testMustUndeployApplicationsWhenApplicationStatusOffline() throws UnirestException {
        GetRequest getRequest = mock(GetRequest.class);

        when(pingServer.ping(anyString())).thenReturn(true);
        when(applicationRepository.save(applicationRequest.getApplication())).thenReturn(applicationRequest.getApplication());
        when(applicationRepository.findById(applicationRequest.getApplication().getId())).thenReturn(Optional.of(applicationRequest.getApplication()));
        when(request.get(anyString())).thenReturn(getRequest);
        when(getRequest.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).thenReturn(getRequest);
        when(getRequest.asString()).thenThrow(new UnirestException("mock exception"));

        integratorServer.deployApplication(applicationRequest);
        assertFalse(integratorServer.findAllApplications().isEmpty());

        integratorServer.undeployOfflineApplications();
        assertTrue(integratorServer.findAllApplications().isEmpty());

        verify(pingServer).ping(anyString());
        verify(applicationRepository).save(applicationRequest.getApplication());
        verify(applicationRepository).findById(applicationRequest.getApplication().getId());
    }

    @Test
    public void testMustReturnTrueExactMatchWhenExistsResourceByApplication() {
        Resource resource = applicationRequest.getResources().get(0);
        Application application = applicationRequest.getApplication();

        when(resourceRepository.findOneByApplicationAndPath(application.getId(), resource.getPath())).thenReturn(Optional.of(resource));

        boolean exists = integratorServer.existsResourceByApplication(application.getId(), resource.getPath());

        verify(resourceRepository).findOneByApplicationAndPath(application.getId(), resource.getPath());

        assertTrue(exists);
    }

    @Test
    public void testMustReturnTruePathMatchWhenExistsResourceByApplication() {
        Resource resource = applicationRequest.getResources().get(0);
        Application application = applicationRequest.getApplication();

        when(resourceRepository.findOneByApplicationAndPath(application.getId(), resource.getPath())).thenReturn(Optional.empty());
        when(resourceRepository.findAllByApplication(application.getId())).thenReturn(applicationRequest.getResources());

        boolean exists = integratorServer.existsResourceByApplication(application.getId(), resource.getPath());

        verify(resourceRepository).findOneByApplicationAndPath(application.getId(), resource.getPath());
        verify(resourceRepository).findAllByApplication(application.getId());

        assertTrue(exists);
    }
}
