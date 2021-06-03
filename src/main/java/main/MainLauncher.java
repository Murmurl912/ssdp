package main;

import server.BroadcastServer;

import java.io.IOException;
import java.net.UnknownHostException;

public class MainLauncher {
    public static void main(String[] args) throws IOException, InterruptedException {
        BroadcastServer server = new BroadcastServer();
        server.start();
    }
}
