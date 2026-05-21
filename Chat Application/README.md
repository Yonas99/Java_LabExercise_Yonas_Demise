# Simple Chat App

A minimal chat application with just 6 Java files. Users can connect and message each other in real-time.

## Files

- `ChatServer.java` - Runs the chat server
- `ChatClient.java` - Console client for chatting
- `ClientHandler.java` - Handles each client connection
- `MessageProtocol.java` - Simple message format
- `User.java` - User model
- `Message.java` - Message model

## How to Run

**Start the server:**
```bash
mvn exec:java -Dexec.mainClass="com.chatapp.server.ChatServer"
```

**Start a client:**
```bash
mvn exec:java -Dexec.mainClass="com.chatapp.client.ChatClient"
```

## Features

- Simple console interface
- Real-time messaging
- No database needed
- No external dependencies
