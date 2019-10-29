package com.martinwalls.nea.data.models;

public class Interval {

    public enum TimeUnit {
        WEEK,
        MONTH;

        public static TimeUnit parseTimeUnit(String unitStr) {
            for (TimeUnit timeUnit : values()) {
                if (timeUnit.name().equalsIgnoreCase(unitStr)) {
                    return timeUnit;
                }
            }
            return null;
        }
    }

    private int value;
    private TimeUnit unit;

    public Interval() {}

    public Interval(int value, TimeUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Interval(int value, String unitStr) {
        this.value = value;
        this.unit = TimeUnit.parseTimeUnit(unitStr);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }
}
