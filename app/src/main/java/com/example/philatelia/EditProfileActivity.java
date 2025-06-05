package com.example.philatelia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etLastName, etFirstName, etMiddleName;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        Button btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));


        // Загружаем сохранённые данные (если есть)
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        etLastName.setText(sharedPreferences.getString("last_name", ""));
        etFirstName.setText(sharedPreferences.getString("first_name", ""));
        etMiddleName.setText(sharedPreferences.getString("middle_name", ""));

        // Сохранение данных при нажатии на кнопку
        btnSaveChanges.setOnClickListener(v -> {
            String lastName = etLastName.getText().toString().trim();
            String firstName = etFirstName.getText().toString().trim();
            String middleName = etMiddleName.getText().toString().trim();

            // Сохраняем ФИО в SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last_name", lastName);
            editor.putString("first_name", firstName);
            editor.putString("middle_name", middleName);
            editor.apply();

            Toast.makeText(getApplicationContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();



            // Закрываем активность и возвращаемся назад
            finish();
        });
    }
}
