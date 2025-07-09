package com.example.philatelia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.models.Stamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StampAdapter extends RecyclerView.Adapter<StampAdapter.StampViewHolder> {
    private List<Stamp> stamps = new ArrayList<>();
    private final OnStampClickListener listener;
    private final OnAddToCartClickListener cartListener;
    private final Random random = new Random();

    // Массивы для случайных данных
    private final String[] categories = {"Редкая", "VIP", "Новинка", "Хит", "Лимитед", "Коллекция"};
    private final String[] availability = {"В наличии", "Ограничено", "Последние", "Заканчивается"};
    private final int[] availabilityColors = {0xFF4CAF50, 0xFFFF9800, 0xFFF44336, 0xFFFF5722};

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
        private final ImageView stampImage;
        private final TextView stampTitle;
        private final TextView stampPrice;
        private final LinearLayout btnAddToCart;
        private final ImageView ivFavorite;
        private final TextView tvCategory;
        private final TextView tvRating;
        private final TextView tvReviews;
        private final TextView tvAvailability;

        StampViewHolder(@NonNull View itemView) {
            super(itemView);
            stampImage = itemView.findViewById(R.id.stamp_image);
            stampTitle = itemView.findViewById(R.id.stamp_title);
            stampPrice = itemView.findViewById(R.id.stamp_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvReviews = itemView.findViewById(R.id.tv_reviews);
            tvAvailability = itemView.findViewById(R.id.tv_availability);

            // Клик по карточке (НЕ по кнопке корзины)
            View cardClickArea = itemView.findViewById(R.id.card_click_area);
            if (cardClickArea != null) {
                cardClickArea.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onStampClick(stamps.get(position));
                    }
                });
            }

            // ИСПРАВЛЕННЫЙ КЛИК ПО КНОПКЕ КОРЗИНЫ
            btnAddToCart.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    cartListener.onAddToCartClick(stamps.get(position));
                    
                    // Анимация клика
                    v.animate()
                            .scaleX(0.9f)
                            .scaleY(0.9f)
                            .setDuration(100)
                            .withEndAction(() -> {
                                v.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(100);
                            });
                }
            });

            // Клик по избранному
            ivFavorite.setOnClickListener(v -> toggleFavorite());
        }

        void bind(Stamp stamp) {
            // Основная информация - очищаем заголовок от лишней информации
            String cleanTitle = cleanStampTitle(stamp.getTitle());
            stampTitle.setText(cleanTitle);
            
            // ИСПРАВЛЕННОЕ ФОРМАТИРОВАНИЕ ЦЕНЫ - В ОДНУ СТРОКУ
            String cleanPrice = extractPrice(stamp.getPrice());
            stampPrice.setText(cleanPrice);
            
            // Загрузка изображения
            Glide.with(itemView.getContext())
                    .load(stamp.getImageUrl())
                    .placeholder(R.drawable.placeholder_stamp)
                    .error(R.drawable.error_stamp)
                    .into(stampImage);

            // Случайные данные для демонстрации
            setupRandomData();
        }

        private void setupRandomData() {
            // Случайная категория
            String category = categories[random.nextInt(categories.length)];
            tvCategory.setText(category);

            // Случайный рейтинг
            float rating = 3.5f + random.nextFloat() * 1.5f; // от 3.5 до 5.0
            tvRating.setText(String.format("%.1f", rating));

            // Случайное количество отзывов
            int reviewsCount = 10 + random.nextInt(500);
            tvReviews.setText(String.format("(%d отзывов)", reviewsCount));

            // Случайная доступность
            int availabilityIndex = random.nextInt(availability.length);
            tvAvailability.setText(availability[availabilityIndex]);
            tvAvailability.setTextColor(availabilityColors[availabilityIndex]);
        }

        private String cleanStampTitle(String title) {
            if (title == null) return "";
            
            // Убираем "В наличии" и артикул
            String cleaned = title.replaceFirst("В наличии\\s*\\n", "");
            cleaned = cleaned.replaceAll("\\nАртикул:.*", "");
            cleaned = cleaned.trim();
            
            // Сокращаем длину
            if (cleaned.length() > 80) {
                cleaned = cleaned.substring(0, 77) + "...";
            }
            
            return cleaned;
        }
        
        private String extractPrice(String priceString) {
            if (priceString == null) return "0 руб.";
            
            // Убираем "В корзину" если есть
            String cleaned = priceString.replace("В корзину", "").trim();
            
            // Если уже содержит "руб.", возвращаем как есть
            if (cleaned.contains("руб.")) {
                return cleaned;
            }
            
            // Пытаемся извлечь числовое значение
            try {
                String numericPrice = cleaned.replaceAll("[^0-9.,]", "").replace(",", ".");
                if (!numericPrice.isEmpty()) {
                    double priceValue = Double.parseDouble(numericPrice);
                    return String.format("%.2f руб.", priceValue);
                }
            } catch (Exception e) {
                // Игнорируем ошибки парсинга
            }
            
            return cleaned.isEmpty() ? "Цена не указана" : cleaned;
        }

        private void toggleFavorite() {
            // Простая анимация переключения избранного
            boolean isFavorite = ivFavorite.getTag() != null && (Boolean) ivFavorite.getTag();
            
            if (isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_favorite_border);
                ivFavorite.setTag(false);
            } else {
                ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
                ivFavorite.setTag(true);
            }

            // Анимация
            ivFavorite.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        ivFavorite.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(150);
                    });
        }
    }
}

