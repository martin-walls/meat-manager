package com.martinwalls.meatmanager.data.models;

import java.io.Serializable;

public class Interval implements Serializable {

    /**
     * Stores the different time units that can be used for intervals.
     */
    public enum TimeUnit implements Serializable {
        WEEK,
        MONTH;

        /**
         * Parses a {@link TimeUnit} from its name. This should be the same
         * as the String returned by {@link #name()};
         */
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

    /**
     * Checks whether this Interval has the same values as those given. Used to
     * compare Interval objects.
     */
    public boolean hasValues(int value, TimeUnit unit) {
        return this.value == value && this.unit.equals(unit);
    }
}
