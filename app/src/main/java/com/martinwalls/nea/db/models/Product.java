package com.martinwalls.nea.db.models;

public class Product implements Comparable<Product> {
    private int productId;
    private String productName;
    private String meatType;

    public Product() {
    }

    // for testing
    public Product(String productName) {
        this.productName = productName;
    }

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

    @Override
    public int compareTo(Product otherProduct) {
        return this.getProductName().compareTo(otherProduct.getProductName());
    }
}
