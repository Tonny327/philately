package com.example.philatelia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.data.CartItemEntity;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    public interface CartActionListener {
        void onIncrease(CartItemEntity item);
        void onDecrease(CartItemEntity item);
        void onDelete(CartItemEntity item);
    }

    private List<CartItemEntity> items;
    private final CartActionListener listener;

    public CartAdapter(List<CartItemEntity> items, CartActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<CartItemEntity> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemEntity item = items.get(position);
        holder.title.setText(item.title);
        holder.price.setText(item.price);
        holder.quantity.setText(String.valueOf(item.quantity));
        Glide.with(holder.image.getContext())
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_stamp_placeholder)
                .into(holder.image);

        holder.btnIncrease.setOnClickListener(v -> {
            listener.onIncrease(item);
            notifyDataSetChanged();
        });
        holder.btnDecrease.setOnClickListener(v -> {
            listener.onDecrease(item);
            notifyDataSetChanged();
        });
        holder.btnDelete.setOnClickListener(v -> {
            listener.onDelete(item);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, quantity;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            price = itemView.findViewById(R.id.item_price);
            quantity = itemView.findViewById(R.id.item_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
} 