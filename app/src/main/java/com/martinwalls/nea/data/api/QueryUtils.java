package com.martinwalls.nea.data.api;

import android.util.Log;
import com.martinwalls.nea.data.models.Currency;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {}

    /**
     * Retrieves the current exchange data from the API at the specified URL.
     *
     * @param urlString the URL to query
     */
    static HashMap<String, Double> fetchExchangeData(String urlString) {
        URL url = createUrl(urlString);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractExchangeRates(jsonResponse);
    }

    /**
     * Fetches the raw JSON response from the API at the specified URL.
     *
     * @param urlString the URL to query
     */
    static String fetchJsonResponse(String urlString) {
        URL url = createUrl(urlString);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    /**
     * Parses the JSON response and extracts the current exchange
     * rates from it.
     *
     * @param  json the JSON string to parse
     * @return      a HashMap of (Currency, Rate) pairs
     */
    static HashMap<String, Double> extractExchangeRates(String json) {
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
     * Queries the API at {@code urlString} and parses the response 
     * to get a list of the supported currencies.
     *
     * @param  urlString the URL to query
     * @return           a List of Currency objects
     */
    // todo refactor this to an API specific class, this class shouldn't be 
    //  so closely tied to its implementation
    static List<Currency> fetchCurrencies(String urlString) {
        URL url = createUrl(urlString);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // extract data
        List<Currency> currencies = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject symbolsObject = jsonObject.getJSONObject("symbols");
            Iterator<String> keys = symbolsObject.keys();

            while (keys.hasNext()) {
                String currencyCode = keys.next();
                String fullName = symbolsObject.getString(currencyCode);
                currencies.add(new Currency(currencyCode, fullName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currencies;
    }

    /**
     * Parses the JSON response to extract the timestamp of the response.
     *
     * @param json the JSON string to parse
     */
    static long extractTimestamp(String json) {
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
     * Converts a URL from a String to a {@link URL} object.
     */
    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Makes a HTTP request to the specified URL and gets the JSON response.
     *
     * @return The JSON response from the request.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "HTTP error code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Reads a String from the {@link InputStream}.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return builder.toString();
    }
}
