package com.example.philatelia.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.philatelia.R;
import com.example.philatelia.adapters.CartAdapter;
import com.example.philatelia.helpers.CartManager;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.models.StampSet;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private CartManager cartManager;

    public CartFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        if (view == null) {
            Log.e("CatalogFragment", "Ошибка: не удалось загрузить макет!");
            return null;
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartManager = new CartManager(getContext());

        List<Stamp> cartStamps = cartManager.getCartStamps();
        List<StampSet> cartSets = cartManager.getCartSets();

        if (cartStamps == null) cartStamps = new ArrayList<>();
        if (cartSets == null) cartSets = new ArrayList<>();


        List<Object> cartItems = new ArrayList<>();
        cartItems.addAll(cartStamps);
        cartItems.addAll(cartSets);

        cartAdapter = new CartAdapter(getContext(), cartItems);
        recyclerView.setAdapter(cartAdapter);

        Button btnCheckout = view.findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> {
            cartManager.clearCart();
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Заказ оформлен!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}

