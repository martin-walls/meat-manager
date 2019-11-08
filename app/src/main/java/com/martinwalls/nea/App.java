package com.martinwalls.nea;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.martinwalls.nea.util.DarkTheme;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.ReminderUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // load theme on startup
        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(this);
        DarkTheme.setDarkTheme(prefs.getIntFromString(R.string.pref_dark_theme, DarkTheme.MODE_NIGHT_AUTO));

        // start on default page so clear saved position
        prefs.setString(R.string.pref_last_opened_page, null);

        createNotificationChannel();

        if (!prefs.getBoolean(R.string.test_pref_reminder_set, false)) {
            ReminderUtils.scheduleReminder(this);
            prefs.setBoolean(R.string.test_pref_reminder_set, true);
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
