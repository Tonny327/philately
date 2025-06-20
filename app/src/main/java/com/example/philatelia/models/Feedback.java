package com.example.philatelia.models;

public class Feedback {
    private String username;
    private String text;
    private long timestamp;
    private String avatarUrl; // Optional

    public Feedback(String username, String text, long timestamp, String avatarUrl) {
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
        this.avatarUrl = avatarUrl;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
} 