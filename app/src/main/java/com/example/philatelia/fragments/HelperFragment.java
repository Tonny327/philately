package com.example.philatelia.fragments;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.philatelia.ChatHistoryActivity;
import com.example.philatelia.R;
import com.example.philatelia.adapters.ChatAdapter;
import com.example.philatelia.data.StampRepository;
import com.example.philatelia.helpers.AIHelper;
import com.example.philatelia.helpers.ChatManager;
import com.example.philatelia.models.ChatMessage;
import com.example.philatelia.models.Chat;
import com.example.philatelia.models.Stamp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelperFragment extends Fragment implements ChatAdapter.AnimationListener {
    private RecyclerView chatRecyclerView;
    private EditText inputMessage;
    private ImageButton sendButton;
    private ImageButton historyButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private AIHelper aiHelper;
    private StampRepository stampRepository;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ValueAnimator currentAnimator;
    private boolean isGenerating = false;

    private ChatManager chatManager;
    private String currentChatId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_helper, container, false);

        chatManager = new ChatManager(getContext());
        initViews(view);
        setupRecyclerView();
        loadChatFromArguments();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        sendButton = view.findViewById(R.id.send_button);
        historyButton = view.findViewById(R.id.history_button);
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        inputMessage = view.findViewById(R.id.input_message);
        aiHelper = new AIHelper();
        stampRepository = new StampRepository();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages, this, chatRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void loadChatFromArguments() {
        if (getArguments() != null && getArguments().containsKey("chatId")) {
            currentChatId = getArguments().getString("chatId");
            Chat chat = chatManager.getChatById(currentChatId);
            if (chat != null) {
                messages.clear();
                messages.addAll(chat.getMessages());
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }
        } else {
            // New chat, currentChatId is null
        }
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> sendMessage());
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void stopGeneration() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
    }

    private void sendMessage() {
        String userMessage = inputMessage.getText().toString().trim();
        if (!userMessage.isEmpty()) {
            ChatMessage message = new ChatMessage(userMessage, true);
            messages.add(message);
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.scrollToPosition(messages.size() - 1);
            inputMessage.setText("");

            if (currentChatId == null) {
                Chat newChat = chatManager.createNewChat(new ArrayList<>(messages));
                currentChatId = newChat.getId();
            } else {
                chatManager.addMessageToChat(currentChatId, message);
            }

            if (isGreeting(userMessage)) {
                showGreetingResponse();
            } else if (isStatusCheck(userMessage)) {
                showStatusResponse();
            } else {
                fetchAIResponse(userMessage);
            }
        }
    }

    private boolean isStatusCheck(String message) {
        String lowerCaseMessage = message.toLowerCase();
        return lowerCaseMessage.contains("как дела");
    }

    private void showStatusResponse() {
        String response = "Спасибо, у меня все отлично! Я готов помочь вам исследовать мир филателии. О какой марке вы бы хотели узнать?";
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                messages.add(new ChatMessage(response, false));
                chatAdapter.notifyItemInserted(messages.size() - 1);
                chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
            });
        }
    }

    private boolean isGreeting(String message) {
        String lowerCaseMessage = message.toLowerCase();
        return lowerCaseMessage.equals("привет") ||
               lowerCaseMessage.equals("здравствуй") ||
               lowerCaseMessage.equals("добрый день") ||
               lowerCaseMessage.equals("добрый вечер");
    }

    private void showGreetingResponse() {
        String greetingResponse = "Здравствуйте! Чем могу помочь?";
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                messages.add(new ChatMessage(greetingResponse, false));
                chatAdapter.notifyItemInserted(messages.size() - 1);
                chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
            });
        }
    }

    private void fetchAIResponse(String userMessage) {
        setGeneratingState(true);
        // Добавляем сообщение "Думаю..."
        requireActivity().runOnUiThread(() -> {
            messages.add(new ChatMessage("Думаю...", false));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        });

        executorService.execute(() -> {
            String aiResponse;
            try {
                List<Stamp> stamps = stampRepository.getStampsFromAssets(getContext());
                List<Stamp> relevantStamps = findRelevantStamps(userMessage, stamps);
                String context = "";
                if (!relevantStamps.isEmpty()) {
                    context = "Вот информация о марках из каталога: " + new Gson().toJson(relevantStamps);
                }

                String finalQuery = context + "\n\nИсходя из этого контекста (если он есть), ответь на вопрос: " + userMessage;
                aiResponse = aiHelper.getResponse(finalQuery);

            } catch (Exception e) {
                Log.e("HelperFragment", "Ошибка при получении ответа от AI", e);
                aiResponse = "Произошла ошибка при обработке вашего запроса.";
            }

            if (isAdded()) {
                final String finalAiResponse = aiResponse;
                ChatMessage aiMessage = new ChatMessage(finalAiResponse, false);

                if (currentChatId != null) {
                    chatManager.addMessageToChat(currentChatId, aiMessage);
                }

                requireActivity().runOnUiThread(() -> {
                    messages.remove(messages.size() - 1);
                    chatAdapter.notifyItemRemoved(messages.size());
                    
                    messages.add(aiMessage);
                    chatAdapter.setPositionToAnimate(messages.size() - 1);
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
                });
            } else {
                setGeneratingState(false);
            }
        });
    }

    private List<Stamp> findRelevantStamps(String userMessage, List<Stamp> allStamps) {
        List<Stamp> relevant = new ArrayList<>();
        String[] keywords = userMessage.toLowerCase().split("\\s+");
        for (Stamp stamp : allStamps) {
            for (String keyword : keywords) {
                if (stamp.getTitle().toLowerCase().contains(keyword)) {
                    relevant.add(stamp);
                    break; 
                }
            }
        }
        return relevant;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown(); // Освобождаем ресурсы потока при уничтожении фрагмента
    }

    @Override
    public void onAnimationStarted(ValueAnimator animator) {
        this.currentAnimator = animator;
        setGeneratingState(true);
    }

    @Override
    public void onAnimationFinished() {
        this.currentAnimator = null;
        setGeneratingState(false);
    }

    private void setGeneratingState(boolean generating) {
        isGenerating = generating;
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                inputMessage.setEnabled(!generating);
                if (generating) {
                    sendButton.setImageResource(R.drawable.ic_stop_generation);
                    sendButton.setOnClickListener(v -> stopGeneration());
                } else {
                    sendButton.setImageResource(R.drawable.ic_send);
                    sendButton.setOnClickListener(v -> sendMessage());
                }
            });
        }
    }
}



