package com.martinwalls.nea.models;

public class Conversion {
    private int conversionId;
    private int timestamp;
    private String primaryCurrency;
    private Double primaryValue;
    private String secondaryCurrency;
    private Double secondaryValue;

    public Conversion() {
    }

    public Conversion(int timestamp, String primaryCurrency, Double primaryValue,
                      String secondaryCurrency, Double secondaryValue) {
        this.timestamp = timestamp;
        this.primaryCurrency = primaryCurrency;
        this.primaryValue = primaryValue;
        this.secondaryCurrency = secondaryCurrency;
        this.secondaryValue = secondaryValue;
    }

    public void setConversionId(int conversionId) {
        this.conversionId = conversionId;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setPrimaryCurrency(String primaryCurrency) {
        this.primaryCurrency = primaryCurrency;
    }

    public void setPrimaryValue(Double primaryValue) {
        this.primaryValue = primaryValue;
    }

    public void setSecondaryCurrency(String secondaryCurrency) {
        this.secondaryCurrency = secondaryCurrency;
    }

    public void setSecondaryValue(Double secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    public int getDaysAgo() {
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        int timeDiff = timeNow - timestamp;
        return timeDiff / (60 * 60 * 24);
    }

    public String getPrimaryString() {
        return primaryValue + " " + primaryCurrency;
    }

    public String getSecondaryString() {
        return secondaryValue + " " + secondaryCurrency;
    }
}
