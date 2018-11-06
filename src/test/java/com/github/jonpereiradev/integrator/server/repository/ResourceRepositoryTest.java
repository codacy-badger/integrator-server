package com.github.jonpereiradev.integrator.server.repository;

import com.github.jonpereiradev.integrator.server.model.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ResourceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ResourceRepository resourceRepository;

    private Resource resource;

    @Before
    public void beforeTest() {
        resource = new Resource();

        resource.setIdentifier("resource-app");
        resource.setPath("/app/test");
        resource.setApplication("test");
    }

    @Test
    public void testFindAllByApplicationMustReturnOne() {
        entityManager.persist(resource);
        Assert.assertEquals(1, resourceRepository.findAllByApplication("test").size());
    }

    @Test
    public void testFindAllByApplicationMustReturnEmpty() {
        entityManager.persist(resource);
        Assert.assertEquals(0, resourceRepository.findAllByApplication("none").size());
    }

    @Test
    public void testFindOneByApplicationAndPathMustReturnNotEmpty() {
        entityManager.persist(resource);
        Assert.assertTrue(resourceRepository.findOneByApplicationAndPath("test", "/app/test").isPresent());
    }

    @Test
    public void testFindOneByApplicationAndPathWrongPathMustReturnEmpty() {
        entityManager.persist(resource);
        Assert.assertFalse(resourceRepository.findOneByApplicationAndPath("test", "/app/none").isPresent());
    }

    @Test
    public void testFindOneByApplicationAndPathWrongApplicationMustReturnEmpty() {
        entityManager.persist(resource);
        Assert.assertFalse(resourceRepository.findOneByApplicationAndPath("none", "/app/test").isPresent());
    }
}
