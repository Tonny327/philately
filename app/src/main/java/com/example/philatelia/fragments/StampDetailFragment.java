package com.example.philatelia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.example.philatelia.R;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.viewmodels.CartViewModel;

public class StampDetailFragment extends Fragment {

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

        // Инициализация views
        ImageView imageView = view.findViewById(R.id.stamp_image);
        TextView titleView = view.findViewById(R.id.stamp_title);
        TextView priceView = view.findViewById(R.id.stamp_price);
        MaterialButton addToCartButton = view.findViewById(R.id.btn_add_to_cart);

        // Установка данных
        titleView.setText(title);
        priceView.setText(price);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_stamp)
                .error(R.drawable.error_stamp)
                .into(imageView);

        // Обработка нажатия на кнопку
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        addToCartButton.setOnClickListener(v -> {
            CartItemEntity item = new CartItemEntity();
            item.title = title;
            item.price = price;
            double priceDouble = 0.0;
            try {
                String priceString = price.replaceAll("[^\\d,.]", "").replace(',', '.');
                priceDouble = Double.parseDouble(priceString);
            } catch (Exception e) {
                //
            }
            item.priceNum = priceDouble;
            item.imageUrl = imageUrl;
            item.quantity = 1;
            cartViewModel.addToCart(item);
            Toast.makeText(requireContext(), "Марка добавлена в корзину", Toast.LENGTH_SHORT).show();
        });
    }
} 