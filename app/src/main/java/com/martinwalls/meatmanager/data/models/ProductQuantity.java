package com.martinwalls.meatmanager.data.models;

import java.io.Serializable;

/**
 * Class to store quantity with product in Order / Contract
 */
public class ProductQuantity implements Serializable {
    private Product product;
    private double quantityMass; // in kg
    private int quantityBoxes;

    public ProductQuantity() {
        product = new Product();
        quantityMass = -1;
        quantityBoxes = -1;
    }

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

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setQuantityMass(double quantityMass) {
        this.quantityMass = quantityMass;
    }

    public double getQuantityMass() {
        return quantityMass;
    }

    public void setQuantityBoxes(int quantityBoxes) {
        this.quantityBoxes = quantityBoxes;
    }

    public int getQuantityBoxes() {
        return quantityBoxes;
    }
}
