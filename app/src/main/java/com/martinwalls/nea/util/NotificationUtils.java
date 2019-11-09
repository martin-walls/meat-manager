package com.martinwalls.nea.util;

import android.app.PendingIntent;
import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtils {

    public static final String GROUP_CONTRACT_REMINDER = "com.martinwalls.nea.CONTRACT_REMINDER";

    private NotificationUtils() {}

    public static void sendNotification(Context context, String channelId,
                                        String title, String text, @DrawableRes int iconId,
                                        int notificationId, String groupKey) {
        sendNotification(context, channelId, title, text, iconId, null, notificationId, groupKey);
    }

    public static void sendNotification(Context context, String channelId,
                                        String title, String text, @DrawableRes int iconId,
                                        PendingIntent pendingIntent, int notificationId, String groupKey) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(text)
                .setGroup(groupKey)
                .setGroupSummary(true)
                .setAutoCancel(true);

//        if (onClickClass != null) {
//            Intent onClickIntent = new Intent(context, onClickClass);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addNextIntentWithParentStack(onClickIntent);
//
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            builder.setContentIntent(pendingIntent);
//        }

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }


}
