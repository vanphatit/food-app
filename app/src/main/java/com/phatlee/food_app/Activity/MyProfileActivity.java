package com.phatlee.food_app.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.phatlee.food_app.R;
import com.phatlee.food_app.databinding.ActivityMyProfileBinding;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ImageView backHomeBtn = findViewById(R.id.backHomeBtn);
        backHomeBtn.setOnClickListener(v -> finish());
    }
}