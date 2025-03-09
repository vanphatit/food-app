package com.phatlee.food_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.phatlee.food_app.Database.AppDatabase;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.databinding.ActivityMyProfileBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

public class MyProfileActivity extends AppCompatActivity {
    private ActivityMyProfileBinding binding;
    private AppDatabase db;
    private int userId;
    private SharedPreferences sharedPreferences;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        saveAvatarToStorage(selectedImageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserProfile();

        binding.backHomeBtn.setOnClickListener(v -> finish());

        binding.button3.setOnClickListener(v -> updateUserProfile());

        binding.avatarImageView.setOnClickListener(v -> openImagePicker());

        binding.imgWishlist.setOnClickListener(v -> {
            Intent intent = new Intent(this, WishlistActivity.class);
            startActivity(intent);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveAvatarToStorage(Uri imageUri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                File file = new File(getFilesDir(), "avatar_user_" + userId + ".jpg");

                try (FileOutputStream out = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }

                String avatarPath = file.getAbsolutePath();
                db.userDao().updateUserAvatar(userId, avatarPath);

                runOnUiThread(() -> {
                    Glide.with(this).load(avatarPath).circleCrop().into(binding.avatarImageView);
                    Toast.makeText(this, "Avatar updated!", Toast.LENGTH_SHORT).show();
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to save avatar!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = db.userDao().getUserById(userId);
            runOnUiThread(() -> {
                if (user != null) {
                    binding.textView10.setText(user.name);
                    binding.textView13.setText(user.email);
                    binding.textView26.setText(user.phone);
                    binding.textView27.setText(user.address);

                    if (user.avatar != null && !user.avatar.isEmpty()) {
                        Glide.with(this).load(user.avatar).circleCrop().into(binding.avatarImageView);
                    }
                } else {
                    Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            String newName = binding.textView10.getText().toString();
            String newPhone = binding.textView26.getText().toString();
            String newAddress = binding.textView27.getText().toString();

            db.userDao().updateUserProfile(userId, newName, newPhone, newAddress);

            runOnUiThread(() -> Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show());
        });
    }
}