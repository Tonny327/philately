package com.example.philatelia.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.philatelia.data.CartItemEntity;
import com.example.philatelia.repositories.CartRepository;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository repository;
    private final LiveData<List<CartItemEntity>> cartItems;
    private final MutableLiveData<String> totalSum = new MutableLiveData<>("0.00");

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new CartRepository(application);
        cartItems = repository.getAllItems();
        cartItems.observeForever(items -> {
            int sumKopecks = 0;
            for (CartItemEntity item : items) {
                int kopecks = item.priceKopecks;
                if (kopecks == 0) {
                    // Пробуем пересчитать из priceNum или price
                    if (item.priceNum > 0.0) {
                        kopecks = (int) Math.round(item.priceNum * 100);
                    } else if (item.price != null && !item.price.isEmpty()) {
                        try {
                            double priceParsed = Double.parseDouble(item.price.replaceAll("[^0-9.,]", "").replace(",", "."));
                            kopecks = (int) Math.round(priceParsed * 100);
                        } catch (Exception ignored) {}
                    }
                }
                sumKopecks += kopecks * item.quantity;
            }
            double sumRub = sumKopecks / 100.0;
            totalSum.setValue(String.format("%.2f", sumRub));
        });
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
} 