package com.example.philatelia.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.philatelia.data.AppDatabase;
import com.example.philatelia.data.CartDao;
import com.example.philatelia.data.CartItemEntity;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CartRepository {
    private final CartDao cartDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public CartRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
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
} 