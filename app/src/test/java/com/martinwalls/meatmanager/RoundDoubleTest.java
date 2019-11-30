package com.martinwalls.meatmanager;

import com.martinwalls.meatmanager.util.Utils;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class RoundDoubleTest {

    @Test
    public void stringFormat_roundToDp_0_down() {
        assertThat(Utils.roundToDp(5.136, 0))
                .isEqualTo("5");
    }

    @Test
    public void stringFormat_roundToDp_0_up() {
        assertThat(Utils.roundToDp(5.536, 0))
                .isEqualTo("6");
    }

    @Test
    public void stringFormat_roundToDp_1_down() {
        assertThat(Utils.roundToDp(5.7146, 1))
                .isEqualTo("5.7");
    }

    @Test
    public void stringFormat_roundToDp_1_up() {
        assertThat(Utils.roundToDp(5.7556, 1))
                .isEqualTo("5.8");
    }

    @Test
    public void stringFormat_roundToDp_2_down() {
        assertThat(Utils.roundToDp(5.7146, 2))
                .isEqualTo("5.71");
    }

    @Test
    public void stringFormat_roundToDp_2_up() {
        assertThat(Utils.roundToDp(5.7156, 2))
                .isEqualTo("5.72");
    }

    @Test
    public void stringFormat_roundToDp_3_down() {
        assertThat(Utils.roundToDp(5.7142, 3))
                .isEqualTo("5.714");
    }

    @Test
    public void stringFormat_roundToDp_3_up() {
        assertThat(Utils.roundToDp(5.7156, 3))
                .isEqualTo("5.716");
    }

}
