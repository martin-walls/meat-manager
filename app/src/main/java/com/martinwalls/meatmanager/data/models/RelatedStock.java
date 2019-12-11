package com.martinwalls.meatmanager.data.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store stock of a certain product, corresponding to products in an
 * order or contract.
 */
public class RelatedStock {
    private Product product;
    private List<StockItem> stockItemList = new ArrayList<>();
    private double totalMass = 0;

    public RelatedStock() {}

    public RelatedStock(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<StockItem> getStockItemList() {
        return stockItemList;
    }

    public void addStockItem(StockItem stockItem) {
        stockItemList.add(stockItem);
        totalMass += stockItem.getMass();
    }

    public double getTotalMass() {
        return totalMass;
    }
}
