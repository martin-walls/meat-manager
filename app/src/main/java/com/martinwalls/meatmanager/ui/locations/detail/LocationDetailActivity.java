package com.martinwalls.meatmanager.ui.locations.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.location.MapsHelper;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.ui.locations.edit.EditLocationActivity;
import com.martinwalls.meatmanager.ui.common.dialog.ConfirmDeleteDialog;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.location.DeleteLocationAction;

public class LocationDetailActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.ConfirmDeleteListener {

    public static final String EXTRA_LOCATION_ID = "location_id";

    public static final int REQUEST_REFRESH_ON_DONE = 1;

    private DBHandler dbHandler; //todo ViewModel
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        getSupportActionBar().setTitle(R.string.location_details_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int locationId = extras.getInt(EXTRA_LOCATION_ID);
            location = dbHandler.getLocation(locationId);
        }

        TextView address = findViewById(R.id.address);
        address.setOnClickListener(v -> openAddressInMaps());

        fillData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_location_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showConfirmDeleteDialog();
                return true;
            case R.id.action_edit:
                startEditLocationActivity();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            recreate();
        }
    }

    @Override
    public void onConfirmDelete() {
        boolean success = dbHandler.deleteLocation(location.getLocationId());
        if (success) {
            Toast.makeText(this, R.string.location_delete_success, Toast.LENGTH_SHORT)
                    .show();
            UndoStack.getInstance().push(new DeleteLocationAction(location));
            finish();
        } else {
            Toast.makeText(this, R.string.location_delete_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Opens {@link EditLocationActivity} so the user can edit this Location.
     */
    private void startEditLocationActivity() {
        Intent editIntent = new Intent(this, EditLocationActivity.class);
        editIntent.putExtra(EditLocationActivity.EXTRA_LOCATION_ID,
                location.getLocationId());
        startActivityForResult(editIntent, REQUEST_REFRESH_ON_DONE);
    }

    /**
     * Shows a {@link ConfirmDeleteDialog} asking the user to confirm the
     * delete action. First checks if the location is safe to delete, shows an
     * error message to the user if not.
     */
    private void showConfirmDeleteDialog() {
        if (dbHandler.isLocationSafeToDelete(location.getLocationId())) {
            DialogFragment dialog = new ConfirmDeleteDialog();
            Bundle args = new Bundle();
            args.putString(ConfirmDeleteDialog.EXTRA_NAME, location.getLocationName());
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "confirm_delete");
        } else {
            Toast.makeText(this,
                    getString(R.string.db_error_delete_location, location.getLocationName()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialises fields with data from the {@link Location} being shown.
     */
    private void fillData() {
        TextView name = findViewById(R.id.name);
        name.setText(location.getLocationName());

        TextView locationType = findViewById(R.id.location_type);
        locationType.setText(location.getLocationType().name());

        // combine address fields into one textview
        TextView address = findViewById(R.id.address);
        address.setText(getAddressDisplayString());

        TextView email = findViewById(R.id.email);
        if (!location.getEmail().isEmpty()) {
            email.setText(location.getEmail());
        } else {
            email.setVisibility(View.GONE);
        }

        TextView phone = findViewById(R.id.phone);
        if (!location.getPhone().isEmpty()) {
            phone.setText(location.getPhone());
        } else {
            phone.setVisibility(View.GONE);
        }
    }

    /**
     * Combines the location's address fields into one string, so it can
     * be displayed in a single TextView.
     */
    private String getAddressDisplayString() {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getAddrLine1());
        if (!location.getAddrLine2().isEmpty()) {
            builder.append("\n");
            builder.append(location.getAddrLine2());
        }
        if (!location.getCity().isEmpty()) {
            builder.append("\n");
            builder.append(location.getCity());
        }
        builder.append("\n");
        builder.append(location.getPostcode());
        builder.append("\n");
        builder.append(location.getCountry());
        return builder.toString();
    }

    /**
     * Opens Google Maps and searches for the address of this location.
     */
    private void openAddressInMaps() {
        Intent mapsIntent = MapsHelper.getMapsIntent(location);
        startActivity(mapsIntent);
    }
}
