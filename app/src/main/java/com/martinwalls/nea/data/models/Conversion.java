package com.martinwalls.nea.data.models;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class Conversion {
    private int conversionId;
    private long timestamp;
    private Currency primaryCurrency;
    private Double primaryValue;
    private Currency secondaryCurrency;
    private Double secondaryValue;

    public Conversion() {
    }

    public Conversion(long timestamp, Currency primaryCurrency, Double primaryValue,
                      Currency secondaryCurrency, Double secondaryValue) {
        this.timestamp = timestamp;
        this.primaryCurrency = primaryCurrency;
        this.primaryValue = primaryValue;
        this.secondaryCurrency = secondaryCurrency;
        this.secondaryValue = secondaryValue;
    }

    public int getConversionId() {
        return conversionId;
    }

    public void setConversionId(int conversionId) {
        this.conversionId = conversionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Currency getPrimaryCurrency() {
        return primaryCurrency;
    }

    public void setPrimaryCurrency(Currency primaryCurrency) {
        this.primaryCurrency = primaryCurrency;
    }

    public Double getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(Double primaryValue) {
        this.primaryValue = primaryValue;
    }

    public Currency getSecondaryCurrency() {
        return secondaryCurrency;
    }

    public void setSecondaryCurrency(Currency secondaryCurrency) {
        this.secondaryCurrency = secondaryCurrency;
    }

    public Double getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(Double secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    public int getDaysAgo() {
        LocalDate conversionDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();

        return (int) ChronoUnit.DAYS.between(conversionDate, today);
    }

    public static Comparator<Conversion> comparatorTime() {
        return (conv1, conv2) -> (int) (conv2.getTimestamp() - conv1.getTimestamp());
    }
}
