package com.martinwalls.nea;

import com.martinwalls.nea.db.models.Product;
import com.martinwalls.nea.db.models.StockItem;
import com.martinwalls.nea.exchange.Conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleData {
    private static StockItem[] stockItems = new StockItem[] {
            new StockItem(new Product("Beef hearts"), 1900.00, 100),
            new StockItem(new Product("Beef livers"), 1540, -1),
            new StockItem(new Product("Beef kidney"), 3100.0, 0),
            new StockItem(new Product("Lamb tripe"), 1401.74, 1),
    };

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

    private static String[] meatTypes = new String[] {
            "Beef",
            "Lamb",
            "Pork",
            "Chicken"
    };

    private static String[] currencies = new String[] {
            "EUR",
            "GBP",
            "HKD",
            "PLN",
            "USD"
    };

    private static Conversion[] conversions = new Conversion[] {
            new Conversion(1561313195, "GBP", "HKD", "30.00", "150.00"),
            new Conversion(1561313249, "HKD", "PLN", "20.00", "30.00"),
            new Conversion(1561313004, "HKD", "PLN", "50.00", "75.00"),
            new Conversion(1561311857, "GBP", "USD", "10.00", "12.35"),
            new Conversion(1561185385, "USD", "EUR", "8.00", "7.89"),
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

    public static ArrayList<String> getSampleMeatTypes() {
        return new ArrayList<>(Arrays.asList(meatTypes));
    }

    public static String[] getSampleCurrencies() {
        return currencies;
    }

    public static List<Conversion> getSampleConversions() {
        return new ArrayList<>(Arrays.asList(conversions));
    }

    public static List<StockItem> getSampleStock() {
        return new ArrayList<>(Arrays.asList(stockItems));
    }
}