package com.martinwalls.nea.data;

public class StockItem {
    public enum Quality {
        GOOD,
        PET_FOOD,
        WASTE;

        public static Quality parseQuality(String name) {
            for (Quality q : values()) {
                if (q.name().equalsIgnoreCase(name)) {
                    return q;
                }
            }
            return null;
        }
    }

    private Product product;
    private int locationId;
    private int supplierId;
    private int destId;
    private double mass; // stored in kg
    private int numBoxes;
    private Quality quality;

    public StockItem() {
    }

    // for testing
    public StockItem(Product product, double mass, int numBoxes) {
        this.product = product;
        this.mass = mass;
        this.numBoxes = numBoxes;
    }

    // db "not null" fields
    public StockItem(Product product, int locationId, int supplierId, double mass, Quality quality) {
        this.product = product;
        this.locationId = locationId;
        this.supplierId = supplierId;
        this.mass = mass;
        this.quality = quality;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getDestId() {
        return destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public int getNumBoxes() {
        return numBoxes;
    }

    public void setNumBoxes(int numBoxes) {
        this.numBoxes = numBoxes;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }
}
