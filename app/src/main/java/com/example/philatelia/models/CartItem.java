package com.example.philatelia.models;

public class CartItem {
    private String stampId;
    private String title;
    private String imageUrl;
    private String price;
    private int quantity;

    public CartItem(String stampId, String title, String imageUrl, String price, int quantity) {
        this.stampId = stampId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public String getStampId() { return stampId; }
    public void setStampId(String stampId) { this.stampId = stampId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 