package com.phatlee.food_app.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insertOrder(Order order);

    @Insert
    void insertOrderItems(List<OrderItem> orderItems);

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    List<Order> getOrdersByUser(int userId);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getOrderItems(int orderId);

    // get order by id
    @Query("SELECT * FROM orders WHERE id = :orderId")
    Order getOrderById(int orderId);

    // update order status
    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    void updateOrderStatus(int orderId, String status);
}
