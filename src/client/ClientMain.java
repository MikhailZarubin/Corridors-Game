package client;

import client.impl.UICorridors;

public class ClientMain {
    public static void main(String[] args) {
        try {
            new UICorridors();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
