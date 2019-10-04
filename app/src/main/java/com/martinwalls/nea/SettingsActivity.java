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

public class SettingsActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.perm_error_storage_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkExternalStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_STORAGE);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            Preference backupDbPref = findPreference(R.string.pref_backup_db);

            backupDbPref.setOnPreferenceClickListener(preference -> {
                ((SettingsActivity) getActivity()).checkExternalStoragePermissions();
                String outputPath = DBHandler.exportDbToFile();
                if (outputPath.equals("")) {
                    Toast.makeText(getContext(), R.string.db_backup_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.db_backup_success, outputPath), Toast.LENGTH_SHORT)
                            .show();
                }
                return outputPath.equals("");
            });
        }

        <T extends Preference> T findPreference(@StringRes int keyId) {
            return findPreference(getString(keyId));
        }
    }
}