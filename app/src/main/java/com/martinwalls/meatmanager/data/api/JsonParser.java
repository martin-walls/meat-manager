package com.martinwalls.meatmanager.data.api;

import com.martinwalls.meatmanager.data.models.Currency;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class JsonParser {

    private JsonParser() {}

    /**
     * Parses the JSON response to get a list of the supported currencies.
     *
     * @param json the JSON string to parse.
     */
    static List<Currency> parseCurrencies(String json) {
        List<Currency> currencyList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject symbolsObject = jsonObject.getJSONObject("symbols");

            Iterator<String> keys = symbolsObject.keys();
            while (keys.hasNext()) {
                String currencyCode = keys.next();
                String fullName = symbolsObject.getString(currencyCode);
                currencyList.add(new Currency(currencyCode, fullName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currencyList;
    }

    /**
     * Parses the JSON response to extract the timestamp of the response.
     *
     * @param json the JSON string to parse.
     */
    static long parseTimestamp(String json) {
        long timestamp = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            timestamp = jsonObject.getLong("timestamp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * Parses the JSON response and extracts the current exchange
     * rates from it.
     *
     * @param  json the JSON string to parse
     * @return      a HashMap of (Currency, Rate) pairs
     */
    static HashMap<String, Double> parseExchangeRates(String json) {
        HashMap<String, Double> rates = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");

            Iterator<String> keys = ratesObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Double value = ratesObject.getDouble(key);
                rates.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rates;
    }

    /**
     * Returns the {@link RequestStatus} of the JSON request.
     */
    static RequestStatus getRequestStatus(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                return RequestStatus.OK;
            } else {
                JSONObject errorObject = jsonObject.getJSONObject("error");
                int errorCode = errorObject.getInt("code");
                return RequestStatus.getStatusByCode(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestStatus.UnknownError;
    }
}
