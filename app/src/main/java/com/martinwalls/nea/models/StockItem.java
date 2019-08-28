package com.martinwalls.nea.models;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StockItem {
    public enum Quality {
        Good,
        Pet_Food,
        Waste;

        // replace underscore with space for display on screen
        public String getDisplayName() {
            return name().replace("_", " ");
        }

        public static Quality parseQuality(String name) {
            //todo improve this logic, this is a quick fix due to the space in "pet food"
//            switch (name.toLowerCase()) {
//                case "good":
//                    return Quality.Good;
//                case "pet food":
//                    return Quality.Pet_Food;
//                case "waste":
//                    return Quality.Waste;
//            }

            for (Quality q : values()) {
                if (q.getDisplayName().equalsIgnoreCase(name)) {
                    return q;
                }
            }
            return null;
        }

        public static List<String> getQualityStrings() {
            return Arrays.stream(values()).map(Quality::getDisplayName).collect(Collectors.toList());
        }
    }

    private Product product;
    // names are also stored for locations so they can be displayed,
    // the other data is only loaded as needed
    private int locationId;
    private String locationName;
    private int supplierId;
    private String supplierName;
    private int destId;
    private String destName;
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

    public StockItem(Product product, int locationId, String locationName, 
            int supplierId, String supplierName, double mass, Quality quality) {
        this.product = product;
        this.locationId = locationId;
        this.locationName = locationName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return locationName;
    }

    public void setSupplierName(String locationName) {
        this.locationName = locationName;
    }

    public int getDestId() {
        return destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public String getDestName() {
        return locationName;
    }

    public void setDestName(String locationName) {
        this.locationName = locationName;
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
