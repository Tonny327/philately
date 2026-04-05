package com.example.philatelia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.api.ApiException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView btnRegister;
    private LinearLayout btnGoogleSignIn;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;// Код запроса для Google Sign-In
    TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // Проверяем, авторизован ли пользователь
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        // Настройка Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // ID из Firebase
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        // Обработчик для "Забыли пароль?"
        tvForgotPassword.setOnClickListener(v -> resetPassword());
    }

    // Вход через Email и пароль
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите Email и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Exception e = task.getException();
                        Log.e(TAG, "Email/password sign-in failed", e);
                        String msg = (e == null) ? "Неизвестная ошибка" : (e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage()));
                        Toast.makeText(this, "Ошибка входа: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Вход через Google
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                int code = e.getStatusCode();
                String codeStr = GoogleSignInStatusCodes.getStatusCodeString(code);
                Log.e(TAG, "Google sign-in failed. code=" + code + " (" + codeStr + ")", e);
                Toast.makeText(this, "Ошибка входа через Google: " + code + " " + codeStr, Toast.LENGTH_LONG).show();
            }
        }
    }

    // Авторизация в Firebase через Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Exception e = task.getException();
                        Log.e(TAG, "Firebase signInWithCredential failed", e);
                        String msg = (e == null) ? "Неизвестная ошибка" : (e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage()));
                        Toast.makeText(LoginActivity.this, "Ошибка входа через Google: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Введите ваш Email для сброса пароля", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Письмо для сброса пароля отправлено на " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        Log.e(TAG, "Password reset failed", e);
                        String msg = (e == null) ? "Неизвестная ошибка" : (e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage()));
                        Toast.makeText(this, "Ошибка: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}

