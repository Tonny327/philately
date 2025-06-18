package com.example.philatelia.models.deepseek;

import java.util.List;

public class ChatResponse {
    public List<Choice> choices;

    public static class Choice {
        public Message message;
    }

    public static class Message {
        public String content;
    }

    public String getFirstChoiceContent() {
        if (choices != null && !choices.isEmpty()) {
            Choice firstChoice = choices.get(0);
            if (firstChoice != null && firstChoice.message != null) {
                return firstChoice.message.content;
            }
        }
        return null;
    }
} 