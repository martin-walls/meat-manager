package com.martinwalls.nea.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.util.ReminderUtils;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_NUM_ELAPSED = "num_elapsed";

    public static final String EXTRA_CONTRACT = "contract";

    @Override
    public void onReceive(Context context, Intent intent) {
        // show notification
        String title = intent.getStringExtra(EXTRA_TITLE);
        String text = intent.getStringExtra(EXTRA_TEXT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, context.getString(R.string.channel_reminder_id))
                        .setSmallIcon(R.drawable.ic_date)
                        .setContentTitle(title)
                        .setContentText(text);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, builder.build());

        // set next reminder
        Contract contract = (Contract) intent.getSerializableExtra(EXTRA_CONTRACT);
        int numElapsed = intent.getIntExtra(EXTRA_NUM_ELAPSED, 0);

        ReminderUtils.setReminder(context, contract, numElapsed + 1);
    }
}
