package com.martinwalls.nea.data.models;

import java.util.Arrays;
import java.util.Comparator;
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
            for (Quality q : values()) {
                if (q.getDisplayName().equalsIgnoreCase(name)) {
                    return q;
                } else if (q.name().equalsIgnoreCase(name)) {
                    return q;
                }
            }
            return null;
        }

        public static List<String> getQualityStrings() {
            return Arrays.stream(values()).map(Quality::getDisplayName).collect(Collectors.toList());
        }
    }

    private int stockId;
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

    public StockItem() {}

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

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
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

    public void setLocation(Location location) {
        this.locationId = location.getLocationId();
        this.locationName = location.getLocationName();
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setSupplier(Location supplier) {
        this.supplierId = supplier.getLocationId();
        this.supplierName = supplier.getLocationName();
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

    public static Comparator<StockItem> comparatorAlpha() {
        return (stock1, stock2) -> stock1.getProduct().getProductName().compareTo(stock2.getProduct().getProductName());
    }

    public static Comparator<StockItem> comparatorAmount(boolean asc) {
        return (stock1, stock2) -> {
            // to prevent rounding errors due to doubles
            if (stock1.getMass() - stock2.getMass() > 0) {
                return asc ? 1 : -1;
            } else if (stock1.getMass() - stock2.getMass() < 0) {
                return asc ? -1 : 1;
            } else {
                return 0;
            }
        };
    }

    public static Comparator<StockItem> comparatorLocation() {
        return (stock1, stock2) -> {
            if (stock1.getLocationName().equals(stock2.getLocationName())) {
                return stock1.getProduct().getProductName().compareTo(stock2.getProduct().getProductName());
            } else {
                return stock1.getLocationName().compareTo(stock2.getLocationName());
            }
        };
    }
}
