package com.chatapp.server;

import com.chatapp.networking.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChatServer {
    private static final int PORT = 8080;
    private static ServerSocket serverSocket;
    private static Set<ClientHandler> clientHandlers = new CopyOnWriteArraySet<>();
    private static boolean isRunning = false;

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("Chat Server started on port " + PORT);
            System.out.println("Waiting for client connections...");

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();

                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Could not start server on port " + PORT);
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            clientHandlers.clear();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

    public static boolean isServerRunning() {
        return isRunning;
    }
}
