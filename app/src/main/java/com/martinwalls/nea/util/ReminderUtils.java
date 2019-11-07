package com.martinwalls.nea.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.data.models.Interval;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.ui.ReminderReceiver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

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
}
