package com.martinwalls.nea;

import java.util.ArrayList;
import java.util.Arrays;

public class SampleData {
    private static String[] products = new String[] {
            "Beef hearts",
            "Beef livers",
            "Beef kidney",
            "Beef sinew",
            "Beef feet",
            "Beef tripe",
            "Beef reeds",
            "Beef front tendon",
            "Beef hind tendon",
            "Beef aorta",
            "Beef lip meat",
            "Beef honeycomb",
            "Beef testicles",
            "Beef pizzles",
            "Beef omasum",
            "Beef omasum pieces",
            "Beef trachea ball",
            "Lamb tripe",
            "Lamb saddle bones",
            "Lamb spare ribs",
            "Lamb paddy wack"
    };

    private static String[] locations = new String[] {
            "UK1535",
            "UK1535",
            "PL12114201",
            "UK2138",
            "UK2138",
            "UK7132",
            "UK8216",
            "EC344",
            "UK1541",
            "UK1541",
            "UK1541",
            "UK1541",
            "UK4067",
            "UK7198",
            "UK8185",
            "UK2066",
            "IE2477",
            "MIXED",
            "UK1160"
    };

    private static String[] qualities = new String[] {
            "Good",
            "Pet food",
            "Waste"
    };

    public static ArrayList<String> getSampleProducts() {
        return new ArrayList<>(Arrays.asList(products));
    }

    public static ArrayList<String> getSampleLocations() {
        return new ArrayList<>(Arrays.asList(locations));
    }

    public static ArrayList<String> getSampleQualities() {
        return new ArrayList<>(Arrays.asList(qualities));
    }
}
