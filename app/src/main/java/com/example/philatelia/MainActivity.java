package com.example.philatelia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private NavController navController;

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

        // Настройка навигации
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            bottomNavigationView = findViewById(R.id.bottom_navigation);

            // Настройка AppBarConfiguration без Toolbar
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_catalog,
                    R.id.nav_postcrossing,
                    R.id.nav_helper,
                    R.id.nav_cart,
                    R.id.nav_user
            ).build();
            
            // Настройка нижней навигации
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        // Обрабатываем интент, который запустил эту Activity
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Обрабатываем интент при возврате на эту Activity
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            String fragmentToLoad = intent.getExtras().getString("fragmentToLoad");
            if ("helper".equals(fragmentToLoad)) {
                String chatId = intent.getExtras().getString("chatId");
                Bundle args = new Bundle();
                if (chatId != null) {
                    args.putString("chatId", chatId);
                }
                if (navController != null) {
                    navController.navigate(R.id.nav_helper, args);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (AppBarConfiguration) null) || super.onSupportNavigateUp();
    }
}

