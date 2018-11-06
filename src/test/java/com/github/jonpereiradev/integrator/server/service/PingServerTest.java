package com.github.jonpereiradev.integrator.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PingServerTest {

    private PingServer pingServer;

    @Before
    public void beforeTest() {
        pingServer = new PingServer();
    }

    @Test
    public void mustValidateWhenHostInvalid() {
        Assert.assertFalse(pingServer.ping("invalid-host"));
    }

    @Test
    public void mustValidateWhenThrowIOException() {
        Assert.assertFalse(pingServer.ping("http://localhost:8080/ping"));
    }

}
