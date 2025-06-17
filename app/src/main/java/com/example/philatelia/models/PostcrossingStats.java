package com.example.philatelia.models;

public class PostcrossingStats {
    private int sent;
    private int received;
    private int participants;

    public PostcrossingStats(int sent, int received, int participants) {
        this.sent = sent;
        this.received = received;
        this.participants = participants;
    }

    public int getSent() { return sent; }
    public void setSent(int sent) { this.sent = sent; }
    public int getReceived() { return received; }
    public void setReceived(int received) { this.received = received; }
    public int getParticipants() { return participants; }
    public void setParticipants(int participants) { this.participants = participants; }
} 