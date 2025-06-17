package com.example.philatelia.viewmodels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.philatelia.data.StampRepository;
import com.example.philatelia.models.Stamp;

import java.util.List;

public class StampViewModel extends ViewModel {
    private final StampRepository repository;
    private final MutableLiveData<List<Stamp>> stamps = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

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
        isLoading.setValue(true);
        error.setValue(null);

        repository.fetchStampsFromAssets(context, new StampRepository.StampCallback() {
            @Override
            public void onSuccess(List<Stamp> result) {
                stamps.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                isLoading.setValue(false);
            }
        });
    }
} 