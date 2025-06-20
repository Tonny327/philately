package com.example.philatelia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.philatelia.R;
import com.example.philatelia.models.ChatMessage;
import java.util.List;
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<ChatMessage> messages;
    private RecyclerView recyclerView;
    private final Markwon markwon;

    // Конструктор
    public ChatAdapter(List<ChatMessage> messages, RecyclerView recyclerView, Markwon markwon) {
        this.messages = messages;
        this.recyclerView = recyclerView;
        this.markwon = markwon;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_AI) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_message, parent, false);
            return new AIViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_AI) {
            ((AIViewHolder) holder).bind(message);
        } else {
            ((UserViewHolder) holder).bind(message);
        }
    }

    class AIViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        AIViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.ai_message_text);
        }

        void bind(ChatMessage message) {
            markwon.setMarkdown(messageText, message.getMessage());
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.user_message_text);
        }

        void bind(ChatMessage message) {
            textView.setText(message.getMessage());
        }
    }
}



