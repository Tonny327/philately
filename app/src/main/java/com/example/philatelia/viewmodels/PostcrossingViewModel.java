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
import com.example.philatelia.models.StampAnalytics;
import com.example.philatelia.repositories.PostcrossingRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.json.JSONException;

public class PostcrossingViewModel extends AndroidViewModel {
    private final PostcrossingRepository repository;
    private final MutableLiveData<PostcrossingUser> user = new MutableLiveData<>();
    private final MutableLiveData<PostcrossingStats> stats = new MutableLiveData<>();
    private final MutableLiveData<PostcrossingPoll> poll = new MutableLiveData<>();
    private final MutableLiveData<List<PostcrossingStamp>> stamps = new MutableLiveData<>();
    private final MutableLiveData<List<StampAnalytics>> analytics = new MutableLiveData<>();

    public PostcrossingViewModel(@NonNull Application application) {
        super(application);
        repository = new PostcrossingRepository();
    }

    public LiveData<PostcrossingUser> getUser() {
        return user;
    }

    public LiveData<PostcrossingStats> getStats() {
        return stats;
    }

    public LiveData<PostcrossingPoll> getPoll() {
        return poll;
    }

    public LiveData<List<PostcrossingStamp>> getStamps() {
        return stamps;
    }

    public LiveData<List<StampAnalytics>> getAnalytics() {
        return analytics;
    }

    public void loadInitialData(Context context) {
        user.setValue(repository.getUser(context));
        stats.setValue(repository.getStats(context));
        poll.setValue(repository.getPoll(context));
        loadStamps(context);
        analytics.setValue(repository.getStampAnalytics(context));
    }

    public void registerUser(Context context, String name, String email, String country, String city, String address) {
        repository.registerUser(context, name, email, country, city, address);
        user.setValue(repository.getUser(context));
        stats.setValue(repository.getStats(context));
    }

    public void voteInPoll(Context context, String option) {
        repository.savePollVote(context, option);
        poll.setValue(repository.getPoll(context));
    }

    public void loadStamps(Context context) {
        List<PostcrossingStamp> stampList = repository.getStamps(context);
        stamps.setValue(stampList);
    }

    public void clearData(Context context) {
        repository.clearUserData(context);
        user.setValue(null);
        stats.setValue(null);
        poll.setValue(null);
        // Не очищаем марки, так как они не зависят от пользователя
        // stamps.setValue(null);
    }

    // TODO: Методы для регистрации, голосования, загрузки марок и аналитики
} 