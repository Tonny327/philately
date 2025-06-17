package com.example.philatelia.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartItemEntity item);

    @Update
    void update(CartItemEntity item);

    @Delete
    void delete(CartItemEntity item);

    @Query("DELETE FROM cart_items")
    void clearCart();

    @Query("SELECT * FROM cart_items")
    LiveData<List<CartItemEntity>> getAllItems();
} 