package com.martinwalls.nea.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

public class NotificationUtils {

    private NotificationUtils() {}

    public static void sendNotification(Context context, String channelId,
                                        String title, String text, @DrawableRes int iconId, int requestCode) {
        sendNotification(context, channelId, title, text, iconId, null, requestCode);
    }

    public static void sendNotification(Context context, String channelId,
                                        String title, String text, @DrawableRes int iconId,
                                        Class<?> onClickClass, int requestCode) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(text);

        if (onClickClass != null) {
            Intent onClickIntent = new Intent(context, onClickClass);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(onClickClass);
            stackBuilder.addNextIntent(onClickIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, builder.build());
    }


}
