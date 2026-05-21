package com.chatapp.networking;

public class MessageProtocol {
    public static final String DELIMITER = "|";

    public static String createMessage(String sender, String content) {
        return sender + DELIMITER + content;
    }

    public static String getSender(String message) {
        int index = message.indexOf(DELIMITER);
        return index != -1 ? message.substring(0, index) : "";
    }

    public static String getContent(String message) {
        int index = message.indexOf(DELIMITER);
        return index != -1 ? message.substring(index + 1) : message;
    }
}
