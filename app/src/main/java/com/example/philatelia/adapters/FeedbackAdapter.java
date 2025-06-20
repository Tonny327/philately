package com.example.philatelia.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.models.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private final Context context;
    private List<Feedback> feedbackList;

    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.username.setText(feedback.getUsername());
        holder.feedbackText.setText(feedback.getText());

        // Format timestamp to a relative time string (e.g., "5 minutes ago")
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                feedback.getTimestamp(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS);
        holder.timestamp.setText(timeAgo);

        Glide.with(context)
                .load(feedback.getAvatarUrl())
                .placeholder(R.drawable.avatar_placeholder)
                .circleCrop()
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
    
    public void updateFeedback(List<Feedback> newFeedbackList) {
        this.feedbackList = newFeedbackList;
        notifyDataSetChanged();
    }


    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username;
        TextView timestamp;
        TextView feedbackText;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.feedback_avatar);
            username = itemView.findViewById(R.id.feedback_username);
            timestamp = itemView.findViewById(R.id.feedback_timestamp);
            feedbackText = itemView.findViewById(R.id.feedback_text);
        }
    }
} 