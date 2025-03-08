package com.phatlee.food_app.Utils;

import android.content.Context;
import android.util.Log;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.Category;
import com.phatlee.food_app.Entity.Foods;
import com.phatlee.food_app.Entity.Location;
import com.phatlee.food_app.Entity.Price;
import com.phatlee.food_app.Entity.Time;
import java.util.List;

public class DataSeeder {
    public static void seedDatabase(AppDatabase db, Context context) {
        try {
            // Kiểm tra nếu database đã có dữ liệu, nếu có thì bỏ qua
            if (!db.categoryDao().getAllCategories().isEmpty() ||
                    !db.foodsDao().getAllFoods().isEmpty() ||
                    !db.locationDao().getAllLocations().isEmpty() ||
                    !db.priceDao().getAllPrices().isEmpty() ||
                    !db.timeDao().getAllTimes().isEmpty()) {
                Log.d("DataSeeder", "✅ Database đã có dữ liệu, bỏ qua việc seed.");
                return;
            }

            Log.d("DataSeeder", "🚀 Đang đọc dữ liệu từ JSON...");
            DatabaseJsonModel data = JsonParser.parseJson(context, "food-app-phatlee-default-rtdb-export.json");

            if (data == null) {
                Log.e("DataSeeder", "❌ Lỗi: Không thể đọc dữ liệu từ JSON!");
                return;
            }

            //Log.d("DataSeeder", "✍️🌠 Đoạn JSON: " + data.toString());

            Log.d("DataSeeder", "✅ Đọc JSON thành công! Bắt đầu nhập dữ liệu vào Room...");

            // Nhập dữ liệu vào Room trong một transaction để tăng hiệu suất
            db.runInTransaction(() -> {
                if (data.Category != null && !data.Category.isEmpty()) {
                    for (Category category : data.Category) {
                        Log.d("DataSeeder", "📌 Category: ID = " + category.getId()
                                + ", Name = " + category.getName());
                    }
                    db.categoryDao().insert(data.Category.toArray(new Category[0]));
                }

                if (data.Foods != null && !data.Foods.isEmpty()) {
                    for (Foods food : data.Foods) {
                        Log.d("DataSeeder", "📌 Food: ID = " + food.getId()
                                + ", Name = " + food.getTitle()
                                + ", CategoryID = " + food.getCategoryId());
                    }
                    db.foodsDao().insert(data.Foods.toArray(new Foods[0]));
                }

                if (data.Location != null && !data.Location.isEmpty()) {
                    for(Location location : data.Location) {
                        Log.d("DataSeeder", "📌 Location: ID = " + location.getId()
                                + ", Name = " + location.getLoc());
                    }
                    db.locationDao().insert(data.Location.toArray(new Location[0]));
                }

                if (data.Price != null && !data.Price.isEmpty()) {
                    for (Price price : data.Price)
                        Log.d("DataSeeder", "📌 Price: ID = " + price.getId()
                                + ", Value = " + price.getValue());
                    db.priceDao().insert(data.Price.toArray(new Price[0]));
                }

                if (data.Time != null && !data.Time.isEmpty()) {
                    for (Time time : data.Time)
                        Log.d("DataSeeder", "📌 Time: ID = " + time.getId()
                                + ", Value = " + time.getValue());
                    db.timeDao().insert(data.Time.toArray(new Time[0]));
                }
            });

            Log.d("DataSeeder", "🎉 Dữ liệu đã nhập thành công vào Room Database!");
        } catch (Exception e) {
            Log.e("DataSeeder", "❌ Lỗi khi nhập dữ liệu từ JSON!", e);
        }
    }
}
