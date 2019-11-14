package com.martinwalls.nea.data.api;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import com.martinwalls.nea.data.cache.CacheHelper;
import com.martinwalls.nea.data.db.ExchangeDBHandler;
import com.martinwalls.nea.data.models.Currency;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * Runs in the background to load exchange rate data from the API.
 */
public class ApiIntentService extends IntentService {

    public static final String EXTRA_PENDING_RESULT = "pending_result";
    public static final String EXTRA_RESULT = "result";
    public static final int RESULT_CODE = 0;

    private static final String TAG = ApiIntentService.class.getSimpleName();

    private static final String CACHE_KEY_RATES = "rates_cache";
    private static final String CACHE_KEY_TIMESTAMP = "last_cache_timestamp";

    // load native code to get API key
    static {
        System.loadLibrary("native-lib");
    }

    // URL of the API
    private final String REQUEST_URL = "http://data.fixer.io/api/";

    private final String[] DEFAULT_FAV_CURRENCIES = {"GBP", "EUR", "USD"};

    public ApiIntentService() {
        super(TAG);
    }

    // method to get the API key stored in native code
    private native String getApiAccessKey();

    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(EXTRA_PENDING_RESULT);

        // if cache doesn't exists, API request will be made
        long timeDiff = getTimeDiffFromLastCache();

        String jsonResponse;
        // if last cache from less than 1 hr ago
        if (timeDiff < 60 * 60) {
            jsonResponse = CacheHelper.retrieve(getApplicationContext(), CACHE_KEY_RATES);
        } else {
            // make new API request as cache is > 1 hr old
            jsonResponse = fetchExchangeJsonFromApi();
        }
        
        HashMap<String, Double> rates = JsonParser.parseExchangeRates(jsonResponse);

        // save API response to cache
        CacheHelper.save(getApplicationContext(), CACHE_KEY_RATES, jsonResponse);

        long timestamp = JsonParser.parseTimestamp(jsonResponse);
        CacheHelper.save(getApplicationContext(),
                CACHE_KEY_TIMESTAMP, String.valueOf(timestamp));

        // save list of currencies to db
        ExchangeDBHandler dbHandler = new ExchangeDBHandler(this);
        if (dbHandler.getCurrencyCount() == 0) {

            List<Currency> currencyList = fetchCurrencies();

            // set default favourited currencies
            for (Currency currency : currencyList) {
                if (Arrays.asList(DEFAULT_FAV_CURRENCIES).contains(currency.getCode())) {
                    currency.setFavourite(true);
                } else {
                    currency.setFavourite(false);
                }
            }

            dbHandler.addAllCurrencies(currencyList);
        }

        Intent result = new Intent();
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
     * and stored in cache. Returns 0 if no cached data exists.
     */
    private long getTimeDiffFromLastCache() {
        long timeDiff = 0;
        if (CacheHelper.doesCacheExist(getApplicationContext(), CACHE_KEY_TIMESTAMP)) {
            long lastTimestamp = Long.parseLong(
                    CacheHelper.retrieve(getApplicationContext(), CACHE_KEY_TIMESTAMP));
            long timestampNow = System.currentTimeMillis() / 1000;
            timeDiff = timestampNow - lastTimestamp;
        }
        return timeDiff;
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
     * Fetches the supported currencies from the API.
     */
    private List<Currency> fetchCurrencies() {
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendPath("symbols");
        builder.appendQueryParameter("access_key", getDecodedAccessKey());

        String jsonResponse = QueryUtils.fetchJsonResponse(builder.toString());

        return JsonParser.parseCurrencies(jsonResponse);
    }
}
