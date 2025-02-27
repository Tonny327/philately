package com.example.philatelia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.philatelia.R;
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

        // Проверяем, есть ли цена, если нет - ставим "N"
        String price = (stamp.getPrice() == null || stamp.getPrice().isEmpty()) ? "N" : stamp.getPrice();
        holder.priceTextView.setText("Цена: " + price);

        // Загружаем изображение с помощью Glide
        Glide.with(context)
                .load(stamp.getImage())
                .placeholder(R.drawable.placeholder) // Заглушка, если картинка не загрузится
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return stamps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView catalogNumberTextView, nameTextView, releaseDateTextView, priceTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catalogNumberTextView = itemView.findViewById(R.id.catalogNumberTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            releaseDateTextView = itemView.findViewById(R.id.releaseDateTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

