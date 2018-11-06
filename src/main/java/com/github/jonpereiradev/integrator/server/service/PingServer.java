package com.github.jonpereiradev.integrator.server.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PingServer {

    private static final Pattern HOST_PATTERN = Pattern.compile("(http:\\/\\/|https:\\/\\/)?([\\w\\.\\d]+)+\\:?(\\d{0,5}).*");

    public boolean ping(String address) {
        Matcher matcher = HOST_PATTERN.matcher(address);

        if (!matcher.matches()) {
            return false;
        }

        String host = matcher.group(2);
        int port = matcher.group(3).isEmpty() ? 80 : Integer.valueOf(matcher.group(3));

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

}
