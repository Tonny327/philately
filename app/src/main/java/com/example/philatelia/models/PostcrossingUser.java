package com.example.philatelia.models;

public class PostcrossingUser {
    private String name;
    private String email;
    private boolean registered;

    public PostcrossingUser(String name, String email, boolean registered) {
        this.name = name;
        this.email = email;
        this.registered = registered;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isRegistered() { return registered; }
    public void setRegistered(boolean registered) { this.registered = registered; }
} 