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
import com.google.firebase.auth.FirebaseAuth;
import com.phatlee.food_app.Database.UserDaoFirestore;
import com.phatlee.food_app.Entity.User;
import com.phatlee.food_app.Repository.UserRepository;
import com.phatlee.food_app.databinding.ActivityMyProfileBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

public class MyProfileActivity extends AppCompatActivity {
    private ActivityMyProfileBinding binding;
    private String userId;
    private UserRepository userRepository;

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

        userRepository = new UserRepository();

        userId = getIntent().getStringExtra("userid");

        binding.backHomeBtn.setOnClickListener(v -> finish());

        binding.button3.setOnClickListener(v -> updateUserProfile());

        binding.avatarImageView.setOnClickListener(v -> openImagePicker());

        binding.imgWishlist.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, WishlistActivity.class);
            intent.putExtra("userid", userId);
            startActivity(intent);
        });

        binding.imgRecentList.setOnClickListener(v -> {
            startActivity(new Intent(MyProfileActivity.this, RecentlyViewedActivity.class));
        });

        loadUserProfile();
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
                String avatarPath = file.getCanonicalPath();
                userRepository.updateUserAvatar(userId, avatarPath, new UserDaoFirestore.OnOperationCompleteListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Glide.with(MyProfileActivity.this).load(avatarPath).circleCrop().into(binding.avatarImageView);
                            Toast.makeText(MyProfileActivity.this, "Avatar updated!", Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "Failed to update avatar!", Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "Failed to save avatar!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadUserProfile() {
        userRepository.getUserById(userId, new UserDaoFirestore.OnUserLoadedListener() {
            @Override
            public void onUserLoaded(User user) {
                runOnUiThread(() -> {
                    if (user != null) {
                        binding.textView10.setText(user.getName());
                        binding.textView13.setText(user.getEmail());
                        binding.textView26.setText(user.getPhone());
                        binding.textView27.setText(user.getAddress());
                        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                            Glide.with(MyProfileActivity.this).load(user.getAvatar()).circleCrop().into(binding.avatarImageView);
                        }
                    } else {
                        Toast.makeText(MyProfileActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "Error loading user profile", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            String newName = binding.textView10.getText().toString();
            String newPhone = binding.textView26.getText().toString();
            String newAddress = binding.textView27.getText().toString();

            userRepository.updateUserProfile(userId, newName, newPhone, newAddress, new UserDaoFirestore.OnOperationCompleteListener() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show());
                }
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}