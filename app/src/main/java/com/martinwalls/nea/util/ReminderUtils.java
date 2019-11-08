package com.martinwalls.nea.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.data.models.Interval;
import com.martinwalls.nea.data.models.ProductQuantity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;

public class ReminderUtils {

    public static void setReminder(Context context, Contract contract, int numElapsed) {
        Intent notifyIntent = new Intent(context, ReminderReceiver.class);

        notifyIntent.putExtra(ReminderReceiver.EXTRA_TITLE,
                "Upcoming contract in " + contract.getReminder() + " days");

        StringBuilder builder = new StringBuilder();
        for (ProductQuantity productQuantity : contract.getProductList()) {
            builder.append(productQuantity.getProduct().getProductName());
            builder.append(", ");
        }
        builder.delete(builder.length() - 3, builder.length() - 1);

        notifyIntent.putExtra(ReminderReceiver.EXTRA_TEXT, builder.toString());

        notifyIntent.putExtra(ReminderReceiver.EXTRA_CONTRACT, contract);
        notifyIntent.putExtra(ReminderReceiver.EXTRA_NUM_ELAPSED, numElapsed);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        LocalDate reminderDate = contract.getStartDate();
        LocalTime reminderTime = LocalTime.of(9, 0);
        LocalDateTime reminderDateTime = LocalDateTime.of(reminderDate, reminderTime);

        if (contract.getRepeatInterval().getUnit() == Interval.TimeUnit.WEEK) {
            reminderDateTime.plusWeeks(contract.getRepeatInterval().getValue() * (numElapsed + 1));
        } else /* MONTH */ {
            reminderDateTime.plusMonths(contract.getRepeatInterval().getValue() * (numElapsed + 1));
        }
        reminderDateTime.minusDays(contract.getReminder());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC,
                reminderDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                pendingIntent);
    }

    public static void scheduleReminderService(Context context) {
        ComponentName serviceName = new ComponentName(context, ReminderService.class);

        //todo use AlarmManager? time is more exact
        JobInfo jobInfo = new JobInfo.Builder(ReminderService.JOB_ID, serviceName)
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        int result = scheduler.schedule(jobInfo);

        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d("DEBUG", "Job executed successfully");
        } else {
            Log.d("DEBUG", "Job failed");
        }
    }

    public static void scheduleReminder(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, ReminderService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void showTestNotification(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, context.getString(R.string.channel_reminder_id))
                        .setSmallIcon(R.drawable.ic_date)
                        .setContentTitle("9am notification")
                        .setContentText("Test notification that should be sent daily");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, builder.build());
    }
}
