package com.example.philatelia.models;

public class PostcrossingUser {
    private String name;
    private String email;
    private boolean isRegistered;
    private String country;
    private String city;
    private String fullAddress;
    // Пароль не будет храниться в модели из соображений безопасности

    public PostcrossingUser(String name, String email, boolean isRegistered, String country, String city, String fullAddress) {
        this.name = name;
        this.email = email;
        this.isRegistered = isRegistered;
        this.country = country;
        this.city = city;
        this.fullAddress = fullAddress;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getFullAddress() {
        return fullAddress;
    }
} 