package com.martinwalls.meatmanager.data.models;

import java.io.Serializable;
import java.util.Comparator;

public class Product implements Serializable {
    private int productId;
    private String productName;
    private String meatType;

    public Product() {}

    public Product(int productId, String productName, String meatType) {
        this.productId = productId;
        this.productName = productName;
        this.meatType = meatType;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMeatType() {
        return meatType;
    }

    public void setMeatType(String meatType) {
        this.meatType = meatType;
    }

    /**
     * {@link Comparator} to sort products alphabetically.
     */
    public static Comparator<Product> comparatorAlpha() {
        return (product1, product2) ->
                product1.getProductName().compareTo(product2.getProductName());
    }

    /**
     * {@link Comparator} to sort products alphabetically by meat type.
     */
    public static Comparator<Product> comparatorMeatType() {
        return (product1, product2) -> {
            if (product1.getMeatType().equals(product2.getMeatType())) {
                return product1.getProductName().compareTo(product2.getProductName());
            } else {
                return product1.getMeatType().compareTo(product2.getMeatType());
            }
        };
    }
}
