package com.martinwalls.meatmanager.data;

public enum ValidationState {
    /**
     * Data is valid
     */
    VALID,
    /**
     * Data is in its initial state, ie. it hasn't been changed
     */
    INITIAL,
    /**
     * Data is blank when it isn't allowed to be
     */
    INVALID_BLANK,
    /**
     * Data is zero when it isn't allowed to be
     */
    INVALID_ZERO;
}
