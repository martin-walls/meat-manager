package com.martinwalls.nea.components;

public class BarChartEntry {

    private String name;
    private float amount;
    private float amountRequired;

    public BarChartEntry() {
    }

    public BarChartEntry(String name, float amount) {
        this.name = name;
        this.amount = amount;
    }

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
}
