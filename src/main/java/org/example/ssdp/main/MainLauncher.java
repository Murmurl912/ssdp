package org.example.ssdp.main;

import org.example.ssdp.server.BroadcastServer;

import java.io.IOException;

public class MainLauncher {
    public static void main(String[] args) throws IOException, InterruptedException {
        BroadcastServer server = new BroadcastServer();
        server.start();
    }
}
