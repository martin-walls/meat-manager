package com.martinwalls.nea.exchange;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;

public class ApiIntentService extends IntentService {

    private static final String TAG = ApiIntentService.class.getSimpleName();

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

        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendPath("latest");
        builder.appendQueryParameter("access_key", "3d73aaa11d179de975bea3bb6b2545bc");
        builder.appendQueryParameter("symbols", "USD,AUD,CAD,PLN,MXN");

        Intent result = new Intent();
        HashMap<String, Double> rates = QueryUtils.fetchExchangeData(builder.toString());

        result.putExtra(EXTRA_RESULT, rates);

        try {
            reply.send(this, RESULT_CODE, result);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
