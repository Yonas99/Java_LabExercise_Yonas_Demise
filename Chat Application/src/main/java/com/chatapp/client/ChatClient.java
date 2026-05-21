package com.chatapp.client;

import com.chatapp.networking.MessageProtocol;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start(args);
    }

    public void start(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Connect to server
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to chat server");

            // Get username from command line args or prompt
            if (args.length > 0) {
                username = args[0];
            } else {
                System.out.print("Enter your username: ");
                username = scanner.nextLine();
            }
            writer.println(username);

            // Start message receiver thread
            new Thread(this::receiveMessages).start();

            // Send messages
            System.out.println("You can now chat! Type your messages and press Enter.");
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                writer.println(message);
            }

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                String sender = MessageProtocol.getSender(message);
                String content = MessageProtocol.getContent(message);
                System.out.println(sender + ": " + content);
            }
        } catch (IOException e) {
            System.err.println("Connection to server lost");
        }
    }

    private void disconnect() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
