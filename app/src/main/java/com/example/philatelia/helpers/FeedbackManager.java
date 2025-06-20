package com.example.philatelia.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.philatelia.models.Feedback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedbackManager {

    private static final String PREFS_NAME = "feedback_prefs";
    private static final String KEY_FEEDBACK_LIST = "feedback_list";
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public FeedbackManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<Feedback> getFeedback() {
        String json = sharedPreferences.getString(KEY_FEEDBACK_LIST, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Feedback>>() {}.getType();
        List<Feedback> feedbackList = gson.fromJson(json, type);
        // Sort by timestamp, newest first
        Collections.sort(feedbackList, (f1, f2) -> Long.compare(f2.getTimestamp(), f1.getTimestamp()));
        return feedbackList;
    }

    public void addFeedback(Feedback feedback) {
        List<Feedback> feedbackList = getFeedback();
        feedbackList.add(feedback);
        // Re-sort after adding
        Collections.sort(feedbackList, (f1, f2) -> Long.compare(f2.getTimestamp(), f1.getTimestamp()));
        String json = gson.toJson(feedbackList);
        sharedPreferences.edit().putString(KEY_FEEDBACK_LIST, json).apply();
    }
} 