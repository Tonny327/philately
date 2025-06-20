package com.example.philatelia.models;

import com.google.gson.annotations.SerializedName;

public class StampAnalytics {

    @SerializedName("year")
    private int year;

    @SerializedName("poll_count")
    private int pollCount;

    @SerializedName("first_place")
    private Place firstPlace;

    @SerializedName("second_place")
    private Place secondPlace;

    @SerializedName("third_place")
    private Place thirdPlace;

    // Getters for all fields
    public int getYear() {
        return year;
    }

    public int getPollCount() {
        return pollCount;
    }

    public Place getFirstPlace() {
        return firstPlace;
    }

    public Place getSecondPlace() {
        return secondPlace;
    }

    public Place getThirdPlace() {
        return thirdPlace;
    }

    // Static inner class for place details
    public static class Place {
        @SerializedName("name")
        private String name;

        @SerializedName("votes")
        private int votes;

        @SerializedName("author")
        private String author;

        // Getters for all fields
        public String getName() {
            return name;
        }

        public int getVotes() {
            return votes;
        }

        public String getAuthor() {
            return author;
        }
    }
} 