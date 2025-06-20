package com.example.philatelia.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.philatelia.FeedbackActivity;
import com.example.philatelia.LoginActivity;
import com.example.philatelia.OrderHistoryActivity;
import com.example.philatelia.R;
import com.example.philatelia.UserProfileActivity;
import com.example.philatelia.viewmodels.PostcrossingViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserFragment extends Fragment {

    private FirebaseAuth auth;
    private TextView tvLogout;
    private Button btnEditProfile;
    private LinearLayout btnOrderHistory, btnFeedback, btnUsefulLinks, btnReviews;
    private ImageView ivAvatar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Инициализируем Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("profile_images");

        // Находим элементы интерфейса
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnOrderHistory = view.findViewById(R.id.btnOrderHistory);
        btnFeedback = view.findViewById(R.id.btnFeedback);
        btnUsefulLinks = view.findViewById(R.id.btnUsefulLinks);
        btnReviews = view.findViewById(R.id.btnReviews);
        tvLogout = view.findViewById(R.id.tvLogout); // Logout теперь TextView
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        ivAvatar = view.findViewById(R.id.ivAvatar);

        //эти две строчки отвечают на фон кнопки редактирования профиля
        btnEditProfile.setBackgroundResource(R.drawable.bg_edit_profile_button);
        btnEditProfile.setBackgroundTintList(null);
        // эти две строчки отвечают на фон кнопки редактирования профиля

        // Настраиваем launcher для выбора изображения
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    uploadImageToFirebase(imageUri);
                }
            }
        );

        // Обработчик нажатия на фото профиля
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        // Загружаем текущее фото профиля
        loadProfileImage();

        // Кнопка редактирования профиля
        btnEditProfile.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Кнопка история заказов
        btnOrderHistory.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), OrderHistoryActivity.class)));

        // Кнопка предложений
        btnFeedback.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FeedbackActivity.class)));

        // Кнопка "Полезные ссылки"
        btnUsefulLinks.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_user_to_linksFragment));

        // Кнопка "Отзывы и вопросы"
        btnReviews.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_nav_user_to_feedbackFragment));

        // Кнопка выхода
        tvLogout.setOnClickListener(v -> {
            auth.signOut(); // Выход из Firebase

            // Очистка данных Postcrossing
            PostcrossingViewModel postcrossingViewModel = new ViewModelProvider(requireActivity()).get(PostcrossingViewModel.class);
            postcrossingViewModel.clearData(requireContext());

            requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE)
                    .edit().putBoolean("isLoggedIn", false).apply();

            // Переход на LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        // Загружаем сохранённое ФИО
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        String lastName = sharedPreferences.getString("last_name", "");
        String firstName = sharedPreferences.getString("first_name", "");
        String middleName = sharedPreferences.getString("middle_name", "");

        // Устанавливаем в TextView (если данные есть)
        if (!firstName.isEmpty() || !lastName.isEmpty() || !middleName.isEmpty()) {
            tvUserName.setText(lastName + " " + firstName + " " + middleName);
        } else {
            tvUserName.setText("Ваше имя"); // Значение по умолчанию
        }

        return view;
    }

    private void loadProfileImage() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            StorageReference imageRef = storageReference.child(user.getUid() + ".jpg");
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.ic_avatar_placeholder)
                            .circleCrop()
                            .into(ivAvatar);
                }
            }).addOnFailureListener(e -> {
                // Если изображение не найдено, загружаем изображение по умолчанию
                if (getActivity() != null && !getActivity().isFinishing()) {
                    ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                }
            });
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null && auth.getCurrentUser() != null) {
            // Показываем сообщение о начале загрузки
            Toast.makeText(getContext(), "Загрузка изображения...", Toast.LENGTH_SHORT).show();

            StorageReference fileRef = storageReference.child(auth.getCurrentUser().getUid() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Загружаем обновленное изображение
                        loadProfileImage();
                        Toast.makeText(getContext(), "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}


