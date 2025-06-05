package com.example.philatelia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity {
    private EditText etFeedback;
    private Button btnSendFeedback;
    private DatabaseReference feedbackRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        etFeedback = findViewById(R.id.etFeedback);
        btnSendFeedback = findViewById(R.id.btnSendFeedback);
        feedbackRef = FirebaseDatabase.getInstance().getReference("feedback");

        btnSendFeedback.setOnClickListener(v -> {
            String feedback = etFeedback.getText().toString().trim();
            if (!feedback.isEmpty()) {
                String feedbackId = feedbackRef.push().getKey();
                feedbackRef.child(feedbackId).setValue(feedback);
                Toast.makeText(this, "Спасибо за предложение!", Toast.LENGTH_SHORT).show();
                etFeedback.setText("");
            } else {
                Toast.makeText(this, "Введите предложение", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
