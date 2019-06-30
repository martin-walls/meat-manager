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
    private Location location;
    private Location supplier;
    private Location dest;
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
    public StockItem(Product product, Location location, Location supplier, double mass, Quality quality) {
        this.product = product;
        this.location = location;
        this.supplier = supplier;
        this.mass = mass;
        this.quality = quality;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getSupplier() {
        return supplier;
    }

    public void setSupplier(Location supplier) {
        this.supplier = supplier;
    }

    public Location getDest() {
        return dest;
    }

    public void setDest(Location dest) {
        this.dest = dest;
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
