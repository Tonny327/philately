package com.example.philatelia.models;

public class ChatMessage {
    private String message;  // Само сообщение
    private boolean isUser;  // true - пользователь, false - AI

    // Конструктор класса
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    // Геттер для текста сообщения
    public String getMessage() {
        return message;
    }

    // Геттер для типа сообщения (от пользователя или AI)
    public boolean isUser() {
        return isUser;
    }
}

