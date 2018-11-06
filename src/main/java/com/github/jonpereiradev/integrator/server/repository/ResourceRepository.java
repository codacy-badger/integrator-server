package com.github.jonpereiradev.integrator.server.repository;


import com.github.jonpereiradev.integrator.server.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Resource repository for database.
 *
 * @author Jonathan Pereira
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findAllByApplication(String application);

    Optional<Resource> findOneByApplicationAndPath(String application, String path);

    Resource findResourceByIdentifierEquals(String identifier);
}
