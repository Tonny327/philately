package com.example.philatelia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.philatelia.R;
import com.example.philatelia.adapters.FeedbackAdapter;
import com.example.philatelia.helpers.FeedbackManager;
import com.example.philatelia.models.Feedback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import android.content.SharedPreferences;
import android.content.Context;

public class FeedbackFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;
    private FeedbackManager feedbackManager;
    private EditText feedbackInput;
    private ImageButton sendButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        setupToolbar(view);

        feedbackManager = new FeedbackManager(requireContext());
        feedbackList = feedbackManager.getFeedback();

        recyclerView = view.findViewById(R.id.feedback_recycler_view);
        feedbackInput = view.findViewById(R.id.feedback_input);
        sendButton = view.findViewById(R.id.send_feedback_button);

        setupRecyclerView();
        setupSendButton();

        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Отзывы и вопросы");
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupRecyclerView() {
        adapter = new FeedbackAdapter(requireContext(), feedbackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> {
            String text = feedbackInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(getContext(), "Отзыв не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String username;

            // 1. Try to get name from SharedPreferences
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String lastName = prefs.getString("last_name", "");
            String firstName = prefs.getString("first_name", "");
            String middleName = prefs.getString("middle_name", "");
            String fullName = (lastName + " " + firstName + " " + middleName).trim().replaceAll("\\s+", " ");

            if (!fullName.isEmpty()) {
                username = fullName;
            } else if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                // 2. Fallback to Firebase display name
                username = currentUser.getDisplayName();
            } else {
                // 3. Final fallback
                username = "Анонимный пользователь";
            }

            String avatarUrl = (currentUser != null && currentUser.getPhotoUrl() != null)
                    ? currentUser.getPhotoUrl().toString()
                    : null;

            Feedback newFeedback = new Feedback(username, text, System.currentTimeMillis(), avatarUrl);
            feedbackManager.addFeedback(newFeedback);

            // Update UI
            feedbackList = feedbackManager.getFeedback(); // Re-fetch the sorted list
            adapter.updateFeedback(feedbackList);
            recyclerView.scrollToPosition(0);
            feedbackInput.setText("");
            Toast.makeText(getContext(), "Спасибо за ваш отзыв!", Toast.LENGTH_SHORT).show();
        });
    }
} 