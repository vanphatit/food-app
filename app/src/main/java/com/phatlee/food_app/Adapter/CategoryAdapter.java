package com.phatlee.food_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phatlee.food_app.Activity.ListFoodsActivity;
import com.phatlee.food_app.Entity.Category;
import com.phatlee.food_app.R;
import com.phatlee.food_app.Repository.CategoryRepository;
import com.phatlee.food_app.databinding.ViewholderCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> items;
    private Context context;
    private CategoryRepository categoryRepository;

    public CategoryAdapter(List<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        categoryRepository = new CategoryRepository();
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = items.get(position);
        holder.titleTxt.setText(category.getName());

        // Giữ nguyên logic thiết lập background theo vị trí
        switch (position) {
            case 0: holder.pic.setBackgroundResource(R.drawable.cat_0_background); break;
            case 1: holder.pic.setBackgroundResource(R.drawable.cat_1_background); break;
            case 2: holder.pic.setBackgroundResource(R.drawable.cat_2_background); break;
            case 3: holder.pic.setBackgroundResource(R.drawable.cat_3_background); break;
            case 4: holder.pic.setBackgroundResource(R.drawable.cat_4_background); break;
            case 5: holder.pic.setBackgroundResource(R.drawable.cat_5_background); break;
            case 6: holder.pic.setBackgroundResource(R.drawable.cat_6_background); break;
            case 7: holder.pic.setBackgroundResource(R.drawable.cat_7_background); break;
        }
        int drawableResourceId = context.getResources().getIdentifier(category.getImagePath(),
                "drawable", context.getPackageName());
        Glide.with(context)
                .load(drawableResourceId)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListFoodsActivity.class);
            intent.putExtra("CategoryId", category.getId());
            intent.putExtra("CategoryName", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Phương thức loadCategories dùng CategoryRepository để cập nhật danh sách từ Firestore
    public void loadCategories(Context context) {
        categoryRepository.getAllCategories(new com.phatlee.food_app.Database.CategoryDaoFirestore.OnCategoriesLoadedListener() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                items.clear();
                items.addAll(categories);
                // Giả sử context là MainActivity, bạn có thể gọi runOnUiThread
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> notifyDataSetChanged());
                }
            }
            @Override
            public void onFailure(Exception e) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        // Hiển thị thông báo lỗi nếu cần
                    });
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;
        public ViewHolder(@NonNull ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            titleTxt = binding.catNameTxt;
            pic = binding.imgCat;
        }
    }
}