package com.example.philatelia.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.philatelia.models.PostcrossingUser;
import com.example.philatelia.models.PostcrossingStats;
import com.example.philatelia.models.PostcrossingPoll;
import com.example.philatelia.models.PostcrossingStamp;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
    private static final String KEY_POLL_VOTES = "poll_votes";
    private static final String KEY_POLL_USER_VOTE = "poll_user_vote";

    public void registerUser(Context context, String name, String email) {
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
                .putInt(KEY_PARTICIPANTS, 100 + new Random().nextInt(100))
                .apply();
    }

    public PostcrossingUser getUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME, "");
        String email = prefs.getString(KEY_EMAIL, "");
        boolean registered = prefs.getBoolean(KEY_REGISTERED, false);
        return new PostcrossingUser(name, email, registered);
    }

    public PostcrossingStats getStats(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int sent = prefs.getInt(KEY_SENT, 0);
        int received = prefs.getInt(KEY_RECEIVED, 0);
        int participants = prefs.getInt(KEY_PARTICIPANTS, 123);
        return new PostcrossingStats(sent, received, participants);
    }

    public PostcrossingPoll getPoll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String question = prefs.getString(KEY_POLL_QUESTION, null);
        String optionsStr = prefs.getString(KEY_POLL_OPTIONS, null);
        String votesStr = prefs.getString(KEY_POLL_VOTES, null);
        int userVote = prefs.getInt(KEY_POLL_USER_VOTE, -1);

        // Если опроса нет — инициализируем дефолтный
        if (question == null || optionsStr == null || votesStr == null) {
            question = "Какая тема открыток вам ближе?";
            String[] options = {"Пейзажи", "Города", "Животные", "Искусство"};
            int[] votes = {0, 0, 0, 0};
            savePoll(context, question, options, votes, -1);
            return new PostcrossingPoll(question, java.util.Arrays.asList(options));
        }
        String[] options = optionsStr.split("\\|\\|");
        String[] votesArr = votesStr.split(",");
        int[] votes = new int[options.length];
        for (int i = 0; i < options.length; i++) {
            votes[i] = (i < votesArr.length) ? Integer.parseInt(votesArr[i]) : 0;
        }
        PostcrossingPoll poll = new PostcrossingPoll(question, java.util.Arrays.asList(options));
        poll.setVotes(votes);
        poll.setUserVote(userVote);
        return poll;
    }

    public void votePoll(Context context, int optionIndex) {
        PostcrossingPoll poll = getPoll(context);
        poll.vote(optionIndex);
        savePoll(context, poll.getQuestion(), poll.getOptions().toArray(new String[0]), poll.getVotes(), poll.getUserVote());
    }

    private void savePoll(Context context, String question, String[] options, int[] votes, int userVote) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String optionsStr = String.join("||", options);
        StringBuilder votesStr = new StringBuilder();
        for (int i = 0; i < votes.length; i++) {
            votesStr.append(votes[i]);
            if (i < votes.length - 1) votesStr.append(",");
        }
        prefs.edit()
                .putString(KEY_POLL_QUESTION, question)
                .putString(KEY_POLL_OPTIONS, optionsStr)
                .putString(KEY_POLL_VOTES, votesStr.toString())
                .putInt(KEY_POLL_USER_VOTE, userVote)
                .apply();
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
} 