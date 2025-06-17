package com.example.philatelia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.models.Stamp;

import java.util.ArrayList;
import java.util.List;

public class StampAdapter extends RecyclerView.Adapter<StampAdapter.StampViewHolder> {
    private List<Stamp> stamps = new ArrayList<>();
    private final OnStampClickListener listener;
    private final OnAddToCartClickListener cartListener;

    public interface OnStampClickListener {
        void onStampClick(Stamp stamp);
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Stamp stamp);
    }

    public StampAdapter(OnStampClickListener listener, OnAddToCartClickListener cartListener) {
        this.listener = listener;
        this.cartListener = cartListener;
    }

    @NonNull
    @Override
    public StampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stamp, parent, false);
        return new StampViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StampViewHolder holder, int position) {
        Stamp stamp = stamps.get(position);
        holder.bind(stamp);
    }

    @Override
    public int getItemCount() {
        return stamps.size();
    }

    public void setStamps(List<Stamp> stamps) {
        this.stamps = stamps;
        notifyDataSetChanged();
    }

    class StampViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView titleView;
        private final TextView priceView;
        private final Button addToCartButton;

        StampViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.stamp_image);
            titleView = itemView.findViewById(R.id.stamp_title);
            priceView = itemView.findViewById(R.id.stamp_price);
            addToCartButton = itemView.findViewById(R.id.btn_add_to_cart);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onStampClick(stamps.get(position));
                }
            });

            addToCartButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    cartListener.onAddToCartClick(stamps.get(position));
                }
            });
        }

        void bind(Stamp stamp) {
            titleView.setText(stamp.getTitle());
            priceView.setText(stamp.getPrice());
            
            Glide.with(itemView.getContext())
                    .load(stamp.getImageUrl())
                    .placeholder(R.drawable.placeholder_stamp)
                    .error(R.drawable.error_stamp)
                    .into(imageView);
        }
    }
}

