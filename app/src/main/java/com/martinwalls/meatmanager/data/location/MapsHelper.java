package com.martinwalls.meatmanager.data.location;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.VisibleForTesting;

import com.martinwalls.meatmanager.data.models.Location;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MapsHelper {

    private static final String BASE_URL = "https://www.google.com/maps/search/?api=1&query=";

    private MapsHelper() {}

    public static Intent getMapsIntent(Location location) {
        String query = getEncodedQuery(location);
        String url = BASE_URL + query;

        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    @VisibleForTesting
    public static String getEncodedQuery(Location location) {
        return encodeUrl(getQueryString(location));
    }

    @VisibleForTesting
    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @VisibleForTesting
    public static String getQueryString(Location location) {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(location.getAddrLine1())) {
            builder.append(location.getAddrLine1());
            builder.append(", ");
        }
        if (!TextUtils.isEmpty(location.getAddrLine2())) {
            builder.append(location.getAddrLine2());
            builder.append(", ");
        }
        if (!TextUtils.isEmpty(location.getCity())) {
            builder.append(location.getCity());
            builder.append(", ");
        }
        if (!TextUtils.isEmpty(location.getPostcode())) {
            builder.append(location.getPostcode());
            builder.append(", ");
        }
        if (!TextUtils.isEmpty(location.getCountry())) {
            builder.append(location.getCountry());
        }
        return builder.toString();
    }
}
