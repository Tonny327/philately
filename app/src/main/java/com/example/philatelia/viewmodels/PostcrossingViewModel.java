package com.example.philatelia.viewmodels;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.philatelia.models.PostcrossingUser;
import com.example.philatelia.models.PostcrossingStats;
import com.example.philatelia.models.PostcrossingPoll;
import com.example.philatelia.models.PostcrossingStamp;
import com.example.philatelia.repositories.PostcrossingRepository;
import java.util.List;

public class PostcrossingViewModel extends AndroidViewModel {
    private final PostcrossingRepository repository;
    private final MutableLiveData<PostcrossingUser> user = new MutableLiveData<>();
    private final MutableLiveData<PostcrossingStats> stats = new MutableLiveData<>();
    private final MutableLiveData<PostcrossingPoll> poll = new MutableLiveData<>();
    private final MutableLiveData<List<PostcrossingStamp>> stamps = new MutableLiveData<>();

    public PostcrossingViewModel(@NonNull Application application) {
        super(application);
        repository = new PostcrossingRepository();
    }

    public LiveData<PostcrossingUser> getUser() { return user; }
    public LiveData<PostcrossingStats> getStats() { return stats; }
    public LiveData<PostcrossingPoll> getPoll() { return poll; }
    public LiveData<List<PostcrossingStamp>> getStamps() { return stamps; }

    public void registerUser(Context context, String name, String email) {
        repository.registerUser(context, name, email);
        user.setValue(repository.getUser(context));
        stats.setValue(repository.getStats(context));
    }

    public void loadUserAndStats(Context context) {
        user.setValue(repository.getUser(context));
        stats.setValue(repository.getStats(context));
    }

    public void loadPoll(Context context) {
        poll.setValue(repository.getPoll(context));
    }

    public void votePoll(Context context, int optionIndex) {
        repository.votePoll(context, optionIndex);
        poll.setValue(repository.getPoll(context));
    }

    public void loadStamps(Context context) {
        List<PostcrossingStamp> stampList = repository.getStamps(context);
        stamps.setValue(stampList);
    }

    // TODO: Методы для регистрации, голосования, загрузки марок и аналитики
} 