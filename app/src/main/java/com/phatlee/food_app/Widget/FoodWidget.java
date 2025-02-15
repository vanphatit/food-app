package com.phatlee.food_app.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.phatlee.food_app.Activity.MainActivity;
import com.phatlee.food_app.R;

public class FoodWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Cập nhật thông tin hiển thị
            views.setTextViewText(R.id.widget_food_name, "Dragon Roll");
            views.setTextViewText(R.id.widget_food_price, "99.000 VND");
            views.setImageViewResource(R.id.widget_food_image, R.drawable.food_dragon_roll);

            // Intent mở ứng dụng khi nhấn nút đặt hàng
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_order_button, pendingIntent);

            // Cập nhật widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
