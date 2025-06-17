package com.example.philatelia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.philatelia.R;
import com.example.philatelia.adapters.CartAdapter;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.viewmodels.CartViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class CartFragment extends Fragment implements CartAdapter.CartActionListener {
    private CartViewModel viewModel;
    private CartAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyPlaceholder;
    private TextView totalSum;
    private MaterialButton checkoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        emptyPlaceholder = view.findViewById(R.id.empty_placeholder);
        totalSum = view.findViewById(R.id.total_sum);
        checkoutButton = view.findViewById(R.id.checkout_button);

        adapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            if (items == null || items.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyPlaceholder.setVisibility(View.VISIBLE);
                checkoutButton.setEnabled(false);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyPlaceholder.setVisibility(View.GONE);
                checkoutButton.setEnabled(true);
            }
        });
        viewModel.getTotalSum().observe(getViewLifecycleOwner(), sum -> {
            totalSum.setText("Общая сумма: " + sum + " руб.");
        });

        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Оформление заказа пока не реализовано", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onIncrease(CartItemEntity item) {
        viewModel.increaseQuantity(item);
    }

    @Override
    public void onDecrease(CartItemEntity item) {
        viewModel.decreaseQuantity(item);
    }

    @Override
    public void onDelete(CartItemEntity item) {
        viewModel.removeFromCart(item);
    }
} 