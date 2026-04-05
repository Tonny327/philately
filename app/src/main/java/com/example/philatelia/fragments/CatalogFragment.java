package com.example.philatelia.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.philatelia.R;
import com.example.philatelia.adapters.StampAdapter;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.data.StampRepository;
import com.example.philatelia.helpers.PriceParseUtils;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.viewmodels.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {
    private RecyclerView recyclerView;
    private StampAdapter adapter;
    private LinearLayout emptyState;
    private EditText etSearch;
    private StampRepository stampRepository;
    private List<Stamp> allStamps = new ArrayList<>();
    private List<Stamp> filteredStamps = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        stampRepository = new StampRepository();
        
        initViews(view);
        setupRecyclerView();
        setupSearch();
        loadStamps();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyState = view.findViewById(R.id.empty_state);
        etSearch = view.findViewById(R.id.et_search);
    }

    private void setupRecyclerView() {
        adapter = new StampAdapter(
            this::onStampClick,
            this::onAddToCartClick
        );
        
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStamps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadStamps() {
        try {
            allStamps = stampRepository.getStampsFromAssets(requireContext());
            filteredStamps = new ArrayList<>(allStamps);
            adapter.setStamps(filteredStamps);
            updateEmptyState();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void filterStamps(String query) {
        if (query.isEmpty()) {
            filteredStamps = new ArrayList<>(allStamps);
        } else {
            filteredStamps = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            
            for (Stamp stamp : allStamps) {
                if (stamp.getTitle() != null && 
                    stamp.getTitle().toLowerCase().contains(lowerQuery)) {
                    filteredStamps.add(stamp);
                }
            }
        }
        
        adapter.setStamps(filteredStamps);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredStamps.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void onStampClick(Stamp stamp) {
        Bundle bundle = new Bundle();
        bundle.putString("title", stamp.getTitle());
        bundle.putString("price", stamp.getPrice());
        bundle.putString("imageUrl", stamp.getImageUrl());
        
        Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_catalog_to_stampDetailFragment, bundle);
    }

    private void onAddToCartClick(Stamp stamp) {
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        CartItemEntity item = new CartItemEntity();
        item.title = stamp.getTitle() != null ? stamp.getTitle() : "";
        item.imageUrl = stamp.getImageUrl() != null ? stamp.getImageUrl() : "";
        PriceParseUtils.applyPriceFields(item, stamp.getPrice());
        item.quantity = 1;
        item.stampId = PriceParseUtils.stableStampId(
                stamp.getTitle(), stamp.getPrice(), stamp.getImageUrl());
        cartViewModel.addToCart(item);

        Toast.makeText(requireContext(),
                "🛒 Марка добавлена в корзину!",
                Toast.LENGTH_SHORT).show();
    }
}

