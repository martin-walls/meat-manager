package com.martinwalls.meatmanager.data.api;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.cache.CacheHelper;
import com.martinwalls.meatmanager.data.db.ExchangeDBHandler;
import com.martinwalls.meatmanager.data.models.Currency;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * Runs in the background to load exchange rate data from the API.
 */
public class ApiIntentService extends IntentService {

    public static final String EXTRA_PENDING_RESULT = "pending_result";
    public static final String EXTRA_SUCCESS = "success";
    public static final String EXTRA_ERROR_CODE = "error_code";
    public static final String EXTRA_RESULT = "result";

    public static final int RESULT_CODE = 0;

    private static final String TAG = ApiIntentService.class.getSimpleName();

    // load native code to get API key
    static {
        System.loadLibrary("native-lib");
    }

    // method to get the API key stored in native code
    private native String getApiAccessKey();

    /**
     * The base URL of the API, to be built upon.
     */
    private final String REQUEST_URL = "http://data.fixer.io/api/";

    /**
     * Currencies that will be set to favourite when the currencies are first
     * retrieved from the API.
     */
    private final String[] DEFAULT_FAV_CURRENCIES = {"GBP", "EUR", "USD"};

    public ApiIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(EXTRA_PENDING_RESULT);
        
        String jsonResponse = getJsonResponse();

        RequestStatus status = JsonParser.getRequestStatus(jsonResponse);
        // if request error, return
        if (!status.isSuccess()) {
            handleApiError(status, reply);
            return;
        }

        HashMap<String, Double> rates = JsonParser.parseExchangeRates(jsonResponse);

        // save API response to cache
        CacheHelper.save(getApplicationContext(), R.string.cache_rates, jsonResponse);

        long timestamp = JsonParser.parseTimestamp(jsonResponse);
        CacheHelper.save(getApplicationContext(),
                R.string.cache_timestamp, String.valueOf(timestamp));

        // save list of currencies to db
        ExchangeDBHandler dbHandler = new ExchangeDBHandler(this);
        if (dbHandler.getCurrencyCount() == 0) {
            List<Currency> currencyList = fetchCurrencies();
            if (currencyList != null) {
                setDefaultFavCurrencies(currencyList);
                dbHandler.addAllCurrencies(currencyList);
            }
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_SUCCESS, true);
        result.putExtra(EXTRA_RESULT, rates);

        try {
            reply.send(this, RESULT_CODE, result);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decodes the API access key.
     */
    private String getDecodedAccessKey() {
        // decode the access key from base 64
        String base64Key = getApiAccessKey();
        return new String(Base64.getDecoder().decode(base64Key));
    }

    /**
     * Calculates how many seconds ago the last data was retrieved from the API
     * and stored in cache.
     *
     * @return the age of last cached data, in seconds. Returns -1 if no cached data.
     */
    private long getTimeDiffFromLastCache() {
        long timeDiff = -1;
        if (CacheHelper.doesCacheExist(getApplicationContext(), R.string.cache_timestamp)) {
            long lastTimestamp = Long.parseLong(
                    CacheHelper.retrieve(getApplicationContext(), R.string.cache_timestamp));
            long timestampNow = System.currentTimeMillis() / 1000;
            timeDiff = timestampNow - lastTimestamp;
        }
        return timeDiff;
    }

    /**
     * Checks whether there is any up-to-date cached data, if there is it will
     * fetch the cached JSON, otherwise fetches fresh data from the API.
     */
    private String getJsonResponse() {
        // if cache doesn't exists, API request will be made
        long timeDiff = getTimeDiffFromLastCache();

        String jsonResponse;
        // if last cache from less than 1 hr ago
        if (timeDiff != -1 && timeDiff < 60 * 60) {
            jsonResponse = CacheHelper.retrieve(getApplicationContext(), R.string.cache_rates);
        } else {
            // make new API request as cache is > 1 hr old
            jsonResponse = fetchExchangeJsonFromApi();
        }
        return jsonResponse;
    }

    /**
     * Fetches the JSON response for the latest exchange rates from the API.
     */
    private String fetchExchangeJsonFromApi() {
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendPath("latest");
        builder.appendQueryParameter("access_key", getDecodedAccessKey());

        return QueryUtils.fetchJsonResponse(builder.toString());
    }

    /**
     * Sets the currencies specified in {@link #DEFAULT_FAV_CURRENCIES} to be
     * favourited, and all others not. This makes sure that when the currencies
     * are first loaded from the API, some are already selected, otherwise
     * there would be none to show on the exchange page.
     * <p>Modifies the items directly in the original list.
     */
    private void setDefaultFavCurrencies(List<Currency> currencyList) {
        for (Currency currency : currencyList) {
            if (Arrays.asList(DEFAULT_FAV_CURRENCIES).contains(currency.getCode())) {
                currency.setFavourite(true);
            } else {
                currency.setFavourite(false);
            }
        }
    }

    /**
     * Fetches the supported currencies from the API. Returns {@code null} if
     * there is an error with the API request.
     */
    @Nullable
    private List<Currency> fetchCurrencies() {
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendPath("symbols");
        builder.appendQueryParameter("access_key", getDecodedAccessKey());

        String jsonResponse = QueryUtils.fetchJsonResponse(builder.toString());

        RequestStatus status = JsonParser.getRequestStatus(jsonResponse);
        if (status.isSuccess()) {
            return JsonParser.parseCurrencies(jsonResponse);
        } else {
            return null;
        }
    }

    /**
     * This is called when the API returns an error. Sends the reply back
     * to the calling activity with the error code.
     */
    private void handleApiError(RequestStatus status, PendingIntent reply) {
        Intent result = new Intent();
        result.putExtra(EXTRA_SUCCESS, false);
        result.putExtra(EXTRA_ERROR_CODE, status.getCode());
        try {
            reply.send(this, RESULT_CODE, result);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
