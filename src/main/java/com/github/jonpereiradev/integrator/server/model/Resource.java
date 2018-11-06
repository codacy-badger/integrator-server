package com.github.jonpereiradev.integrator.server.model;


import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "RESOURCES", uniqueConstraints = @UniqueConstraint(name = "RESOURCES_UQ_IDENTIFIER", columnNames = "NO_RESOURCE"))
@SequenceGenerator(name = "SQ_SERVER_RESOURCES", sequenceName = "SQ_SERVER_RESOURCES", allocationSize = 1)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Resource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_SERVER_RESOURCES")
    @Column(name = "ID_RESOURCE", columnDefinition = "NUMBER(12,0)", nullable = false, scale = 12)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "NO_RESOURCE", columnDefinition = "VARCHAR2(200)", nullable = false, length = 200)
    private String identifier;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "DS_RESOURCE", columnDefinition = "VARCHAR2(200)", nullable = false, length = 200)
    private String path;

    @Column(name = "ID_APPLICATION", columnDefinition = "VARCHAR2(10)", nullable = false, scale = 10)
    private String application;

    public boolean matches(String resource) {
        if (path.equals(resource)) {
            return true;
        }

        boolean isResouceEquals = false;

        String[] paths = path.split("/");
        String[] resources = resource.split("/");

        if (resources.length == paths.length) {
            isResouceEquals = true;

            for (int i = 1; i < paths.length; i++) {
                if (paths[i].startsWith("{")) {
                    continue;
                }

                if (!paths[i].equals(resources[i])) {
                    isResouceEquals = false;
                    break;
                }
            }
        }

        return isResouceEquals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(identifier, resource.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
