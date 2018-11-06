package com.github.jonpereiradev.integrator.server.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RequestTest {

    private Request request;

    @Before
    public void beforeTest() {
        request = new Request();
    }

    @Test
    public void mustCreateValidGetRequest() {
        assertEquals("http://localhost/get", request.get("http://localhost/get").getUrl());
    }

    @Test
    public void mustCreateValidPostRequest() {
        assertEquals("http://localhost/post", request.post("http://localhost/post").getUrl());
    }

    @Test
    public void mustCreateValidPutRequest() {
        assertEquals("http://localhost/put", request.put("http://localhost/put").getUrl());
    }

    @Test
    public void mustCreateValidDeleteRequest() {
        assertEquals("http://localhost/delete", request.delete("http://localhost/delete").getUrl());
    }

    @Test
    public void mustCreateValidPatchRequest() {
        assertEquals("http://localhost/patch", request.patch("http://localhost/patch").getUrl());
    }

}
