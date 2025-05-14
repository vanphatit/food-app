package com.phatlee.food_app.Repository;

import com.phatlee.food_app.Database.OrderDaoFirestore;
import com.phatlee.food_app.Entity.Order;
import com.phatlee.food_app.Entity.OrderItem;
import java.util.List;

public class OrderRepository {
    private OrderDaoFirestore dao;

    public OrderRepository() {
        dao = new OrderDaoFirestore();
    }

    public void insertOrder(Order order, OrderDaoFirestore.OnInsertOrderListener listener) {
        dao.insertOrder(order, listener);
    }

    public void insertOrderItems(List<OrderItem> orderItems, OrderDaoFirestore.OnOperationCompleteListener listener) {
        dao.insertOrderItems(orderItems, listener);
    }

    public String insertOrderSync(Order order) throws Exception {
        return dao.insertOrderSync(order);
    }

    public void insertOrderItemsSync(List<OrderItem> orderItems) throws Exception {
        dao.insertOrderItemsSync(orderItems);
    }

    public void getOrderById(String orderId, OrderDaoFirestore.OnOrderLoadedListener listener) {
        dao.getOrderById(orderId, listener);
    }

    public void getOrdersByUser(String userId, OrderDaoFirestore.OnOrdersLoadedListener listener) {
        dao.getOrdersByUser(userId, listener);
    }

    public void getOrderItems(String orderId, OrderDaoFirestore.OnOrderItemsLoadedListener listener) {
        dao.getOrderItems(orderId, listener);
    }

    public void updateOrderStatus(String orderId, String status, OrderDaoFirestore.OnOperationCompleteListener listener) {
        dao.updateOrderStatus(orderId, status, listener);
    }
}