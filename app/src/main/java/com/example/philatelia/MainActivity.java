package com.example.philatelia;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.philatelia.fragments.CatalogFragment;
import com.example.philatelia.fragments.PostcrossingFragment;
import com.example.philatelia.fragments.ReviewsFragment;
import com.example.philatelia.fragments.HelperFragment;
import com.example.philatelia.fragments.LinksFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Загрузка CatalogFragment при запуске приложения
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CatalogFragment())
                    .commit();
        }

        // Слушатель кликов на пункты меню
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            } else if (id == R.id.nav_postcrossing) {
                selectedFragment = new PostcrossingFragment();
            } else if (id == R.id.nav_reviews) {
                selectedFragment = new ReviewsFragment();
            } else if (id == R.id.nav_helper) {
                selectedFragment = new HelperFragment();
            } else if (id == R.id.nav_links) {
                selectedFragment = new LinksFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;

        });


        // При старте приложения сразу выберем пункт "Каталоги"
        bottomNavigationView.setSelectedItemId(R.id.nav_catalog);

    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // Возвращаемся к предыдущему фрагменту
        } else {
            super.onBackPressed(); // Если фрагментов нет, выходим
        }
    }

}
