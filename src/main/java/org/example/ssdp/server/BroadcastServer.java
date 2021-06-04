package org.example.ssdp.server;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BroadcastServer {
    public final InetAddress DEFAULT_MULTICAST_ADDRESS = InetAddress.getByName("228.5.6.7");
    public final int DEFAULT_MULTICAST_PORT = 1900;
    private MulticastSocket socket;
    private ScheduledExecutorService executor;

    public BroadcastServer() throws UnknownHostException {

    }

    public void start() throws IOException, InterruptedException {
        init();
        executor.scheduleWithFixedDelay(this::task, 0, 1000, TimeUnit.MILLISECONDS);
        executor.submit(this::listen);
    }

    private void init() throws IOException, InterruptedException {
        if(executor != null) {
            executor.shutdownNow();
            executor.awaitTermination(30000, TimeUnit.MILLISECONDS);
        }
        executor = Executors.newScheduledThreadPool(8);
        socket = new MulticastSocket(DEFAULT_MULTICAST_PORT);
        socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
        socket.joinGroup(DEFAULT_MULTICAST_ADDRESS);
    }

    private void task() {
        try {
            broadcast();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcast() throws IOException {
        List<NetworkInterface> interfaceList = NetworkInterface.networkInterfaces()
                .collect(Collectors.toList());
        List<NetworkInterface> interfaces = interfaceList.stream()
                .filter(netface -> {
                    try {
                        return !netface.isVirtual()
                                && !netface.isLoopback()
                                && netface.isUp()
                                && netface.supportsMulticast();
                    } catch (SocketException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        Set<InetAddress> set = new HashSet<>();
        interfaces
                .forEach(face -> {
                    Iterator<InetAddress> addresses = face.getInetAddresses()
                            .asIterator();
                    addresses.forEachRemaining(set::add);
                });
        String message = new Date().toString() + "\n" + set.stream().map(InetAddress::getHostAddress)
                .collect(Collectors.joining("\n"));
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, DEFAULT_MULTICAST_ADDRESS, DEFAULT_MULTICAST_PORT);
        socket.send(packet);
    }

    private void listen() {
        while (true) {
            try {
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void receive() throws IOException {
        byte[] data = new byte[65536];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        System.out.println("\nReceived: " + packet.getAddress().getHostAddress() + "\n" + new String(packet.getData(), packet.getOffset(), packet.getLength()));
    }

    public void stop() throws InterruptedException {
        executor.shutdownNow();
        executor.awaitTermination(30000, TimeUnit.MILLISECONDS);
        socket.close();
    }
}
