package com.example.philatelia.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItemEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String stampId;
    public String title;
    public String imageUrl;
    public String price;
    public double priceNum;
    public int quantity;
    public int priceKopecks;
} 