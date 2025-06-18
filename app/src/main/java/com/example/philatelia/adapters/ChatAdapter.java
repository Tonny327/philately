package com.example.philatelia.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface AnimationListener {
        void onAnimationStarted(ValueAnimator animator);
        void onAnimationFinished();
    }

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private List<ChatMessage> messages;
    private AnimationListener animationListener;
    private RecyclerView recyclerView;
    private int positionToAnimate = -1;

    // Конструктор
    public ChatAdapter(List<ChatMessage> messages, AnimationListener listener, RecyclerView recyclerView) {
        this.messages = messages;
        this.animationListener = listener;
        this.recyclerView = recyclerView;
    }

    public void setPositionToAnimate(int position) {
        this.positionToAnimate = position;
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
            boolean shouldAnimate = (position == positionToAnimate);
            ((AIViewHolder) holder).bind(message, shouldAnimate);
            if (shouldAnimate) {
                positionToAnimate = -1; // Сбрасываем после использования
            }
        } else {
            ((UserViewHolder) holder).bind(message);
        }
    }

    class AIViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ValueAnimator animator;

        AIViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.ai_message_text);
        }

        void bind(ChatMessage message, boolean animate) {
            // Отменяем старую анимацию, если она еще идет
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }

            if (animate) {
                animateMessage(message.getMessage());
            } else {
                messageText.setText(message.getMessage());
            }
        }

        private void animateMessage(final String text) {
            animator = ValueAnimator.ofInt(0, text.length());
            animator.setDuration(text.length() * 50); // Скорость печати
            animator.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                messageText.setText(text.substring(0, animatedValue));
                if (recyclerView != null) {
                    recyclerView.post(() -> recyclerView.smoothScrollToPosition(getAdapterPosition()));
                }
            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (animationListener != null) {
                        animationListener.onAnimationStarted(animator);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (animationListener != null) {
                        animationListener.onAnimationFinished();
                    }
                }
            });

            animator.start();
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ValueAnimator animator;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.user_message_text);
        }

        void bind(ChatMessage message) {
            textView.setText(message.getMessage());
        }
        
        public void animateText(String text) {
            if (animator != null) {
                animator.cancel();
            }
            
            textView.setText(""); // Очищаем текст перед анимацией
            animator = ValueAnimator.ofInt(0, text.length());
            animator.setDuration(text.length() * 50L); // Длительность зависит от длины текста
            animator.addUpdateListener(animation -> {
                int animatedValue = (int) animation.getAnimatedValue();
                textView.setText(text.substring(0, animatedValue));
            });
            animator.start();
        }
    }
}



