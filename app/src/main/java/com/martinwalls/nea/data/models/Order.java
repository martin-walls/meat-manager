package com.martinwalls.nea.data.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Order implements Serializable {
    private int orderId;
    private int destId;
    private String destName;
    private LocalDateTime orderDate;
    private boolean isCompleted;

    private List<ProductQuantity> productList = new ArrayList<>();

    public Order() {
    }

    public Order(int orderId, int destId, String destName, LocalDateTime orderDate, boolean isCompleted) {
        this.orderId = orderId;
        this.destId = destId;
        this.destName = destName;
        this.orderDate = orderDate;
        this.isCompleted = isCompleted;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getDestId() {
        return destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public void setDest(Location dest) {
        this.destId = dest.getLocationId();
        this.destName = dest.getLocationName();
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public List<ProductQuantity> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductQuantity> productList) {
        this.productList = productList;
    }

    public void addProduct(Product product, double quantityMass) {
        productList.add(new ProductQuantity(product, quantityMass));
    }

    public void addProduct(Product product, double quantityMass, int quantityBoxes) {
        productList.add(new ProductQuantity(product, quantityMass, quantityBoxes));
    }

    public static Comparator<Order> comparatorDate() {
        return (order1, order2) -> order1.getOrderDate().compareTo(order2.getOrderDate());
    }
}