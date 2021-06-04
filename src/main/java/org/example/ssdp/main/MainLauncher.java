package org.example.ssdp.main;

import org.example.ssdp.server.BroadcastServer;

import java.io.IOException;

public class MainLauncher {
    private BroadcastServer server;

    public static void main(String[] args) throws IOException, InterruptedException {
        BroadcastServer server = new BroadcastServer();
        server.start();
    }

    private void command(String cmd) throws IOException, InterruptedException {
        switch (cmd) {
            case "START":
                server.start();
            case "STOP":
                server.stop();
        }
    }
}
