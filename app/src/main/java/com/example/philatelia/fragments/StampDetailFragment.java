package com.example.philatelia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.viewmodels.CartViewModel;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StampDetailFragment extends Fragment {
    private Random random = new Random();
    private final String[] categories = {"Редкая", "VIP", "Новинка", "Хит", "Лимитед", "Коллекция"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stamp_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем данные из аргументов
        Bundle args = getArguments();
        if (args == null) return;

        String title = args.getString("title", "");
        String price = args.getString("price", "");
        String imageUrl = args.getString("imageUrl", "");

        initViews(view, title, price, imageUrl);
        setupClickListeners(view, title, price, imageUrl);
    }

    private void initViews(View view, String title, String price, String imageUrl) {
        // Основные элементы
        ImageView stampImage = view.findViewById(R.id.stamp_image);
        TextView stampTitle = view.findViewById(R.id.stamp_title);
        TextView stampPrice = view.findViewById(R.id.stamp_price);
        TextView categoryBadge = view.findViewById(R.id.tv_category_badge);
        TextView tvRating = view.findViewById(R.id.tv_rating);
        TextView tvReviewsCount = view.findViewById(R.id.tv_reviews_count);
        TextView tvAvailability = view.findViewById(R.id.tv_availability);
        TextView tvArticle = view.findViewById(R.id.tv_article);

        // Дополнительная информация
        TextView tvStampType = view.findViewById(R.id.tv_stamp_type);
        TextView tvYear = view.findViewById(R.id.tv_year);
        TextView tvCountry = view.findViewById(R.id.tv_country);
        TextView tvCondition = view.findViewById(R.id.tv_condition);

        // Установка основных данных
        String cleanTitle = cleanStampTitle(title);
        stampTitle.setText(cleanTitle);
        
        String cleanPrice = extractPrice(price);
        stampPrice.setText(cleanPrice);

        // Загрузка изображения
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_stamp)
                .error(R.drawable.error_stamp)
                .into(stampImage);

        // Случайные данные для демонстрации
        setupRandomData(categoryBadge, tvRating, tvReviewsCount, tvAvailability, tvArticle, 
                       tvStampType, tvYear, tvCountry, tvCondition, title);
    }

    private void setupClickListeners(View view, String title, String price, String imageUrl) {
        // Кнопка назад
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                Navigation.findNavController(v).navigateUp();
            }
        });

        // Кнопка избранного
        ImageView btnFavorite = view.findViewById(R.id.btn_favorite);
        btnFavorite.setOnClickListener(v -> toggleFavorite(btnFavorite));

        // Кнопка поделиться
        ImageView btnShare = view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "🔗 Функция поделиться в разработке", Toast.LENGTH_SHORT).show();
        });

        // Кнопка добавления в корзину
        LinearLayout btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        
        btnAddToCart.setOnClickListener(v -> {
            // Анимация нажатия
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100);
                    });

            // Добавление в корзину
            CartItemEntity item = new CartItemEntity();
            item.title = cleanStampTitle(title);
            item.price = extractPrice(price);
            
            try {
                String priceString = price.replaceAll("[^\\d,.]", "").replace(',', '.');
                item.priceNum = Double.parseDouble(priceString);
            } catch (Exception e) {
                item.priceNum = 0.0;
            }
            
            item.imageUrl = imageUrl;
            item.quantity = 1;
            cartViewModel.addToCart(item);
            
            Toast.makeText(requireContext(), "✅ Марка добавлена в корзину", Toast.LENGTH_SHORT).show();
        });
    }

    private String cleanStampTitle(String title) {
        if (title == null) return "";
        
        // Убираем "В наличии" и артикул
        String cleaned = title.replaceFirst("В наличии\\s*\\n", "");
        
        // Убираем артикул (строку, начинающуюся с "Артикул:")
        cleaned = cleaned.replaceAll("\\nАртикул:.*", "");
        
        return cleaned.trim();
    }
    
    private String extractPrice(String priceString) {
        if (priceString == null) return "0.00 руб.";
        
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

    private String extractArticle(String title) {
        Pattern pattern = Pattern.compile("Артикул:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()) {
            return "Артикул: " + matcher.group(1);
        }
        return "Артикул: " + (800 + random.nextInt(200));
    }

    private void setupRandomData(TextView categoryBadge, TextView tvRating, TextView tvReviewsCount, 
                                TextView tvAvailability, TextView tvArticle, TextView tvStampType, 
                                TextView tvYear, TextView tvCountry, TextView tvCondition, String title) {
        
        // Случайная категория
        String category = categories[random.nextInt(categories.length)];
        categoryBadge.setText(category);

        // Случайный рейтинг
        float rating = 3.5f + random.nextFloat() * 1.5f;
        tvRating.setText(String.format("%.1f", rating));

        // Случайное количество отзывов
        int reviewsCount = 10 + random.nextInt(500);
        tvReviewsCount.setText(reviewsCount + " отзывов");

        // Статус наличия
        tvAvailability.setText("В наличии");

        // Артикул
        tvArticle.setText(extractArticle(title));

        // Тип марки
        tvStampType.setText("Художественная марка");

        // Случайный год
        int year = 2020 + random.nextInt(5);
        tvYear.setText(String.valueOf(year));

        // Страна
        tvCountry.setText("Беларусь");

        // Состояние
        String[] conditions = {"Отличное", "Очень хорошее", "Хорошее", "Удовлетворительное"};
        tvCondition.setText(conditions[random.nextInt(conditions.length)]);
    }

    private void toggleFavorite(ImageView btnFavorite) {
        boolean isFavorite = btnFavorite.getTag() != null && (Boolean) btnFavorite.getTag();
        
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            btnFavorite.setTag(false);
            Toast.makeText(requireContext(), "💔 Удалено из избранного", Toast.LENGTH_SHORT).show();
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            btnFavorite.setTag(true);
            Toast.makeText(requireContext(), "❤️ Добавлено в избранное", Toast.LENGTH_SHORT).show();
        }

        // Анимация
        btnFavorite.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(150)
                .withEndAction(() -> {
                    btnFavorite.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150);
                });
    }
} 