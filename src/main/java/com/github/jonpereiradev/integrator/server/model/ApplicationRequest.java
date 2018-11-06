package com.github.jonpereiradev.integrator.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApplicationRequest {

    @Valid
    @NotNull
    private Application application;

    @NotNull
    private String secret;

    @Valid
    private List<Resource> resources = new ArrayList<>();

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<Resource> getResources() {
        return resources;
    }

}
