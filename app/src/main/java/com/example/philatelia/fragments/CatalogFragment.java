package com.example.philatelia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.philatelia.R;
import com.example.philatelia.adapters.StampAdapter;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.viewmodels.CartViewModel;
import com.example.philatelia.viewmodels.StampViewModel;

public class CatalogFragment extends Fragment implements StampAdapter.OnStampClickListener, StampAdapter.OnAddToCartClickListener {
    private StampViewModel viewModel;
    private CartViewModel cartViewModel;
    private StampAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация views
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        errorText = view.findViewById(R.id.error_text);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Настройка адаптера
        adapter = new StampAdapter(this, this);
        recyclerView.setAdapter(adapter);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(StampViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // Наблюдение за данными
        viewModel.getStamps().observe(getViewLifecycleOwner(), stamps -> {
            adapter.setStamps(stamps);
            swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                errorText.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                errorText.setText(error);
                errorText.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
        } else {
                errorText.setVisibility(View.GONE);
            }
        });

        // Настройка SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            errorText.setVisibility(View.GONE);
            viewModel.loadStamps(requireContext());
        });

        // Загрузка данных
        viewModel.loadStamps(requireContext());
    }

    @Override
    public void onStampClick(Stamp stamp) {
        // Создаем Bundle с данными марки
        Bundle bundle = new Bundle();
        bundle.putString("title", stamp.getTitle());
        bundle.putString("price", stamp.getPrice());
        bundle.putString("imageUrl", stamp.getImageUrl());

        // Навигация к детальному фрагменту
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_catalog_to_stampDetailFragment, bundle);
    }

    @Override
    public void onAddToCartClick(Stamp stamp) {
        CartItemEntity item = new CartItemEntity();
        item.title = stamp.getTitle();
        item.price = stamp.getPrice();
        try {
            item.priceNum = Double.parseDouble(stamp.getPrice().replaceAll("[^0-9.,]", "").replace(",", "."));
            item.priceKopecks = (int) Math.round(item.priceNum * 100);
        } catch (Exception e) {
            item.priceNum = 0.0;
            item.priceKopecks = 0;
        }
        item.imageUrl = stamp.getImageUrl();
        item.quantity = 1;
        // item.stampId = null; // если появится уникальный id, добавить сюда
        cartViewModel.addToCart(item);
        Toast.makeText(requireContext(), "Марка добавлена в корзину", Toast.LENGTH_SHORT).show();
    }
}

