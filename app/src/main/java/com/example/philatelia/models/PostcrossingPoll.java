package com.example.philatelia.models;

import java.util.List;

public class PostcrossingPoll {
    private String question;
    private List<String> options;
    private int[] votes;
    private int userVote;

    public PostcrossingPoll(String question, List<String> options) {
        this.question = question;
        this.options = options;
        this.votes = new int[options.size()];
        this.userVote = -1;
    }

    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public int[] getVotes() { return votes; }
    public void setVotes(int[] votes) { this.votes = votes; }
    public int getUserVote() { return userVote; }
    public void setUserVote(int userVote) { this.userVote = userVote; }
    public void vote(int optionIndex) {
        if (userVote == -1 && optionIndex >= 0 && optionIndex < votes.length) {
            votes[optionIndex]++;
            userVote = optionIndex;
        }
    }
} 