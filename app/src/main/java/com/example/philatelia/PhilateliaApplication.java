package com.example.philatelia;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class PhilateliaApplication extends Application {
    private static final String TAG = "PhilateliaApp";

    @Override
    public void onCreate() {
        super.onCreate();
        // #region agent log
        try {
            FirebaseApp app = FirebaseApp.getInstance();
            String projectId = app.getOptions().getProjectId();
            String appId = app.getOptions().getApplicationId();
            Log.w(TAG, "Firebase config: projectId=" + projectId + " applicationId=" + appId);
        } catch (Exception e) {
            Log.e(TAG, "Firebase config read failed", e);
        }
        // #endregion
    }
} 