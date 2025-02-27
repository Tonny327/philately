package com.example.philatelia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.philatelia.R;
import com.example.philatelia.adapters.ChatAdapter;
import com.example.philatelia.helpers.AIHelper;
import com.example.philatelia.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelperFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText inputMessage;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private AIHelper aiHelper;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_helper, container, false);

        sendButton = view.findViewById(R.id.send_button);
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        inputMessage = view.findViewById(R.id.input_message);

        // ✅ Убрали обработчик OnQuickReplyClickListener
        chatAdapter = new ChatAdapter(messages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        // ✅ Инициализация AIHelper для запросов к Gemini
        aiHelper = new AIHelper();

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String userMessage = inputMessage.getText().toString().trim();
        if (!userMessage.isEmpty()) {
            messages.add(new ChatMessage(userMessage, true));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            inputMessage.setText("");

            fetchAIResponse(userMessage);
        }
    }

    private void fetchAIResponse(String userMessage) {
        executorService.execute(() -> {
            String aiResponse = aiHelper.getResponse(userMessage);
            if (isAdded()) { // Проверяем, что фрагмент активен
                requireActivity().runOnUiThread(() -> {
                    messages.add(new ChatMessage(aiResponse, false));
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown(); // Освобождаем ресурсы потока при уничтожении фрагмента
    }
}



