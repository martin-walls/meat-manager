package com.martinwalls.meatmanager.data;

import android.text.TextUtils;

import com.martinwalls.meatmanager.data.location.MapsHelper;
import com.martinwalls.meatmanager.data.models.Location;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
public class MapsHelperTest {

    @Test
    public void getQueryString_All() {
        Location location = new Location();
        location.setLocationName("UK7132");
        location.setAddrLine1("12 Peterfield Road");
        location.setAddrLine2("Kingstown Industrial Estate");
        location.setCity("Carlisle");
        location.setPostcode("CA3 0EY");
        location.setCountry("UK");

        String expected = "12 Peterfield Road, Kingstown Industrial Estate, Carlisle, CA3 0EY, UK";

        assertThat(MapsHelper.getQueryString(location)).isEqualTo(expected);
    }

    @Test
    public void getQueryString_Necessary() {
        Location location = new Location();
        location.setAddrLine1("12 Peterfield Road");
        location.setPostcode("CA3 0EY");
        location.setCountry("UK");

        String expected = "12 Peterfield Road, CA3 0EY, UK";

        assertThat(MapsHelper.getQueryString(location)).isEqualTo(expected);
    }

    @Test
    public void getQueryString_Empty() {
        Location location = new Location();
        assertThat(MapsHelper.getQueryString(location)).isEqualTo("");
    }

    @Test
    public void encodeQuery_All() {
        Location location = new Location();
        location.setLocationName("UK7132");
        location.setAddrLine1("12 Peterfield Road");
        location.setAddrLine2("Kingstown Industrial Estate");
        location.setCity("Carlisle");
        location.setPostcode("CA3 0EY");
        location.setCountry("UK");

        String expected = "12+Peterfield+Road%2C+Kingstown+Industrial+Estate%2C+Carlisle%2C+CA3+0EY%2C+UK";

        assertThat(MapsHelper.getEncodedQuery(location)).isEqualTo(expected);
    }

    @Test
    public void encodeQuery_Necessary() {
        Location location = new Location();
        location.setAddrLine1("12 Peterfield Road");
        location.setPostcode("CA3 0EY");
        location.setCountry("UK");

        String expected = "12+Peterfield+Road%2C+CA3+0EY%2C+UK";

        assertThat(MapsHelper.getEncodedQuery(location)).isEqualTo(expected);
    }

    @Test
    public void encodeQuery_Empty() {
        Location location = new Location();
        assertThat(MapsHelper.getEncodedQuery(location)).isEqualTo("");
    }

    @Test
    public void encodeUrl_Normal() {
        String url = "12 High Street, Lancaster, LA1 4BF";
        String expected = "12+High+Street%2C+Lancaster%2C+LA1+4BF";
        assertThat(MapsHelper.encodeUrl(url)).isEqualTo(expected);
    }

    @Test
    public void encodeUrl_Empty() {
        assertThat(MapsHelper.encodeUrl("")).isEqualTo("");
    }
}
