package com.example.philatelia;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {
    private static final int REQUEST_EDIT_PROFILE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private TextView etUserName, etUserPhone, etUserEmail, etUserPassword;
    private TextView btnEditName, btnEditPhone, btnEditEmail, btnEditPassword;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        try {
            // Инициализация Firebase
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

            // Находим все view элементы
            ImageView btnBack = findViewById(R.id.btnBack);
            etUserName = findViewById(R.id.etUserName);
            etUserPhone = findViewById(R.id.etUserPhone);
            etUserEmail = findViewById(R.id.etUserEmail);
            etUserPassword = findViewById(R.id.etUserPassword);
            btnEditName = findViewById(R.id.btnEditName);
            btnEditPhone = findViewById(R.id.btnEditPhone);
            btnEditEmail = findViewById(R.id.btnEditEmail);
            btnEditPassword = findViewById(R.id.btnEditPassword);

            if (etUserName == null || etUserPhone == null || etUserEmail == null || 
                etUserPassword == null || btnEditName == null || btnEditPhone == null || 
                btnEditEmail == null || btnEditPassword == null) {
                Toast.makeText(this, "Ошибка инициализации интерфейса", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Загружаем данные пользователя
            loadUserData();

            // Обработчики нажатий
            btnEditName.setOnClickListener(v -> showEditNameDialog());
            btnEditPhone.setOnClickListener(v -> showEditPhoneDialog());
            btnEditEmail.setOnClickListener(v -> showEditEmailDialog());
            btnEditPassword.setOnClickListener(v -> showChangePasswordDialog());
            btnBack.setOnClickListener(v -> onBackPressed());
            
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при инициализации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadUserData() {
        // Загрузка ФИО
        String lastName = sharedPreferences.getString("last_name", "");
        String firstName = sharedPreferences.getString("first_name", "");
        String middleName = sharedPreferences.getString("middle_name", "");
        String fullName = String.format("%s %s %s", lastName, firstName, middleName).trim();
        etUserName.setText(fullName.isEmpty() ? "Не указано" : fullName);

        // Загрузка телефона
        String phone = sharedPreferences.getString("phone", "");
        etUserPhone.setText(phone.isEmpty() ? "Не указан" : phone);

        // Загрузка email
        if (user != null && user.getEmail() != null) {
            etUserEmail.setText(user.getEmail());
        } else {
            etUserEmail.setText("Не указан");
        }
    }

    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions[0].equals(Manifest.permission.CAMERA)) {
                    openCamera();
                } else {
                    openGallery();
                }
            } else {
                Toast.makeText(this, "Необходимо разрешение для продолжения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_IMAGE_PICK:
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        uploadImageToFirebase(selectedImageUri);
                    }
                    break;

                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            String path = MediaStore.Images.Media.insertImage(
                                getContentResolver(), 
                                imageBitmap,
                                "profile_photo",
                                null
                            );
                            if (path != null) {
                                uploadImageToFirebase(Uri.parse(path));
                            }
                        }
                    }
                    break;

                case REQUEST_EDIT_PROFILE:
                    String lastName = data.getStringExtra("last_name");
                    String firstName = data.getStringExtra("first_name");
                    String middleName = data.getStringExtra("middle_name");
                    
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("last_name", lastName);
                    editor.putString("first_name", firstName);
                    editor.putString("middle_name", middleName);
                    editor.apply();
                    
                    String fullName = String.format("%s %s %s", 
                        lastName != null ? lastName : "",
                        firstName != null ? firstName : "",
                        middleName != null ? middleName : "").trim();
                    etUserName.setText(fullName);
                    break;
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Необходимо авторизоваться", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog progressDialog = new AlertDialog.Builder(this)
            .setTitle("Загрузка фото")
            .setMessage("Пожалуйста, подождите...")
            .setCancelable(false)
            .create();
        progressDialog.show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(userId + ".jpg");

        try {
            // Сжимаем изображение перед загрузкой
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] data = baos.toByteArray();

            // Загружаем файл
            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Получаем URL для Glide
                storageRef.child("avatars/" + user.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                    if (!isFinishing()) {
                    }
                });
                Toast.makeText(UserProfileActivity.this, "Аватар успешно обновлен", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Ошибка при загрузке: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Ошибка при обработке изображения", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изменить номер телефона");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_field, null);
        EditText input = view.findViewById(R.id.editText);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        String currentPhone = etUserPhone.getText().toString();
        input.setText(currentPhone.equals("Не указан") ? "" : currentPhone);
        builder.setView(view);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                String newPhone = input.getText().toString().trim();
                if (isValidPhone(newPhone)) {
                    sharedPreferences.edit().putString("phone", newPhone).apply();
                    etUserPhone.setText(newPhone);
                    Toast.makeText(this, "Номер телефона обновлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка при сохранении номера телефона: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изменить email");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_field, null);
        EditText input = view.findViewById(R.id.editText);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setText(etUserEmail.getText().toString());
        builder.setView(view);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newEmail = input.getText().toString().trim();
            if (isValidEmail(newEmail)) {
                updateEmail(newEmail);
            } else {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изменить пароль");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        EditText currentPassword = view.findViewById(R.id.currentPassword);
        EditText newPassword = view.findViewById(R.id.newPassword);
        EditText confirmPassword = view.findViewById(R.id.confirmPassword);
        builder.setView(view);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String currentPwd = currentPassword.getText().toString();
            String newPwd = newPassword.getText().toString();
            String confirmPwd = confirmPassword.getText().toString();

            if (newPwd.equals(confirmPwd)) {
                if (isValidPassword(newPwd)) {
                    updatePassword(currentPwd, newPwd);
                } else {
                    Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateEmail(String newEmail) {
        if (user != null) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            etUserEmail.setText(newEmail);
                            Toast.makeText(this, "Email успешно обновлен", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Ошибка при обновлении email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePassword(String currentPassword, String newPassword) {
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(UserProfileActivity.this, "Пароль успешно изменен", Toast.LENGTH_SHORT).show();
                            etUserPassword.setText("••••••••");
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Ошибка при смене пароля: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(UserProfileActivity.this, "Неверный текущий пароль", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\+?[0-9]{10,13}$");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_name, null);
        builder.setView(dialogView);

        final EditText etLastName = dialogView.findViewById(R.id.etLastName);
        final EditText etFirstName = dialogView.findViewById(R.id.etFirstName);
        final EditText etMiddleName = dialogView.findViewById(R.id.etMiddleName);

        // Заполняем поля текущими значениями
        etLastName.setText(sharedPreferences.getString("last_name", ""));
        etFirstName.setText(sharedPreferences.getString("first_name", ""));
        etMiddleName.setText(sharedPreferences.getString("middle_name", ""));

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newLastName = etLastName.getText().toString().trim();
            String newFirstName = etFirstName.getText().toString().trim();
            String newMiddleName = etMiddleName.getText().toString().trim();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("last_name", newLastName);
            editor.putString("first_name", newFirstName);
            editor.putString("middle_name", newMiddleName);
            editor.apply();

            loadUserData(); // Обновляем UI
            Toast.makeText(UserProfileActivity.this, "ФИО успешно обновлено", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
