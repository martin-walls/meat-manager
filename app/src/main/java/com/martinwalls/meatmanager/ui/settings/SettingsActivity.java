package com.martinwalls.meatmanager.ui.settings;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.martinwalls.meatmanager.BuildConfig;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.ui.tutorial.TutorialActivity;
import com.martinwalls.meatmanager.util.AppTheme;
import com.martinwalls.meatmanager.util.EasyPreferences;
import com.martinwalls.meatmanager.util.notification.ReminderUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDarkTheme();
    }

    /**
     * Sets the app theme, to be called when the activity starts so the theme
     * changes when the user changes the dark theme preference.
     */
    private void setDarkTheme() {
//        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(this);
        EasyPreferences prefs = EasyPreferences.getInstance(this);
        AppTheme.setAppTheme(
                prefs.getIntFromString(R.string.pref_theme, AppTheme.MODE_AUTO));
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private EasyPreferences prefs;

        private Preference reminderTimePref;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            prefs = EasyPreferences.getInstance(getContext());

            Preference darkThemePref = findPreference(R.string.pref_theme);

            darkThemePref.setOnPreferenceChangeListener((preference, newValue) -> {
                getActivity().recreate();
                return true;
            });

            Preference enableNotificationsPref =
                    findPreference(R.string.pref_enable_notifications);
            enableNotificationsPref.setOnPreferenceChangeListener((preference, newValue) -> {
                setNotificationsEnabled((boolean) newValue);
                return true;
            });


            reminderTimePref = findPreference(R.string.pref_reminder_time);
            reminderTimePref.setOnPreferenceClickListener(preference -> {
                showReminderTimeDialog();
                return true;
            });

            int hour = prefs.getInt(R.string.pref_reminder_time_hr,
                    ReminderUtils.DEFAULT_REMINDER_HR);
            int min = prefs.getInt(R.string.pref_reminder_time_min,
                    ReminderUtils.DEFAULT_REMINDER_MIN);
            setReminderTimeSummary(hour, min);

            ///////// todo TESTING: remove from production
            if (BuildConfig.BUILD_TYPE.equals("debug")) {
                Preference testCategory = findPreference("pref_test");
                testCategory.setVisible(true);

                Preference showTutorialPref = findPreference("show_tutorial");
                showTutorialPref.setOnPreferenceClickListener(preference -> {
                    Intent tutorialIntent = new Intent(getContext(), TutorialActivity.class);
                    startActivity(tutorialIntent);
                    getActivity().finish();
                    return true;
                });
            }
        }

        /**
         * Finds the {@link Preference} with the given key. This should be used
         * rather than {@link #findPreference(CharSequence)} as this forces
         * the use of a string resource, so it is much less likely to be mistyped.
         *
         * @see #findPreference(CharSequence)
         */
        <T extends Preference> T findPreference(@StringRes int keyId) {
            return findPreference(getString(keyId));
        }

        /**
         * Enable or disable notifications when the user changes the 
         * preference. Either schedules reminders to be active or cancels
         * any active reminders.
         */
        private void setNotificationsEnabled(boolean enabled) {
            if (enabled) {
                ReminderUtils.scheduleReminderAtDefaultTime(getContext());
            } else {
                ReminderUtils.cancelReminder(getContext());
            }
        }

        /**
         * Sets the summary text for the reminder time preference.
         */
        private void setReminderTimeSummary(int hour, int min) {
            reminderTimePref.setSummary(
                    getString(R.string.settings_reminder_time_summary, hour, min));
        }

        /**
         * Listener to change the reminder time when a time is chosen by the
         * user in the {@link TimePickerDialog}. Makes sure the next reminder 
         * is scheduled for the new time (if notifications are enabled).
         */
        private TimePickerDialog.OnTimeSetListener timePickerListener =
                (view, hourOfDay, minute) -> {
            prefs.setInt(R.string.pref_reminder_time_hr, hourOfDay);
            prefs.setInt(R.string.pref_reminder_time_min, minute);
            setReminderTimeSummary(hourOfDay, minute);

            if (prefs.getBoolean(R.string.pref_enable_notifications, false)) {
                ReminderUtils.cancelReminder(getContext());
                ReminderUtils.scheduleReminderAtDefaultTime(getContext());
            }
        };

        /**
         * Shows a {@link TimePickerDialog} so the user can choose a time
         * for reminders.
         */
        private void showReminderTimeDialog() {
            int hour = prefs.getInt(R.string.pref_reminder_time_hr, 9);
            int min = prefs.getInt(R.string.pref_reminder_time_min, 0);
            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(getContext(), timePickerListener, hour, min, true);
            timePickerDialog.show();
        }
    }
}
