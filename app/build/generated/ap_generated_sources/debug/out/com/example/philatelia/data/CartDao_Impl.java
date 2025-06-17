package com.example.philatelia.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CartDao_Impl implements CartDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CartItemEntity> __insertionAdapterOfCartItemEntity;

  private final EntityDeletionOrUpdateAdapter<CartItemEntity> __deletionAdapterOfCartItemEntity;

  private final EntityDeletionOrUpdateAdapter<CartItemEntity> __updateAdapterOfCartItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearCart;

  public CartDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCartItemEntity = new EntityInsertionAdapter<CartItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cart_items` (`id`,`stampId`,`title`,`imageUrl`,`price`,`priceNum`,`quantity`,`priceKopecks`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CartItemEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.stampId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.stampId);
        }
        if (entity.title == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.title);
        }
        if (entity.imageUrl == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.imageUrl);
        }
        if (entity.price == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.price);
        }
        statement.bindDouble(6, entity.priceNum);
        statement.bindLong(7, entity.quantity);
        statement.bindLong(8, entity.priceKopecks);
      }
    };
    this.__deletionAdapterOfCartItemEntity = new EntityDeletionOrUpdateAdapter<CartItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `cart_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CartItemEntity entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfCartItemEntity = new EntityDeletionOrUpdateAdapter<CartItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cart_items` SET `id` = ?,`stampId` = ?,`title` = ?,`imageUrl` = ?,`price` = ?,`priceNum` = ?,`quantity` = ?,`priceKopecks` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CartItemEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.stampId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.stampId);
        }
        if (entity.title == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.title);
        }
        if (entity.imageUrl == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.imageUrl);
        }
        if (entity.price == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.price);
        }
        statement.bindDouble(6, entity.priceNum);
        statement.bindLong(7, entity.quantity);
        statement.bindLong(8, entity.priceKopecks);
        statement.bindLong(9, entity.id);
      }
    };
    this.__preparedStmtOfClearCart = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cart_items";
        return _query;
      }
    };
  }

  @Override
  public void insert(final CartItemEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCartItemEntity.insert(item);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final CartItemEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfCartItemEntity.handle(item);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final CartItemEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfCartItemEntity.handle(item);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearCart() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearCart.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearCart.release(_stmt);
    }
  }

  @Override
  public LiveData<List<CartItemEntity>> getAllItems() {
    final String _sql = "SELECT * FROM cart_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"cart_items"}, false, new Callable<List<CartItemEntity>>() {
      @Override
      @Nullable
      public List<CartItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStampId = CursorUtil.getColumnIndexOrThrow(_cursor, "stampId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfPriceNum = CursorUtil.getColumnIndexOrThrow(_cursor, "priceNum");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfPriceKopecks = CursorUtil.getColumnIndexOrThrow(_cursor, "priceKopecks");
          final List<CartItemEntity> _result = new ArrayList<CartItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CartItemEntity _item;
            _item = new CartItemEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfStampId)) {
              _item.stampId = null;
            } else {
              _item.stampId = _cursor.getString(_cursorIndexOfStampId);
            }
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _item.title = null;
            } else {
              _item.title = _cursor.getString(_cursorIndexOfTitle);
            }
            if (_cursor.isNull(_cursorIndexOfImageUrl)) {
              _item.imageUrl = null;
            } else {
              _item.imageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            }
            if (_cursor.isNull(_cursorIndexOfPrice)) {
              _item.price = null;
            } else {
              _item.price = _cursor.getString(_cursorIndexOfPrice);
            }
            _item.priceNum = _cursor.getDouble(_cursorIndexOfPriceNum);
            _item.quantity = _cursor.getInt(_cursorIndexOfQuantity);
            _item.priceKopecks = _cursor.getInt(_cursorIndexOfPriceKopecks);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
