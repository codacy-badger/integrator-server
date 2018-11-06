package com.github.jonpereiradev.integrator.server.controller;


import com.github.jonpereiradev.integrator.server.model.Application;
import com.github.jonpereiradev.integrator.server.model.Resource;
import com.github.jonpereiradev.integrator.server.service.IntegratorServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApplicationController {

    private final IntegratorServer integratorServer;

    @Autowired
    public ApplicationController(IntegratorServer integratorServer) {
        this.integratorServer = integratorServer;
    }

    /**
     * Get all applications that are deployed on the server.
     *
     * @return all applications deployed.
     */
    @GetMapping
    public Collection<Application> findAllApplications() {
        return integratorServer.findAllApplications();
    }

    /**
     * Get all resources registered for an application by key.
     *
     * @param id the application key identifier.
     * @return application deployed with the key.
     */
    @GetMapping(path = "/{id}/resources")
    public List<Resource> findResourcesByApplication(@PathVariable String id) {
        return integratorServer.findAllResourcesByApplication(id);
    }

    /**
     * Get one resource by identifier.
     *
     * @param identifier the resource endpoint identifier.
     * @return resource deployed with the identifier.
     */
    @GetMapping(path = "/resources/{identifier}")
    public Resource findResourceByIdentifierEquals(@PathVariable String identifier) {
        return integratorServer.findResourceByIdentifierEquals(identifier);
    }

}
