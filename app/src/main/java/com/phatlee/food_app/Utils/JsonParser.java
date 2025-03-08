package com.phatlee.food_app.Utils;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonParser {
    public static DatabaseJsonModel parseJson(Context context, String filename) {
        try {
            // Đọc file JSON từ thư mục assets
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            //Log.d("JsonParser", "✌️ File JSON: " + json);

            // Chuyển đổi JSON thành đối tượng DatabaseJsonModel
            return new Gson().fromJson(json, DatabaseJsonModel.class);
        } catch (Exception e) {
            Log.e("JsonParser", "❌ Lỗi khi đọc file JSON!", e);
            return null;
        }
    }
}
