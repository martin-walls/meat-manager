package com.martinwalls.meatmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.preference.PreferenceManager;

import com.martinwalls.meatmanager.util.AppTheme;
import com.martinwalls.meatmanager.util.EasyPreferences;
import com.martinwalls.meatmanager.util.notification.ReminderUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // load theme on startup
        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(this);
        AppTheme.setAppTheme(
                prefs.getIntFromString(R.string.pref_theme, AppTheme.MODE_AUTO));

        // start on default page so clear saved position
        prefs.setString(R.string.pref_last_opened_page, null);

        // make sure the notification channel exists so the app can send notifications
        createNotificationChannels();

        // set default values of settings; this doesn't override any the user has changed
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        // if notifications are enabled in settings
        if (prefs.getBoolean(R.string.pref_enable_notifications, false)) {
            ReminderUtils.scheduleReminderAtDefaultTime(this);
        }
    }

    /**
     * Create the required notification channels for the app. This allows the
     * app to send notifications, as the system will block notifications
     * without a channel.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create "Reminders" channel
            String name = getString(R.string.channel_reminder_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channelId = getString(R.string.channel_reminder_id);
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
