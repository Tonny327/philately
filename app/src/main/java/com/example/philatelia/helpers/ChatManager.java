package com.example.philatelia.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.philatelia.models.Chat;
import com.example.philatelia.models.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatManager {
    private static final String PREFS_NAME = "ChatHistoryPrefs";
    private static final String CHATS_KEY = "Chats";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ChatManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<Chat> getChats() {
        String json = sharedPreferences.getString(CHATS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Chat>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveChats(List<Chat> chats) {
        String json = gson.toJson(chats);
        sharedPreferences.edit().putString(CHATS_KEY, json).apply();
    }

    public Chat getChatById(String chatId) {
        List<Chat> chats = getChats();
        for (Chat chat : chats) {
            if (chat.getId().equals(chatId)) {
                return chat;
            }
        }
        return null;
    }

    public Chat createNewChat(List<ChatMessage> messages) {
        String title = "Новый чат";
        if (!messages.isEmpty()) {
            title = messages.get(0).getMessage();
            if (title.length() > 30) {
                title = title.substring(0, 30) + "...";
            }
        }
        Chat newChat = new Chat(title, messages);
        List<Chat> chats = getChats();
        chats.add(0, newChat); // Add to the top of the list
        saveChats(chats);
        return newChat;
    }

    public void addMessageToChat(String chatId, ChatMessage message) {
        List<Chat> chats = getChats();
        for (Chat chat : chats) {
            if (chat.getId().equals(chatId)) {
                chat.getMessages().add(message);
                chat.setTimestamp(System.currentTimeMillis());
                break;
            }
        }
        saveChats(chats);
    }

    public void deleteChat(String chatId) {
        List<Chat> chats = getChats();
        chats.removeIf(chat -> chat.getId().equals(chatId));
        saveChats(chats);
    }

    public void renameChat(String chatId, String newTitle) {
        List<Chat> chats = getChats();
        for (Chat chat : chats) {
            if (chat.getId().equals(chatId)) {
                chat.setTitle(newTitle);
                break;
            }
        }
        saveChats(chats);
    }
} 