package com.martinwalls.nea.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.martinwalls.nea.R;

import java.util.Calendar;

public class ReminderUtils {

    @Deprecated
    public static void showTestNotification(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, context.getString(R.string.channel_reminder_id))
                        .setSmallIcon(R.drawable.ic_date)
                        .setContentTitle("Notification")
                        .setContentText("Test notification that should be sent daily");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, builder.build());
    }




///////////////////////////////////////////////////////////////////////////////


    public static void scheduleReminder(Context context, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }


}
