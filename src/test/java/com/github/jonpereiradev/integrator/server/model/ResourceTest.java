package com.github.jonpereiradev.integrator.server.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ResourceTest {

    private Resource resource;

    @Before
    public void beforeTest() {
        resource = new Resource();
    }

    @Test
    public void testMustReturnTrueWhenPathEquals() {
        resource.setPath("/exact/path");
        assertTrue(resource.matches("/exact/path"));
    }

    @Test
    public void testMustReturnFalseWhenPathSizeNotEquals() {
        resource.setPath("/exact/path");
        assertFalse(resource.matches("/false"));
    }

    @Test
    public void testMustReturnFalseWhenPathWithSizeEqualsAndContentNotEquals() {
        resource.setPath("/exact");
        assertFalse(resource.matches("/equal"));
    }

    @Test
    public void testMustReturnTrueWhenPathWithParametersEquals() {
        resource.setPath("/users/{id}");
        assertTrue(resource.matches("/users/1"));
    }

    @Test
    public void testMustReturnFalseWhenIdentifierDifferent() {
        Resource resource1 = new Resource();
        Resource resource2 = new Resource();

        resource1.setIdentifier("id-1");
        resource2.setIdentifier("id-2");

        assertNotEquals(resource1, resource2);
        assertNotEquals(resource1.hashCode(), resource2.hashCode());
    }

    @Test
    public void testMustReturnTrueWhenIdentifierDifferent() {
        Resource resource1 = new Resource();
        Resource resource2 = new Resource();

        resource1.setIdentifier("id-1");
        resource2.setIdentifier("id-1");

        assertEquals(resource1, resource2);
        assertEquals(resource1.hashCode(), resource2.hashCode());
    }
}
