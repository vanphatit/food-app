package com.phatlee.food_app.Repository;

import com.phatlee.food_app.Database.FoodsDaoFirestore;
import com.phatlee.food_app.Entity.Foods;

public class FoodsRepository {
    private FoodsDaoFirestore dao;

    public FoodsRepository() {
        dao = new FoodsDaoFirestore();
    }

    public void getAllFoods(FoodsDaoFirestore.OnFoodsLoadedListener listener) {
        dao.getAllFoods(listener);
    }

    public void getFoodById(int id, FoodsDaoFirestore.OnFoodLoadedListener listener) {
        dao.getFoodById(id, listener);
    }

    public Foods getFoodByIdSync(int foodId) throws Exception {
        return dao.getFoodByIdSync(foodId);
    }

    public void update(Foods food, FoodsDaoFirestore.OnOperationCompleteListener listener) {
        dao.update(food, listener);
    }

    public void updateRating(int foodId, float newRating, FoodsDaoFirestore.OnOperationCompleteListener listener) {
        dao.updateRating(foodId, newRating, listener);
    }

    public void updateStockQuantity(int foodId, int newStock, FoodsDaoFirestore.OnOperationCompleteListener listener) {
        dao.updateStockQuantity(foodId, newStock, listener);
    }

    public void updateStockQuantitySync(int foodId, int newStock) throws Exception {
        dao.updateStockQuantitySync(foodId, newStock);
    }

    public void updateRatingSync(int foodId, float newRating) throws Exception {
        dao.updateRatingSync(foodId, newRating);
    }
}