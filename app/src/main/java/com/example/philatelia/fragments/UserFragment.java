package com.example.philatelia.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.philatelia.LoginActivity;
import com.example.philatelia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserFragment extends Fragment {
    private TextView tvUserEmail;
    private Button btnLogout;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Инициализация Firebase Auth
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Находим элементы интерфейса
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Проверяем, авторизован ли пользователь
        if (user != null) {
            tvUserEmail.setText("Вы вошли как: " + user.getEmail());
        } else {
            tvUserEmail.setText("Вы не авторизованы");
        }

        // Обработчик нажатия на кнопку "Выйти"
        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void logoutUser() {
        auth.signOut(); // Выход из Firebase

        // Переход на экран авторизации
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish(); // Закрываем текущую активность
    }
}

