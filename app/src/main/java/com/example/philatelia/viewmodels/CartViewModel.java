package com.example.philatelia.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.helpers.PriceParseUtils;
import com.example.philatelia.repositories.CartRepository;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository repository;
    private final LiveData<List<CartItemEntity>> cartItems;
    private final MediatorLiveData<String> totalSum = new MediatorLiveData<>();

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new CartRepository(application);
        cartItems = repository.getAllItems();
        totalSum.setValue("0.00");
        totalSum.addSource(cartItems, items ->
                totalSum.setValue(PriceParseUtils.computeTotalFormatted(items)));
    }

    public LiveData<List<CartItemEntity>> getCartItems() {
        return cartItems;
    }

    public LiveData<String> getTotalSum() {
        return totalSum;
    }

    public void addToCart(CartItemEntity item) {
        repository.insert(item);
    }

    public void increaseQuantity(CartItemEntity item) {
        item.quantity++;
        repository.update(item);
    }

    public void decreaseQuantity(CartItemEntity item) {
        if (item.quantity > 1) {
            item.quantity--;
            repository.update(item);
        }
    }

    public void removeFromCart(CartItemEntity item) {
        repository.delete(item);
    }

    public void clearCart() {
        repository.clearCart();
    }

    public void migrateLegacyCartIfNeeded() {
        repository.migrateLegacyCartIfNeeded();
    }
} 