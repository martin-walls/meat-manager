package com.martinwalls.nea.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Contract {
    public static Comparator<Contract> comparatorId =
            (contract1, contract2) -> contract1.getContractId() - contract2.getContractId();

    private int contractId;
    private int destId;
    private String destName;
    private Interval repeatInterval;
    private int repeatOn;
    private int reminder;

    private List<ProductQuantity> productList = new ArrayList<>();

    public Contract() {
    }

    public Contract(int contractId, int destId, Interval repeatInterval, int repeatOn,
                    int reminder, List<ProductQuantity> productList) {
        this.contractId = contractId;
        this.destId = destId;
        this.repeatInterval = repeatInterval;
        this.repeatOn = repeatOn;
        this.reminder = reminder;
        this.productList = productList;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
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

    public Interval getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(Interval repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public int getRepeatOn() {
        return repeatOn;
    }

    public void setRepeatOn(int repeatOn) {
        this.repeatOn = repeatOn;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public List<ProductQuantity> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductQuantity> productList) {
        this.productList = productList;
    }

    public void addProduct(Product product, double quantityMass) {
        productList.add(new ProductQuantity(product, quantityMass));
    }

    public void addProduct(Product product, double quantityMass, int quantityBoxes) {
        productList.add(new ProductQuantity(product, quantityMass, quantityBoxes));
    }
}
