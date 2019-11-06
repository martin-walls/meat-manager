package com.martinwalls.nea.data.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Contract {

    private int contractId;
    private int destId;
    private String destName;
    private Interval repeatInterval;
    // which day of week/month to repeat on (first day is 1),
    // start of week is MONDAY
    private int repeatOn;
    private LocalDate startDate;
    private int reminder;

    private List<ProductQuantity> productList = new ArrayList<>();

    public Contract() {}

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
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

    public int getDaysToNextRepeat() {
        LocalDate today = LocalDate.now();

        if (repeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
            int dayOfWeekToday = today.getDayOfWeek().getValue();

            int daysDifference = repeatOn - dayOfWeekToday;

            if (dayOfWeekToday > repeatOn) {
                daysDifference += 7;
            }

            return daysDifference;
        } else /* MONTH */ {
            int dayOfMonthToday = today.getDayOfMonth();
            int numDaysInMonth = today.getMonth().length(today.isLeapYear());

            int daysDifference = repeatOn - dayOfMonthToday;

            if (dayOfMonthToday > repeatOn) {
                daysDifference += numDaysInMonth;
            }

            return daysDifference;
        }
    }

    public static Comparator<Contract> comparatorId() {
        return (contract1, contract2) -> contract1.getContractId() - contract2.getContractId();
    }

    public static Comparator<Contract> comparatorDate() {
        return (contract1, contract2) -> contract1.getDaysToNextRepeat() - contract2.getDaysToNextRepeat();
    }
}
