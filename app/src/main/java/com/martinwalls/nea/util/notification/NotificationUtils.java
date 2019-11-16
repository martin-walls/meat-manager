package com.martinwalls.nea.util.notification;

import android.app.PendingIntent;
import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtils {

    public static final String GROUP_CONTRACT_REMINDER =
            "com.martinwalls.nea.CONTRACT_REMINDER";
    public static final String GROUP_ORDER_REMINDER =
            "com.martinwalls.nea.ORDER_REMINDER";

    private NotificationUtils() {}

    /**
     * Sends a notification with the specified data.
     *
     * @param channelId      id of the notification channel
     * @param title          text to be displayed as the notification title
     * @param text           body text of the notification
     * @param iconId         resource id for the notification icon
     * @param pendingIntent  intent to launch on notification click
     * @param notificationId id of the notification
     * @param groupKey       key for the notification group
     */
    public static void sendNotification(Context context, String channelId,
                                        String title, String text, @DrawableRes int iconId,
                                        PendingIntent pendingIntent,
                                        int notificationId, String groupKey) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(text)
                .setGroup(groupKey)
                .setAutoCancel(true);

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }


}
