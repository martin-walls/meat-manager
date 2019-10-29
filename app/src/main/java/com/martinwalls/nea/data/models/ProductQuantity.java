package com.martinwalls.nea.data.models;

/**
 * Class to store quantity with product in Order / Contract
 */
public class ProductQuantity {
    private Product product;
    private double quantityMass; // in kg
    private int quantityBoxes;

    public ProductQuantity() {
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
