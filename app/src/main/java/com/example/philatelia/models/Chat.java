package com.example.philatelia.models;

import java.util.List;
import java.util.UUID;

public class Chat {
    private String id;
    private String title;
    private long timestamp;
    private List<ChatMessage> messages;

    public Chat(String title, List<ChatMessage> messages) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.timestamp = System.currentTimeMillis();
        this.messages = messages;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
} 