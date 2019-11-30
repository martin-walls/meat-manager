package com.martinwalls.meatmanager;

import com.martinwalls.meatmanager.util.Utils;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UnitConversionTest {

    @Test
    public void unitConversion_kgToLbs_1() {
        assertThat(Utils.convertToLbs(1))
                .isWithin(0.01)
                .of(2.20462);
    }

    @Test
    public void unitConversion_kgToLbs_small() {
        assertThat(Utils.convertToLbs(0.25))
                .isWithin(0.01)
                .of(0.5511557);
    }

    @Test
    public void unitConversion_kgToLbs_medium() {
        assertThat(Utils.convertToLbs(400))
                .isWithin(0.01)
                .of(881.849);
    }

    @Test
    public void unitConversion_kgToLbs_large() {
        assertThat(Utils.convertToLbs(2100))
                .isWithin(0.01)
                .of(4629.708);
    }

    @Test
    public void unitConversion_lbsToKg_1() {
        assertThat(Utils.convertToKgs(1))
                .isWithin(0.01)
                .of(0.453592);
    }

    @Test
    public void unitConversion_lbsToKg_small() {
        assertThat(Utils.convertToKgs(0.25))
                .isWithin(0.01)
                .of(0.1133981);
    }

    @Test
    public void unitConversion_lbsToKg_medium() {
        assertThat(Utils.convertToKgs(400))
                .isWithin(0.01)
                .of(181.437);
    }

    @Test
    public void unitConversion_lbsToKg_large() {
        assertThat(Utils.convertToKgs(2100))
                .isWithin(0.01)
                .of(952.544);
    }
}
