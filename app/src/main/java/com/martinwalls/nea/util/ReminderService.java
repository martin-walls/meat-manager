package com.martinwalls.nea.util;

import android.app.job.JobParameters;
import android.app.job.JobService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.martinwalls.nea.R;

public class ReminderService extends JobService {

    public static final int JOB_ID = 1;

    @Override
    public boolean onStartJob(JobParameters params) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, getString(R.string.channel_reminder_id))
                        .setSmallIcon(R.drawable.ic_date)
                        .setContentTitle("TITLE")
                        .setContentText("TEXT");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());


        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
