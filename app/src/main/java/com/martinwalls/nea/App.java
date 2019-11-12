package com.martinwalls.nea;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.martinwalls.nea.util.DarkTheme;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.notification.ReminderUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // load theme on startup
        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(this);
        DarkTheme.setDarkTheme(prefs.getIntFromString(R.string.pref_dark_theme, DarkTheme.MODE_NIGHT_AUTO));

        // start on default page so clear saved position
        prefs.setString(R.string.pref_last_opened_page, null);

        // make sure the notification channel exists so the app can send notifications
        createNotificationChannel();

        // if notifications are enabled in settings
        if (prefs.getBoolean(R.string.pref_enable_notifications, false)) {
            ReminderUtils.scheduleReminderAtDefaultTime(this);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.channel_reminder_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channelId = getString(R.string.channel_reminder_id);
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
