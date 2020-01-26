package com.martinwalls.meatmanager.ui.dashboard;

import java.util.Comparator;

public class BarChartEntry {

    private String name;
    private float amount;
    private float amountRequired;

    public BarChartEntry(String name, float amount, float amountRequired) {
        this.name = name;
        this.amount = amount;
        this.amountRequired = amountRequired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmountRequired() {
        return amountRequired;
    }

    public void setAmountRequired(float amountRequired) {
        this.amountRequired = amountRequired;
    }

    public static Comparator<BarChartEntry> comparatorNameAlpha() {
        return (entry1, entry2) -> entry1.getName().compareTo(entry2.getName());
    }

    public static Comparator<BarChartEntry> comparatorAmount(boolean asc) {
        return (entry1, entry2) -> {
            if (entry1.getAmount() - entry2.getAmount() > 0) {
                return asc ? 1 : -1;
            } else if (entry1.getAmount() - entry2.getAmount() < 0) {
                return asc ? -1 : 1;
            } else {
                if (entry1.getAmountRequired() - entry2.getAmountRequired() > 0) {
                    return asc ? 1 : -1;
                } else if (entry1.getAmountRequired() - entry2.getAmountRequired() < 0) {
                    return asc ? -1 : 1;
                } else {
                    return 0;
                }
            }
        };
    }
}
