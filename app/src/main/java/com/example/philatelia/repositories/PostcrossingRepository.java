package com.example.philatelia.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.philatelia.models.PostcrossingUser;
import com.example.philatelia.models.PostcrossingStats;
import com.example.philatelia.models.PostcrossingPoll;
import com.example.philatelia.models.PostcrossingStamp;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.models.StampAnalytics;
import com.example.philatelia.models.StampsByYear;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;

public class PostcrossingRepository {
    private static final String PREFS = "postcrossing_prefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_REGISTERED = "user_registered";
    private static final String KEY_SENT = "stat_sent";
    private static final String KEY_RECEIVED = "stat_received";
    private static final String KEY_PARTICIPANTS = "stat_participants";
    private static final String KEY_POLL_QUESTION = "poll_question";
    private static final String KEY_POLL_OPTIONS = "poll_options";
    private static final String KEY_POLL_VOTES = "poll_votes_";
    private static final String KEY_POLL_USER_VOTE = "poll_user_vote";
    private static final String KEY_COUNTRY = "user_country";
    private static final String KEY_CITY = "user_city";
    private static final String KEY_ADDRESS = "user_address";

    public void registerUser(Context context, String name, String email, String country, String city, String address) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_REGISTERED, true)
                .apply();
        // Сбросить статистику для нового пользователя (заглушка)
        prefs.edit()
                .putInt(KEY_SENT, 0)
                .putInt(KEY_RECEIVED, 0)
                .putInt(KEY_PARTICIPANTS, prefs.getInt(KEY_PARTICIPANTS, 0) + 1)
                .apply();
    }

    public PostcrossingUser getUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME, "");
        String email = prefs.getString(KEY_EMAIL, "");
        boolean registered = prefs.getBoolean(KEY_REGISTERED, false);
        String country = prefs.getString(KEY_COUNTRY, "");
        String city = prefs.getString(KEY_CITY, "");
        String address = prefs.getString(KEY_ADDRESS, "");
        return new PostcrossingUser(name, email, registered, country, city, address);
    }

    public PostcrossingStats getStats(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int sent = prefs.getInt(KEY_SENT, 0);
        int received = prefs.getInt(KEY_RECEIVED, 0);
        int participants = prefs.getInt(KEY_PARTICIPANTS, 123);
        return new PostcrossingStats(sent, received, participants);
    }

    public void savePollVote(Context context, String option) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int currentVotes = prefs.getInt(KEY_POLL_VOTES + option, 0);
        prefs.edit()
                .putInt(KEY_POLL_VOTES + option, currentVotes + 1)
                .putString(KEY_POLL_USER_VOTE, option)
                .apply();
    }

    public PostcrossingPoll getPoll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String defaultQuestion = "Какая тема открыток вам ближе?";
        List<String> defaultOptions = java.util.Arrays.asList("Пейзажи", "Города", "Животные", "Искусство");

        if (!prefs.contains(KEY_POLL_QUESTION)) {
            prefs.edit().putString(KEY_POLL_QUESTION, defaultQuestion)
                    .putString(KEY_POLL_OPTIONS, TextUtils.join(",", defaultOptions))
                    .apply();
        }

        String question = prefs.getString(KEY_POLL_QUESTION, defaultQuestion);
        String optionsStr = prefs.getString(KEY_POLL_OPTIONS, "");
        
        List<String> options;
        if (optionsStr.contains("||")) {
            // Found old "||" delimiter. Splitting by it and migrating the data.
            options = new ArrayList<>(java.util.Arrays.asList(optionsStr.split("\\|\\|")));
            prefs.edit().putString(KEY_POLL_OPTIONS, TextUtils.join(",", options)).apply();
        } else {
            // Default delimiter is ","
            options = new ArrayList<>(java.util.Arrays.asList(TextUtils.split(optionsStr, ",")));
        }

        Map<String, Integer> votes = new HashMap<>();
        for (String option : options) {
            votes.put(option, prefs.getInt(KEY_POLL_VOTES + option, 0));
        }

        String userVote = null;
        try {
            userVote = prefs.getString(KEY_POLL_USER_VOTE, null);
        } catch (ClassCastException e) {
            // Found old data type (Integer). Clearing it to prevent future crashes.
            prefs.edit().remove(KEY_POLL_USER_VOTE).apply();
        }

        return new PostcrossingPoll(question, options, votes, userVote);
    }

    public List<PostcrossingStamp> getStamps(Context context) {
        List<PostcrossingStamp> stamps = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("stamps.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                stamps.add(new PostcrossingStamp(
                    obj.getString("title"),
                    obj.getString("imageUrl"),
                    obj.getString("price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stamps;
    }

    public List<StampAnalytics> getStampAnalytics(Context context) {
        try {
            InputStream is = context.getAssets().open("stamp_analytics.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<StampAnalytics>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void clearUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_NAME)
                .remove(KEY_EMAIL)
                .remove(KEY_REGISTERED)
                .remove(KEY_COUNTRY)
                .remove(KEY_CITY)
                .remove(KEY_ADDRESS)
                .remove(KEY_POLL_USER_VOTE)
                .apply();
    }

    // Helper to clear all data, for testing
    public void clearAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}