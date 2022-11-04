package server;

import server.impl.CorridorsServer;

public class ServerMain {
    public static void main(String[] args) {
        try {
            new CorridorsServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
