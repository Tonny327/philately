package com.example.philatelia.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Stamp implements Parcelable {
    private String title;
    private String price;
    private String imageUrl;

    public Stamp(String title, String price, String imageUrl) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    protected Stamp(Parcel in) {
        title = in.readString();
        price = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Stamp> CREATOR = new Creator<Stamp>() {
        @Override
        public Stamp createFromParcel(Parcel in) {
            return new Stamp(in);
        }

        @Override
        public Stamp[] newArray(int size) {
            return new Stamp[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(imageUrl);
    }
}

