package com.chatapp.networking;

import com.chatapp.models.User;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private User user;
    private Set<ClientHandler> allHandlers;

    public ClientHandler(Socket socket, Set<ClientHandler> allHandlers) {
        this.socket = socket;
        this.allHandlers = allHandlers;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error setting up client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // First message is username
            String username = reader.readLine();
            if (username != null && !username.isEmpty()) {
                this.user = new User(username);
                user.setOnline(true);
                broadcastMessage("Server", username + " has joined the chat");
                System.out.println(username + " connected");
            }

            String message;
            while ((message = reader.readLine()) != null) {
                broadcastMessage(user.getUsername(), message);
            }
        } catch (IOException e) {
            System.err.println("Error reading from client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void broadcastMessage(String sender, String content) {
        String formattedMessage = MessageProtocol.createMessage(sender, content);
        for (ClientHandler handler : allHandlers) {
            handler.sendMessage(formattedMessage);
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    private void disconnect() {
        try {
            if (user != null) {
                user.setOnline(false);
                broadcastMessage("Server", user.getUsername() + " has left the chat");
                System.out.println(user.getUsername() + " disconnected");
            }
            allHandlers.remove(this);
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting client: " + e.getMessage());
        }
    }
}
