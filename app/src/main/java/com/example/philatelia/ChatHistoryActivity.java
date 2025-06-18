package com.example.philatelia;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.philatelia.adapters.ChatHistoryAdapter;
import com.example.philatelia.helpers.ChatManager;
import com.example.philatelia.models.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity implements ChatHistoryAdapter.OnChatInteractionListener {

    private ChatManager chatManager;
    private ChatHistoryAdapter adapter;
    private List<Chat> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("История чатов");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatManager = new ChatManager(this);
        chatList = chatManager.getChats();

        RecyclerView recyclerView = findViewById(R.id.chats_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatHistoryAdapter(chatList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_new_chat);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ChatHistoryActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "helper");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshChatList();
    }

    private void refreshChatList() {
        chatList = chatManager.getChats();
        adapter.updateChats(chatList);
    }

    @Override
    public void onChatClicked(Chat chat) {
        Intent intent = new Intent(ChatHistoryActivity.this, MainActivity.class);
        intent.putExtra("fragmentToLoad", "helper");
        intent.putExtra("chatId", chat.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteClicked(Chat chat) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить чат")
                .setMessage("Вы уверены, что хотите удалить этот чат?")
                .setPositiveButton("Да", (dialog, which) -> {
                    chatManager.deleteChat(chat.getId());
                    refreshChatList();
                })
                .setNegativeButton("Нет", null)
                .show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 