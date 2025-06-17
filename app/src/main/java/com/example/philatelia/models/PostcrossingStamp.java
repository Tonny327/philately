package com.example.philatelia.models;

public class PostcrossingStamp {
    private String title;
    private String imageUrl;
    private String price;

    public PostcrossingStamp(String title, String imageUrl, String price) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
} 