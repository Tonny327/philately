package com.example.philatelia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.helpers.CartManager;
import com.example.philatelia.models.StampSet;

import java.util.List;

public class StampSetAdapter extends RecyclerView.Adapter<StampSetAdapter.ViewHolder> {
    private List<StampSet> stampSets;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String year);
    }

    public StampSetAdapter(Context context, List<StampSet> stampSets, OnItemClickListener listener) {
        this.context = context;
        this.stampSets = stampSets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stamp_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StampSet stampSet = stampSets.get(position);

        holder.nameTextView.setText(stampSet.getName());
        holder.priceTextView.setText("Цена: " + stampSet.getPrice());

        Glide.with(context)
                .load(stampSet.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView1);

        Glide.with(context)
                .load(stampSet.getImage2())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView2);

        // ✅ Проверяем, что кнопка не null перед назначением слушателя
        if (holder.addToCartButton != null) {
            holder.addToCartButton.setOnClickListener(v -> {
                CartManager cartManager = new CartManager(context);
                cartManager.addStampSetToCart(stampSet);
                Toast.makeText(context, "Набор добавлен в корзину!", Toast.LENGTH_SHORT).show();
            });
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(stampSet.getYear()));


    }

    @Override
    public int getItemCount() {
        return stampSets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView;
        ImageView imageView1, imageView2;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
            addToCartButton = itemView.findViewById(R.id.btnAddToCartSet); // ✅ Правильное инициализирование
        }
    }
}

