package com.example.philatelia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.models.PostcrossingStamp;
import java.util.List;

public class PostcrossingStampAdapter extends RecyclerView.Adapter<PostcrossingStampAdapter.StampViewHolder> {
    private List<PostcrossingStamp> stamps;

    public interface OnStampClickListener {
        void onStampClick(PostcrossingStamp stamp);
    }

    private OnStampClickListener listener;

    public PostcrossingStampAdapter(List<PostcrossingStamp> stamps) {
        this.stamps = stamps;
    }

    public void setOnStampClickListener(OnStampClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postcrossing_stamp, parent, false);
        return new StampViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StampViewHolder holder, int position) {
        PostcrossingStamp stamp = stamps.get(position);
        holder.title.setText(stamp.getTitle());
        holder.price.setText(stamp.getPrice());
        Glide.with(holder.image.getContext())
                .load(stamp.getImageUrl())
                .placeholder(R.drawable.ic_stamp_placeholder)
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStampClick(stamp);
        });
    }

    @Override
    public int getItemCount() {
        return stamps == null ? 0 : stamps.size();
    }

    public void setStamps(List<PostcrossingStamp> stamps) {
        this.stamps = stamps;
        notifyDataSetChanged();
    }

    static class StampViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price;
        public StampViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.stamp_image);
            title = itemView.findViewById(R.id.stamp_title);
            price = itemView.findViewById(R.id.stamp_price);
        }
    }
} 