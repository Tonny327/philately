package com.example.philatelia.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import com.example.philatelia.data.AppDatabase;
import com.example.philatelia.data.CartDao;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.helpers.CartManager;
import com.example.philatelia.helpers.PriceParseUtils;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.models.StampSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CartRepository {
    private static final String MIGRATION_PREFS = "cart_room_migration";
    private static final String KEY_LEGACY_MIGRATED = "legacy_prefs_migrated";

    private final CartDao cartDao;
    private final Context appContext;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public CartRepository(Context context) {
        appContext = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(appContext);
        cartDao = db.cartDao();
    }

    public LiveData<List<CartItemEntity>> getAllItems() {
        return cartDao.getAllItems();
    }

    public void insert(CartItemEntity item) {
        executor.execute(() -> cartDao.insert(item));
    }

    public void update(CartItemEntity item) {
        executor.execute(() -> cartDao.update(item));
    }

    public void delete(CartItemEntity item) {
        executor.execute(() -> cartDao.delete(item));
    }

    public void clearCart() {
        executor.execute(cartDao::clearCart);
    }

    /**
     * One-time import from legacy {@link CartManager} (SharedPreferences) into Room.
     */
    public void migrateLegacyCartIfNeeded() {
        executor.execute(() -> {
            SharedPreferences prefs = appContext.getSharedPreferences(MIGRATION_PREFS, Context.MODE_PRIVATE);
            if (prefs.getBoolean(KEY_LEGACY_MIGRATED, false)) {
                return;
            }
            CartManager legacy = new CartManager(appContext);
            List<Stamp> stamps = legacy.getCartStamps();
            List<StampSet> sets = legacy.getCartSets();
            if (stamps == null) {
                stamps = new ArrayList<>();
            }
            if (sets == null) {
                sets = new ArrayList<>();
            }
            if (stamps.isEmpty() && sets.isEmpty()) {
                prefs.edit().putBoolean(KEY_LEGACY_MIGRATED, true).apply();
                return;
            }
            for (Stamp stamp : stamps) {
                CartItemEntity item = new CartItemEntity();
                item.title = stamp.getTitle() != null ? stamp.getTitle() : "";
                item.imageUrl = stamp.getImageUrl() != null ? stamp.getImageUrl() : "";
                PriceParseUtils.applyPriceFields(item, stamp.getPrice());
                item.quantity = 1;
                item.stampId = PriceParseUtils.stableStampId(
                        stamp.getTitle(), stamp.getPrice(), stamp.getImageUrl());
                cartDao.insert(item);
            }
            for (StampSet set : sets) {
                CartItemEntity item = new CartItemEntity();
                String year = set.getYear() != null ? set.getYear() : "";
                item.title = (set.getName() != null ? set.getName() : "") + " (набор " + year + ")";
                item.imageUrl = set.getImage() != null ? set.getImage() : "";
                PriceParseUtils.applyPriceFields(item, set.getPrice());
                item.quantity = 1;
                item.stampId = PriceParseUtils.stableSetId(
                        set.getName(), set.getPrice(), year);
                cartDao.insert(item);
            }
            legacy.clearCart();
            prefs.edit().putBoolean(KEY_LEGACY_MIGRATED, true).apply();
        });
    }
} 