package com.example.philatelia.viewmodels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.philatelia.data.StampRepository;
import com.example.philatelia.models.Stamp;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StampViewModel extends ViewModel {
    private final StampRepository repository;
    private final MutableLiveData<List<Stamp>> stamps = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public StampViewModel() {
        repository = new StampRepository();
    }

    public LiveData<List<Stamp>> getStamps() {
        return stamps;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadStamps(Context context) {
        isLoading.postValue(true);
        error.postValue(null);
        executorService.execute(() -> {
            try {
                List<Stamp> result = repository.getStampsFromAssets(context.getApplicationContext());
                stamps.postValue(result);
            } catch (Exception e) {
                error.postValue(e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
} 