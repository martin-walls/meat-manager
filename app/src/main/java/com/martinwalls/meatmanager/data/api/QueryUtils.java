package com.martinwalls.meatmanager.data.api;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Helper class to make HTTP requests and get the JSON response.
 */
class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * This shouldn't be instantiated
     */
    private QueryUtils() {}

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
            if (urlConnection != null) urlConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * Reads a String from the {@link InputStream}.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
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
