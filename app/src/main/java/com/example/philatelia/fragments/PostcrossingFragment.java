package com.example.philatelia.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.philatelia.R;
import com.example.philatelia.adapters.PostcrossingStampAdapter;
import com.example.philatelia.adapters.StampAnalyticsAdapter;
import com.example.philatelia.models.PostcrossingPoll;
import com.example.philatelia.models.PostcrossingStats;
import com.example.philatelia.models.PostcrossingUser;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.viewmodels.PostcrossingViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostcrossingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostcrossingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PostcrossingViewModel viewModel;
    private RecyclerView rvStamps;
    private RecyclerView rvAnalytics;
    private PostcrossingStampAdapter stampAdapter;
    private StampAnalyticsAdapter analyticsAdapter;
    private LinearLayout layoutRegistrationForm;
    private LinearLayout layoutStats;
    private EditText etName, etEmail, etPassword, etAddress;
    private Spinner spinnerCountry, spinnerCity;
    private Button btnRegister;
    private TextView tvStats, tvStatsData;
    private TextView tvPollQuestion, tvPollResult;
    private RadioGroup rgPollOptions;
    private Button btnVote;

    private Map<String, List<String>> citiesByCountry = new HashMap<>();

    public PostcrossingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostcrossingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostcrossingFragment newInstance(String param1, String param2) {
        PostcrossingFragment fragment = new PostcrossingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postcrossing, container, false);

        initViews(view);
        setupCountryCitySpinners();

        viewModel = new ViewModelProvider(this).get(PostcrossingViewModel.class);

        setupRecyclerViews();
        setupObservers();
        setupListeners();

        viewModel.loadInitialData(requireContext());

        return view;
    }

    private void initViews(View view) {
        rvStamps = view.findViewById(R.id.rv_stamps);
        rvAnalytics = view.findViewById(R.id.rv_analytics);
        layoutRegistrationForm = view.findViewById(R.id.layout_registration_form);
        layoutStats = view.findViewById(R.id.layout_stats);
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        spinnerCountry = view.findViewById(R.id.spinner_country);
        spinnerCity = view.findViewById(R.id.spinner_city);
        etAddress = view.findViewById(R.id.et_address);
        btnRegister = view.findViewById(R.id.btn_register);
        tvStats = view.findViewById(R.id.tv_stats);
        tvStatsData = view.findViewById(R.id.tv_stats_data);
        tvPollQuestion = view.findViewById(R.id.tv_poll_question);
        rgPollOptions = view.findViewById(R.id.rg_poll_options);
        btnVote = view.findViewById(R.id.btn_vote);
        tvPollResult = view.findViewById(R.id.tv_poll_result);
    }

    private void setupCountryCitySpinners() {
        // Заглушки данных
        citiesByCountry.put("Беларусь", Arrays.asList("Минск", "Гомель", "Брест", "Витебск", "Гродно", "Могилёв"));
        citiesByCountry.put("Россия", Arrays.asList("Москва", "Санкт-Петербург", "Новосибирск"));
        citiesByCountry.put("Украина", Arrays.asList("Киев", "Харьков", "Одесса"));

        List<String> countries = new ArrayList<>(citiesByCountry.keySet());
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = (String) parent.getItemAtPosition(position);
                List<String> cities = citiesByCountry.get(selectedCountry);
                if (cities != null) {
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cities);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(cityAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupRecyclerViews() {
        rvStamps.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        stampAdapter = new PostcrossingStampAdapter(new ArrayList<>());
        rvStamps.setAdapter(stampAdapter);

        rvAnalytics.setLayoutManager(new LinearLayoutManager(getContext()));
        analyticsAdapter = new StampAnalyticsAdapter(new ArrayList<>());
        rvAnalytics.setAdapter(analyticsAdapter);
    }

    private void setupObservers() {
        viewModel.getUser().observe(getViewLifecycleOwner(), this::updateUiBasedOnRegistration);
        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            String statsText = "Отправлено: " + stats.getSent() + "\nПолучено: " + stats.getReceived() + "\nУчастников: " + stats.getParticipants();
            tvStatsData.setText(statsText);
        });
        viewModel.getPoll().observe(getViewLifecycleOwner(), this::displayPoll);
        viewModel.getStamps().observe(getViewLifecycleOwner(), stamps -> stampAdapter.setStamps(stamps));
        viewModel.getAnalytics().observe(getViewLifecycleOwner(), analytics -> {
            if (analytics != null) {
                analyticsAdapter.updateData(analytics);
            }
        });
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String country = spinnerCountry.getSelectedItem().toString();
            String city = spinnerCity.getSelectedItem().toString();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                Toast.makeText(getContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(getContext(), "Пароль должен содержать не менее 6 символов", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.registerUser(requireContext(), name, email, country, city, address);
        });

        btnVote.setOnClickListener(v -> {
            int selectedId = rgPollOptions.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = rgPollOptions.findViewById(selectedId);
                String selectedOption = selectedRadioButton.getText().toString();
                viewModel.voteInPoll(requireContext(), selectedOption);
            } else {
                Toast.makeText(getContext(), "Пожалуйста, выберите вариант", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUiBasedOnRegistration(PostcrossingUser user) {
        if (user.isRegistered()) {
            layoutRegistrationForm.setVisibility(View.GONE);
            layoutStats.setVisibility(View.VISIBLE);
        } else {
            layoutRegistrationForm.setVisibility(View.VISIBLE);
            layoutStats.setVisibility(View.GONE);
        }
    }

    private void displayPoll(PostcrossingPoll poll) {
        if (poll != null) {
            tvPollQuestion.setText(poll.getQuestion());
            rgPollOptions.removeAllViews();
            for (String option : poll.getOptions()) {
                RadioButton rb = new RadioButton(getContext());
                rb.setText(option);
                int totalVotes = poll.getTotalVotes();
                int optionVotes = poll.getVotesForOption(option);
                String resultText = option + " (" + (totalVotes > 0 ? (100 * optionVotes / totalVotes) : 0) + "%)";
                rb.setText(poll.isVoted() ? resultText : option);
                rb.setEnabled(!poll.isVoted());
                rgPollOptions.addView(rb);
            }
            btnVote.setEnabled(!poll.isVoted());
            tvPollResult.setVisibility(poll.isVoted() ? View.VISIBLE : View.GONE);
        }
    }
}