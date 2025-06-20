package com.example.philatelia.models;

import java.util.List;
import java.util.Map;

public class PostcrossingPoll {
    private String question;
    private List<String> options;
    private Map<String, Integer> votes;
    private String userVote;

    public PostcrossingPoll(String question, List<String> options, Map<String, Integer> votes, String userVote) {
        this.question = question;
        this.options = options;
        this.votes = votes;
        this.userVote = userVote;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public String getUserVote() {
        return userVote;
    }

    public boolean isVoted() {
        return userVote != null && !userVote.isEmpty();
    }

    public int getTotalVotes() {
        int total = 0;
        for (int count : votes.values()) {
            total += count;
        }
        return total;
    }

    public int getVotesForOption(String option) {
        return votes.getOrDefault(option, 0);
    }
} 