package com.github.jonpereiradev.integrator.server.model;


import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "APPLICATIONS")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Application implements Serializable {

    @Id
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "ID_APPLICATION", columnDefinition = "VARCHAR2(10)", nullable = false, scale = 10)
    private String id;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "NO_APPLICATION", columnDefinition = "VARCHAR2(20)", nullable = false, length = 20)
    private String name;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "DS_APPLICATION", columnDefinition = "VARCHAR2(100)", nullable = false, length = 100)
    private String description;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "VR_APPLICATION", columnDefinition = "VARCHAR2(20)", nullable = false, length = 20)
    private String version;

    @NotNull
    @Size(min = 1, max = 200)
    @Pattern(regexp = "(http:\\/\\/|https:\\/\\/)+([\\w\\.\\d]+)+\\:?(\\d{0,5}).*")
    @Column(name = "NO_HOST", columnDefinition = "VARCHAR2(200)", nullable = false, length = 200)
    private String host;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
