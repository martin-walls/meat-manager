package com.martinwalls.nea.util.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.martinwalls.nea.R;
import com.martinwalls.nea.util.EasyPreferences;

import java.util.Calendar;

public class ReminderUtils {

    public static final int DEFAULT_REMINDER_HR = 9;
    public static final int DEFAULT_REMINDER_MIN = 0;

    private static final int REQUEST_CODE_REMINDER = 1;

    public static void scheduleReminder(Context context, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_REMINDER,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

        setBootReceiverEnabled(context, true);
    }

    public static void scheduleReminderAtDefaultTime(Context context) {
        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(context);
        int hour = prefs.getInt(R.string.pref_reminder_time_hr, DEFAULT_REMINDER_HR);
        int min = prefs.getInt(R.string.pref_reminder_time_min, DEFAULT_REMINDER_MIN);
        scheduleReminder(context, hour, min);
    }

    public static void cancelReminder(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_REMINDER,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        setBootReceiverEnabled(context, false);
    }

    private static void setBootReceiverEnabled(Context context, boolean enabled) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(receiver,
                enabled
                        ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
