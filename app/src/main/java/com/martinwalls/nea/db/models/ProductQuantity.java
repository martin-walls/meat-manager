package com.martinwalls.nea.db.models;

/**
 * Class to store quantity with product in Order / Contract
 */
public class ProductQuantity {
    private Product product;
    private double quantityMass; // in kg
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
