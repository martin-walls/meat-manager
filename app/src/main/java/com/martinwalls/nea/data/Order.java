package com.martinwalls.nea.data;

import java.util.ArrayList;
import java.util.List;

public class Order {
    /**
     * Class to store quantity of product held as well as Product object
     */
    private class ProductQuantity {
        private Product product;
        private double quantityMass;
        private int quantityBoxes;

        public ProductQuantity(Product product, double quantityMass) {
            this.product = product;
            this.quantityMass = quantityMass;
            this.quantityBoxes = -1;
        }

        public ProductQuantity(Product product, double quantityMass, int quantityBoxes) {
            this.product = product;
            this.quantityMass = quantityMass;
            this.quantityBoxes = quantityBoxes;
        }

        public Product getProduct() {
            return product;
        }

        public double getQuantityMass() {
            return quantityMass;
        }

        public int getQuantityBoxes() {
            return quantityBoxes;
        }
    }

    private int orderId;
    private int destId;
    private String destName;
    private String orderDate; //TODO make this a custom date object
    private boolean isCompleted;

    private List<ProductQuantity> productList = new ArrayList<>();

    public Order() {
    }

    public Order(int orderId, int destId, String destName, String orderDate, boolean isCompleted) {
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
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
}