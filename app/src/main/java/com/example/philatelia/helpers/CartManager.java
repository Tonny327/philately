package com.example.philatelia.helpers;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.models.StampSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREFS_NAME = "CartPrefs";
    private static final String STAMPS_KEY = "cart_stamps";
    private static final String SETS_KEY = "cart_sets";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addStampToCart(Stamp stamp) {
        List<Stamp> cart = getCartStamps();
        cart.add(stamp);
        saveStamps(cart);
    }

    public void addStampSetToCart(StampSet set) {
        List<StampSet> cart = getCartSets();
        cart.add(set);
        saveSets(cart);
    }

    public List<Stamp> getCartStamps() {
        String json = sharedPreferences.getString(STAMPS_KEY, "");
        Type type = new TypeToken<List<Stamp>>() {}.getType();
        return json.isEmpty() ? new ArrayList<>() : gson.fromJson(json, type);
    }

    public List<StampSet> getCartSets() {
        String json = sharedPreferences.getString(SETS_KEY, "");
        Type type = new TypeToken<List<StampSet>>() {}.getType();
        return json.isEmpty() ? new ArrayList<>() : gson.fromJson(json, type);
    }

    public void clearCart() {
        sharedPreferences.edit().remove(STAMPS_KEY).apply();
        sharedPreferences.edit().remove(SETS_KEY).apply();
    }

    private void saveStamps(List<Stamp> cart) {
        String json = gson.toJson(cart);
        sharedPreferences.edit().putString(STAMPS_KEY, json).apply();
    }

    private void saveSets(List<StampSet> cart) {
        String json = gson.toJson(cart);
        sharedPreferences.edit().putString(SETS_KEY, json).apply();
    }
}

