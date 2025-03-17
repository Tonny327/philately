package com.example.philatelia.adapters;

import android.content.Context;
import android.util.Log;
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
import com.example.philatelia.models.Stamp;

import java.util.List;

public class StampAdapter extends RecyclerView.Adapter<StampAdapter.ViewHolder> {
    private List<Stamp> stamps;
    private Context context;

    public StampAdapter(Context context, List<Stamp> stamps) {
        this.context = context;
        this.stamps = stamps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stamp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stamp stamp = stamps.get(position);

        holder.catalogNumberTextView.setText("Номер: " + stamp.getCatalogNumber());
        holder.nameTextView.setText("Марка «" + stamp.getName() + "»");
        holder.releaseDateTextView.setText("Дата выпуска: " + stamp.getReleaseDate());

        String price = (stamp.getPrice() == null || stamp.getPrice().isEmpty()) ? "N" : stamp.getPrice();
        holder.priceTextView.setText("Номинал: " + price + " BYN");

        Glide.with(context)
                .load(stamp.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);

        // ✅ Проверяем, не `null` ли кнопка перед `setOnClickListener`
        if (holder.addToCartButton != null) {
            holder.addToCartButton.setOnClickListener(v -> {
                CartManager cartManager = new CartManager(context);
                cartManager.addStampToCart(stamp);
                Toast.makeText(context, "Марка добавлена в корзину!", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("StampAdapter", "❌ Ошибка: addToCartButton == null!");
        }
    }



    @Override
    public int getItemCount() {
        return stamps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView catalogNumberTextView, nameTextView, releaseDateTextView, priceTextView;
        ImageView imageView;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catalogNumberTextView = itemView.findViewById(R.id.catalogNumberTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            releaseDateTextView = itemView.findViewById(R.id.releaseDateTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            imageView = itemView.findViewById(R.id.imageView);
            addToCartButton = itemView.findViewById(R.id.btnAddToCartStamp); // ✅ Обязательно!
        }
    }

}

