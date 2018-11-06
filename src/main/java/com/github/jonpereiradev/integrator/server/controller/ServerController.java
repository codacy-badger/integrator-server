package com.github.jonpereiradev.integrator.server.controller;


import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.model.ApplicationRequest;
import com.github.jonpereiradev.integrator.server.service.IntegratorServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ServerController {

    private final Logger logger = LoggerFactory.getLogger(ServerController.class);

    private final IntegratorServer integratorServer;

    @Autowired
    public ServerController(IntegratorServer integratorServer) {
        this.integratorServer = integratorServer;
    }

    @PostMapping(path = "/deploy")
    public ResponseEntity<String> deploy(@Valid @RequestBody ApplicationRequest application) {
        String proxyAuthorization = integratorServer.deployApplication(application);

        if (logger.isInfoEnabled()) {
            logApplicationDeploy(application);
        }

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.PROXY_AUTHORIZATION, proxyAuthorization).build();
    }

    @PostMapping(path = "/undeploy")
    public String undeploy(@Valid @RequestBody ApplicationRequest applicationRequest) throws JSONException {
        integratorServer.undeployApplication(applicationRequest.getApplication().getId());
        return new JSONObject().put("message", "Application '" + applicationRequest.getApplication().getId() + "' undeployed.").toString();
    }

    /**
     * Log the applicationRequest deployed success.
     *
     * @param applicationRequest the applicationRequest deployed.
     */
    private void logApplicationDeploy(ApplicationRequest applicationRequest) {
        Application application = applicationRequest.getApplication();

        logger.info("################################################");
        logger.info("# Application " + application.getId() + " deployed");
        logger.info("# Name: " + application.getName());
        logger.info("# Description: " + application.getDescription());
        logger.info("# Version: " + application.getVersion());
        logger.info("# Host: " + application.getHost());
        logger.info("################################################");
    }
}
