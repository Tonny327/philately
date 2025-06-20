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

    public List<Stamp> getStampsFromAssets(Context context) throws Exception {
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
        return stamps;
    }
} 