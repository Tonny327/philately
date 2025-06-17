package com.example.philatelia.data;

import android.content.Context;
import com.example.philatelia.models.Stamp;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StampRepository {
    public interface StampCallback {
        void onSuccess(List<Stamp> stamps);
        void onError(String error);
    }

    public void fetchStampsFromAssets(Context context, StampCallback callback) {
        new Thread(() -> {
            try {
                InputStream is = context.getAssets().open("stamps.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, StandardCharsets.UTF_8);
                JSONArray arr = new JSONArray(json);
                List<Stamp> stamps = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    stamps.add(new Stamp(
                        obj.getString("title"),
                        obj.getString("price"),
                        obj.getString("imageUrl")
                    ));
                }
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onSuccess(stamps));
            } catch (Exception e) {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }
} 