package com.example.philatelia.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.example.philatelia.R;
import com.example.philatelia.models.PostcrossingUser;
import com.example.philatelia.models.PostcrossingStats;
import com.example.philatelia.viewmodels.PostcrossingViewModel;
import com.example.philatelia.adapters.PostcrossingStampAdapter;
import com.example.philatelia.models.PostcrossingStamp;
import java.util.ArrayList;

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
    private EditText etName, etEmail;
    private Button btnRegister;
    private TextView tvStats, tvStatsData;
    private RadioGroup rgPollOptions;
    private Button btnVote;
    private TextView tvPollQuestion, tvPollResult;
    private RecyclerView rvStamps;
    private PostcrossingStampAdapter stampAdapter;

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
        return inflater.inflate(R.layout.fragment_postcrossing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        btnRegister = view.findViewById(R.id.btn_register);
        tvStats = view.findViewById(R.id.tv_stats);
        tvStatsData = view.findViewById(R.id.tv_stats_data);
        rgPollOptions = view.findViewById(R.id.rg_poll_options);
        btnVote = view.findViewById(R.id.btn_vote);
        tvPollQuestion = view.findViewById(R.id.tv_poll_question);
        tvPollResult = view.findViewById(R.id.tv_poll_result);
        rvStamps = view.findViewById(R.id.rv_stamps);

        viewModel = new ViewModelProvider(this).get(PostcrossingViewModel.class);
        viewModel.loadUserAndStats(requireContext());

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.isRegistered()) {
                etName.setVisibility(View.GONE);
                etEmail.setVisibility(View.GONE);
                btnRegister.setVisibility(View.GONE);
                tvStats.setVisibility(View.VISIBLE);
                tvStatsData.setVisibility(View.VISIBLE);
            } else {
                etName.setVisibility(View.VISIBLE);
                etEmail.setVisibility(View.VISIBLE);
                btnRegister.setVisibility(View.VISIBLE);
                tvStats.setVisibility(View.GONE);
                tvStatsData.setVisibility(View.GONE);
            }
        });

        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                String statsText = "Отправлено: " + stats.getSent() + ", Получено: " + stats.getReceived() + ", Участников: " + stats.getParticipants();
                tvStatsData.setText(statsText);
            }
        });

        viewModel.loadPoll(requireContext());
        viewModel.getPoll().observe(getViewLifecycleOwner(), poll -> {
            if (poll == null) return;
            tvPollQuestion.setText(poll.getQuestion());
            rgPollOptions.removeAllViews();
            int[] votes = poll.getVotes();
            int totalVotes = 0;
            for (int v : votes) totalVotes += v;
            for (int i = 0; i < poll.getOptions().size(); i++) {
                RadioButton rb = new RadioButton(requireContext());
                rb.setText(poll.getOptions().get(i));
                rb.setId(i);
                rb.setEnabled(poll.getUserVote() == -1);
                rgPollOptions.addView(rb);
                if (poll.getUserVote() == i) rb.setChecked(true);
            }
            // Показать результаты, если пользователь уже голосовал
            if (poll.getUserVote() != -1) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < poll.getOptions().size(); i++) {
                    int percent = totalVotes > 0 ? (int) Math.round(100.0 * votes[i] / totalVotes) : 0;
                    result.append(poll.getOptions().get(i)).append(": ").append(percent).append("%\n");
                }
                tvPollResult.setText(result.toString().trim());
                tvPollResult.setVisibility(View.VISIBLE);
                btnVote.setEnabled(false);
            } else {
                tvPollResult.setVisibility(View.GONE);
                btnVote.setEnabled(true);
            }
        });

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.registerUser(requireContext(), name, email);
            Toast.makeText(requireContext(), "Регистрация успешна!", Toast.LENGTH_SHORT).show();
        });

        btnVote.setOnClickListener(v -> {
            int checkedId = rgPollOptions.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(requireContext(), "Выберите вариант", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.votePoll(requireContext(), checkedId);
        });

        rvStamps.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        stampAdapter = new PostcrossingStampAdapter(new ArrayList<>());
        rvStamps.setAdapter(stampAdapter);

        viewModel.getStamps().observe(getViewLifecycleOwner(), stamps -> {
            stampAdapter.setStamps(stamps);
        });
        viewModel.loadStamps(requireContext());

        stampAdapter.setOnStampClickListener(stamp -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", stamp.getTitle());
            bundle.putString("price", stamp.getPrice());
            bundle.putString("imageUrl", stamp.getImageUrl());
            Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_postcrossing_to_stampDetailFragment, bundle);
        });
    }
}