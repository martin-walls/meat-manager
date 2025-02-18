package com.martinwalls.meatmanager.util.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    /**
     * Makes sure reminder is still set if phone is restarted
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ReminderUtils.scheduleReminderAtDefaultTime(context);
        }
    }
}
