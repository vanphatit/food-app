package com.phatlee.food_app.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.phatlee.food_app.Entity.Category;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Location;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;
import com.phatlee.food_app.Entity.Price;
import com.phatlee.food_app.Entity.Time;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.Entity.Wishlist;
import com.phatlee.food_app.Entity.Cart;

@Database(entities = {
        Foods.class,
        Category.class,
        Location.class,
        Price.class,
        Time.class,
        User.class,
        Wishlist.class,
        Cart.class,
        Order.class,
        OrderItem.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract FoodsDao foodsDao();
    public abstract CategoryDao categoryDao();
    public abstract LocationDao locationDao();
    public abstract PriceDao priceDao();
    public abstract TimeDao timeDao();
    public abstract UserDao userDao();
    public abstract WishlistDao wishlistDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "food_app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}