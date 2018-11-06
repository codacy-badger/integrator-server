package com.github.jonpereiradev.integrator.server.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.model.ApplicationRequest;
import com.github.jonpereiradev.integrator.server.model.Resource;
import com.github.jonpereiradev.integrator.server.repository.ApplicationRepository;
import com.github.jonpereiradev.integrator.server.repository.ResourceRepository;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A server component that stores all deployed applications and manages then.
 *
 * @author Jonathan Pereira
 */
@Service
@ApplicationScope
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class IntegratorServer {

    private final Map<String, Application> applications;

    private final Request request;
    private final PingServer pingServer;
    private final ApplicationRepository applicationRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    public IntegratorServer(
        Request request,
        PingServer pingServer,
        ResourceRepository resourceRepository,
        ApplicationRepository applicationRepository) {
        this.request = request;
        this.pingServer = pingServer;
        this.resourceRepository = resourceRepository;
        this.applicationRepository = applicationRepository;
        this.applications = new ConcurrentHashMap<>();
    }

    /**
     * Starts the server deploying all applications.
     */
    @PostConstruct
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @VisibleForTesting
    void onPostConstruct() {
        applicationRepository.findAll().forEach(app -> applications.put(app.getId(), app));
    }

    /**
     * Deploy an application on server.
     *
     * @param applicationRequest the application that will be deployed.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String deployApplication(ApplicationRequest applicationRequest) {
        // make the undeploy if the applicationRequest already exists on integrator server.
        if (findOneApplicationByKey(applicationRequest.getApplication().getId()) != null) {
            undeployApplication(applicationRequest.getApplication().getId());
        }

        Application application = applicationRepository.save(applicationRequest.getApplication());

        deployResources(application, applicationRequest.getResources());

        applications.put(application.getId(), application);

        return generateProxyAuthorization(applicationRequest);
    }

    /**
     * Removes an application from the server.
     *
     * @param appKey the application key that will be removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void undeployApplication(String appKey) {
        if (findOneApplicationByKey(appKey) == null) {
            throw new IllegalStateException("Application " + appKey + " not deployed.");
        }

        applicationRepository.findById(appKey).ifPresent(application -> {
            resourceRepository.deleteAll(resourceRepository.findAllByApplication(application.getId()));
            applicationRepository.delete(application);

            applications.remove(appKey);
        });
    }

    /**
     * Removes applications that are offline.
     */
    @Scheduled(fixedDelay = 1000 * 60)
    public void undeployOfflineApplications() {
        applications.forEach((key, application) -> {
            if (!isApplicationOnline(application.getHost())) {
                undeployApplication(application.getId());
            }
        });
    }

    /**
     * Get all applications that are deployed on the server.
     *
     * @return all applications deployed.
     */
    public Collection<Application> findAllApplications() {
        return applications.values();
    }

    /**
     * Get an application deployed by application key.
     *
     * @param appKey the application key identifier.
     * @return application deployed with the key.
     */
    public Application findOneApplicationByKey(String appKey) {
        return applications.get(appKey);
    }

    /**
     * Get all resources registered for an application by key.
     *
     * @param appKey the application key identifier.
     * @return application deployed with the key.
     */
    public List<Resource> findAllResourcesByApplication(String appKey) {
        return resourceRepository.findAllByApplication(appKey);
    }

    /**
     * Get one resource by identifier.
     *
     * @param identifier name of the identifier.
     * @return the resource registered by the identifier.
     */
    public Resource findResourceByIdentifierEquals(String identifier) {
        return resourceRepository.findResourceByIdentifierEquals(identifier);
    }

    /**
     * Check if the the resources exists for the application.
     *
     * @param application the application key.
     * @param resource    the resource path.
     * @return {@code true} if the application has the resource endpoint.
     */
    public boolean existsResourceByApplication(String application, String resource) {
        Optional<Resource> optional = resourceRepository.findOneByApplicationAndPath(application, resource);

        if (optional.isPresent()) {
            return true;
        }

        List<Resource> resources = resourceRepository.findAllByApplication(application);

        return resources.stream().anyMatch((r) -> (r.matches(resource)));
    }

    /**
     * Validates the proxy authorization token.
     *
     * @param proxyAuthorization token with the authorization.
     */
    public void validateProxyAuthorization(String proxyAuthorization) {
        DecodedJWT decode = JWT.decode(proxyAuthorization);

        if (decode.getSubject() == null || decode.getNotBefore().after(new Date())) {
            throw new IllegalStateException("Proxy-Authorization is invalid");
        }
    }

    /**
     * Deploy the resources of an application.
     *
     * @param application the application object from request.
     */
    private void deployResources(Application application, List<Resource> resources) {
        resources.forEach(resource -> {
            resource.setApplication(application.getId());
            resourceRepository.save(resource);
        });
    }

    /**
     * Check if an application is reachable and if is online.
     *
     * @param address the URI of the application.
     * @return {@code true} if the application is online.
     */
    private boolean isApplicationOnline(String address) {
        boolean online = pingServer.ping(address);

        if (online) {
            String pingAddress = address + "/status";

            try {
                GetRequest getRequest = request.get(pingAddress).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                online = getRequest.asString().getStatus() == HttpStatus.OK.value();
            } catch (UnirestException e) {
                // exception is not important, just want to know if application is reacheable.
                online = false;
            }
        }

        return online;
    }

    /**
     * Generates a proxy with subject for client and all authorizations.
     *
     * @param application the application client for the proxy authorization.
     * @return proxy authorization token.
     */
    private String generateProxyAuthorization(ApplicationRequest application) {
        JWTCreator.Builder builder = JWT.create();

        builder.withSubject(application.getApplication().getId());
        builder.withNotBefore(new Date());

        return builder.sign(Algorithm.HMAC256(application.getSecret().getBytes()));
    }
}
