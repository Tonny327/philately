package com.example.philatelia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.philatelia.R;
import com.example.philatelia.models.Chat;
import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private final OnChatInteractionListener listener;

    public interface OnChatInteractionListener {
        void onChatClicked(Chat chat);
        void onDeleteClicked(Chat chat);
    }

    public ChatHistoryAdapter(List<Chat> chatList, OnChatInteractionListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChats(List<Chat> newChats) {
        this.chatList = newChats;
        notifyDataSetChanged();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageButton deleteButton;

        ChatViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.chat_title_text_view);
            deleteButton = itemView.findViewById(R.id.delete_chat_button);
        }

        void bind(final Chat chat, final OnChatInteractionListener listener) {
            titleTextView.setText(chat.getTitle());
            itemView.setOnClickListener(v -> listener.onChatClicked(chat));
            deleteButton.setOnClickListener(v -> listener.onDeleteClicked(chat));
        }
    }
} 