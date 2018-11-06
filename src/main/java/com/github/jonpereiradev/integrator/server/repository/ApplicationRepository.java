package com.github.jonpereiradev.integrator.server.repository;


import com.github.jonpereiradev.integrator.server.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Application repository for database.
 *
 * @author Jonathan Pereira
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

}
