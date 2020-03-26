package com.company;

import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8080);

            while(true) {
                new Server(server.accept());
            }

        }catch(Exception e) {
            System.out.println("Error: " + e);

        }

    }
}
