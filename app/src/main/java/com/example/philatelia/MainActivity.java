package com.example.philatelia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.philatelia.fragments.CatalogFragment;
import com.example.philatelia.fragments.PostcrossingFragment;
import com.example.philatelia.fragments.CartFragment;
import com.example.philatelia.fragments.HelperFragment;
import com.example.philatelia.fragments.UserFragment;
import com.example.philatelia.R;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Если пользователь НЕ вошел – отправляем его на экран авторизации
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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

            if (item.getItemId() == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            } else if (item.getItemId() == R.id.nav_postcrossing) {
                selectedFragment = new PostcrossingFragment();
            } else if (item.getItemId() == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (item.getItemId() == R.id.nav_helper) {
                selectedFragment = new HelperFragment();
            } else if (item.getItemId() == R.id.nav_user) {
                selectedFragment = new UserFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // При старте приложения сразу выбираем пункт "Каталог"
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

