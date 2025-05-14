package com.phatlee.food_app.Repository;

import com.phatlee.food_app.Database.CategoryDaoFirestore;
import com.phatlee.food_app.Entity.Category;
import java.util.List;

public class CategoryRepository {
    private CategoryDaoFirestore dao;

    public CategoryRepository() {
        dao = new CategoryDaoFirestore();
    }

    public void insertCategory(Category category, CategoryDaoFirestore.OnOperationCompleteListener listener) {
        dao.insert(category, listener);
    }

    public void getAllCategories(CategoryDaoFirestore.OnCategoriesLoadedListener listener) {
        dao.getAllCategories(listener);
    }
}