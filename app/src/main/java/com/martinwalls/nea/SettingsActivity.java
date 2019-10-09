package com.martinwalls.nea;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.util.DarkTheme;
import com.martinwalls.nea.util.EasyPreferences;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setDarkTheme();
    }

    private void setDarkTheme() {
        EasyPreferences prefs = EasyPreferences.createForDefaultPreferences(this);
        DarkTheme.setDarkTheme(prefs.getIntFromString(R.string.pref_dark_theme, DarkTheme.MODE_NIGHT_AUTO));
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final int PERMISSIONS_REQUEST_STORAGE_FOR_BACKUP = 1;
        private final int PERMISSIONS_REQUEST_STORAGE_FOR_RESTORE = 2;

        private Preference backupDbPref;
        private Preference restoreDbPref;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            Preference darkThemePref = findPreference(R.string.pref_dark_theme);

            darkThemePref.setOnPreferenceChangeListener((preference, newValue) -> {
                getActivity().recreate();
                return true;
            });

            backupDbPref = findPreference(R.string.pref_backup_db);
            restoreDbPref = findPreference(R.string.pref_restore_db);

            if (!isExternalStoragePermissionGranted()) {
                backupDbPref.setSummary(R.string.settings_perm_req_storage);
                restoreDbPref.setSummary(R.string.settings_perm_req_storage);
            }

            backupDbPref.setOnPreferenceClickListener(preference -> {
                if (!isExternalStoragePermissionGranted()) {
                    requestExternalStoragePermission(PERMISSIONS_REQUEST_STORAGE_FOR_BACKUP);
                } else {
                    exportDb(); //todo custom location
                }
                return true; // click handled
            });

            restoreDbPref.setOnPreferenceClickListener(preference -> {
                if (!isExternalStoragePermissionGranted()) {
                    requestExternalStoragePermission(PERMISSIONS_REQUEST_STORAGE_FOR_RESTORE);
                } else {
                    importDB(); //todo custom location
                }
                return true;
            });
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == PERMISSIONS_REQUEST_STORAGE_FOR_BACKUP
                    || requestCode == PERMISSIONS_REQUEST_STORAGE_FOR_RESTORE) {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backupDbPref.setSummary("");
                    restoreDbPref.setSummary("");
                    if (requestCode == PERMISSIONS_REQUEST_STORAGE_FOR_BACKUP) {
                        exportDb();
                    } else {
                        importDB();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.perm_error_storage_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }

        // allow use of resource id in parameter rather than string value
        <T extends Preference> T findPreference(@StringRes int keyId) {
            return findPreference(getString(keyId));
        }



        private boolean isExternalStoragePermissionGranted() {
            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }

        private void requestExternalStoragePermission(int requestCode) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }

        private void exportDb() {
            String outputPath = DBHandler.exportDbToFile();
            if (outputPath.equals("")) {
                Toast.makeText(getContext(), R.string.db_backup_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.db_backup_success, outputPath), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        private void importDB() {
            boolean success = DBHandler.importDbFromFile();
            if (success) {
                Toast.makeText(getContext(), R.string.db_import_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.db_import_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}