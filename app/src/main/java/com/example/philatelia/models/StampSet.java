package com.example.philatelia.models;

public class StampSet {
    private String name;
    private String price;
    private String image;
    private String image2;
    private String year;

    public StampSet(String name, String price, String image, String image2, String year){
        this.name = name;
        this.price = price;
        this.image = image;
        this.image2 = image2;
        this.year = year;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImage() { return image; }
    public String getImage2() { return image2; }
    public String getYear() { return year; }

}

