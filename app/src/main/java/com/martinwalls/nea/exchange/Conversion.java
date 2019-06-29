package com.martinwalls.nea.exchange;

public class Conversion {
    private int timestamp;
    private String primaryCurrency;
    private String secondaryCurrency;
    private String primaryValue;
    private String secondaryValue;

    public Conversion() {
    }

    public Conversion(int timestamp, String primaryCurrency, String secondaryCurrency, String primaryValue, String secondaryValue) {
        this.timestamp = timestamp;
        this.primaryCurrency = primaryCurrency;
        this.secondaryCurrency = secondaryCurrency;
        this.primaryValue = primaryValue;
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
