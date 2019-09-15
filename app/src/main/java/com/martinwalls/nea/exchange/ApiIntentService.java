package com.martinwalls.nea.exchange;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import com.martinwalls.nea.db.ExchangeDbHandler;
import com.martinwalls.nea.models.Currency;

import java.util.HashMap;
import java.util.List;

public class ApiIntentService extends IntentService {

    private static final String TAG = ApiIntentService.class.getSimpleName();

    private static final String CACHE_KEY_RATES = "rates_cache";
    private static final String CACHE_KEY_TIMESTAMP = "last_cache_timestamp";

    public static final String EXTRA_PENDING_RESULT = "pending_result";
    public static final String EXTRA_RESULT = "result";

    public static final int RESULT_CODE = 0;

    private final String REQUEST_URL = "http://data.fixer.io/api/";

    public ApiIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(EXTRA_PENDING_RESULT);

        // if cache doesn't exists, API request will be made
        long timeDiff = 0;
        if (CacheHelper.doesCacheExist(getApplicationContext(), CACHE_KEY_TIMESTAMP)) {
            long lastTimestamp = Long.parseLong(CacheHelper.retrieve(getApplicationContext(), CACHE_KEY_TIMESTAMP));
            long timestampNow = System.currentTimeMillis() / 1000;
            timeDiff = (timestampNow - lastTimestamp);
        }

        String jsonResponse;
        if (timeDiff < 60 * 60) {
            jsonResponse = CacheHelper.retrieve(getApplicationContext(), CACHE_KEY_RATES);
        } else {
            Uri baseUri = Uri.parse(REQUEST_URL);
            Uri.Builder builder = baseUri.buildUpon();

            builder.appendPath("latest");
            builder.appendQueryParameter("access_key", "3d73aaa11d179de975bea3bb6b2545bc"); //todo store access key securely
//        builder.appendQueryParameter("symbols", "USD,AUD,CAD,PLN,MXN");


            jsonResponse = QueryUtils.fetchJsonResponse(builder.toString());
        }
        HashMap<String, Double> rates = QueryUtils.extractExchangeRates(jsonResponse);

        CacheHelper.save(getApplicationContext(), CACHE_KEY_RATES, jsonResponse);

        long timestamp = QueryUtils.extractTimestamp(jsonResponse);
        CacheHelper.save(getApplicationContext(), CACHE_KEY_TIMESTAMP, String.valueOf(timestamp));


        ExchangeDbHandler dbHandler = new ExchangeDbHandler(this);

        if (dbHandler.getCurrencyCount() == 0) {
            Uri baseUri = Uri.parse(REQUEST_URL);
            Uri.Builder builder = baseUri.buildUpon();

            builder.appendPath("symbols");
            builder.appendQueryParameter("access_key", "3d73aaa11d179de975bea3bb6b2545bc");

            List<Currency> currencyList = QueryUtils.fetchCurrencies(builder.toString());

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
}
