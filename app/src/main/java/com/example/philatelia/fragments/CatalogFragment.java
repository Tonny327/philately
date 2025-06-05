package com.example.philatelia.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.philatelia.R;
import com.example.philatelia.adapters.StampSetAdapter;
import com.example.philatelia.adapters.StampAdapter;
import com.example.philatelia.models.StampSet;
import com.example.philatelia.models.Stamp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CatalogFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayout backButton;
    private TextView tvHeader;
    private StampSetAdapter stampSetAdapter;
    private StampAdapter stampAdapter;
    private List<StampSet> stampSetList;
    private Map<String, List<Stamp>> stampsByYear;
    private boolean isShowingStampSets = true; // Флаг: true = показываем наборы, false = показываем марки
    private String selectedYear = "2024";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        backButton = view.findViewById(R.id.backButton);
        tvHeader = view.findViewById(R.id.tvHeader);


        // ✅ Обработчик системной кнопки "Назад"
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPressed();
            }
        });

        // ✅ Обработчик кнопки "Назад" на экране
        backButton.setOnClickListener(v -> handleBackPressed());

        // Загружаем данные
        stampSetList = loadStampSets();
        if (stampSetList == null) {
            Log.e("CatalogFragment", "❌ Ошибка: список наборов марок пуст!");
            return view;
        }
        stampsByYear = loadStamps();

        // Отображаем наборы марок
        showStampSets();

        return view;
    }
    private void handleBackPressed() {
        if (!isShowingStampSets) {
            // Если сейчас показываются марки - вернуться к наборам марок
            showStampSets();
        } else {
            // Если уже на экране наборов - выйти из приложения
            requireActivity().finish();
        }
    }




    private void showStampSets() {
        isShowingStampSets = true;

        if (backButton != null) {
            backButton.setVisibility(View.GONE); // Прячем кнопку "Назад"
        }

        stampSetAdapter = new StampSetAdapter(requireContext(), stampSetList, year -> {
            selectedYear = year;
            showStampsForYear();
        });
        recyclerView.setAdapter(stampSetAdapter);
    }

    private void showStampsForYear() {
        isShowingStampSets = false;

        if (backButton != null) {
            backButton.setVisibility(View.VISIBLE); // Показываем кнопку "Назад"
        }
        if (tvHeader != null) {
            tvHeader.setText("Марки за " + selectedYear + " год"); // 🔥 Обновляем заголовок
        }

        List<Stamp> stampList = stampsByYear.get(selectedYear);
        if (stampList == null || stampList.isEmpty()) {
            Log.e("CatalogFragment", "❌ Ошибка: Марки за " + selectedYear + " не найдены!");
            return;
        }

        stampAdapter = new StampAdapter(requireContext(), stampList);
        recyclerView.setAdapter(stampAdapter);
    }




    private List<StampSet> loadStampSets() {
        String json = loadJSONFromAsset("stamps_set_by_year.json");

        if (json == null) {
            Log.e("CatalogFragment", "❌ Ошибка: JSON-файл `stamps_set_by_year.json` не найден!");
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<StampSet>>>() {}.getType();
        Map<String, List<StampSet>> stampSetsByYear = gson.fromJson(json, type);

        if (stampSetsByYear == null) {
            Log.e("CatalogFragment", "❌ Ошибка: JSON-файл `stamps_set_by_year.json` пустой или неправильно отформатирован!");
            return null;
        }

        List<StampSet> allStampSets = new ArrayList<>();

        // ✅ Добавляем год вручную из ключа JSON
        for (String year : stampSetsByYear.keySet()) {
            for (StampSet set : stampSetsByYear.get(year)) {
                allStampSets.add(new StampSet(set.getName(), set.getPrice(), set.getImage(), set.getImage2(), year));
            }
        }

        if (allStampSets.isEmpty()) {
            Log.e("CatalogFragment", "❌ Ошибка: В JSON нет данных!");
            return null;
        }

        return allStampSets;
    }



    private Map<String, List<Stamp>> loadStamps() {
        String json = loadJSONFromAsset("stamps_by_year.json");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<Stamp>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private String loadJSONFromAsset(String fileName) {
        try {
            InputStream is = requireContext().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

