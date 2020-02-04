package com.martinwalls.meatmanager.ui.locations.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.location.MapsHelper;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.databinding.ActivityLocationDetailBinding;
import com.martinwalls.meatmanager.ui.locations.edit.EditLocationActivity;
import com.martinwalls.meatmanager.ui.common.dialog.ConfirmDeleteDialog;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.location.DeleteLocationAction;

public class LocationDetailActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.ConfirmDeleteListener {

    public static final String EXTRA_LOCATION_ID = "location_id";

    public static final int REQUEST_REFRESH_ON_DONE = 1;

    private LocationDetailViewModel viewModel;

    private ActivityLocationDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(R.string.location_details_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get location ID passed in extras bundle
        Bundle extras = getIntent().getExtras();
        int locationId = -1;
        if (extras != null) {
            locationId = extras.getInt(EXTRA_LOCATION_ID);
        }

        // get ViewModel
        LocationDetailViewModelFactory factory =
                new LocationDetailViewModelFactory(getApplication(), locationId);
        viewModel = ViewModelProviders.of(this, factory)
                .get(LocationDetailViewModel.class);

        viewModel.getLocation().observe(this, location -> {
            binding.name.setText(location.getLocationName());

            binding.locationType.setText(location.getLocationType().name());

            binding.address.setText(getAddressDisplayString(location));

            if (!TextUtils.isEmpty(location.getEmail())) {
                binding.email.setVisibility(View.VISIBLE);
                binding.email.setText(location.getEmail());
            } else {
                binding.email.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(location.getPhone())) {
                binding.phone.setVisibility(View.VISIBLE);
                binding.phone.setText(location.getPhone());
            } else {
                binding.phone.setVisibility(View.GONE);
            }
        });

        //todo test this now that VM is added

        binding.address.setOnClickListener(v -> openAddressInMaps());
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
        boolean success = viewModel.deleteLocation();
        if (success) {
            Toast.makeText(this, R.string.location_delete_success, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.location_delete_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens {@link EditLocationActivity} so the user can edit this Location.
     */
    private void startEditLocationActivity() {
        Intent editIntent = new Intent(this, EditLocationActivity.class);
        editIntent.putExtra(EditLocationActivity.EXTRA_LOCATION_ID,
                viewModel.getLocationId());
        startActivityForResult(editIntent, REQUEST_REFRESH_ON_DONE);
    }

    /**
     * Shows a {@link ConfirmDeleteDialog} asking the user to confirm the
     * delete action. First checks if the location is safe to delete, shows an
     * error message to the user if not.
     */
    private void showConfirmDeleteDialog() {
        if (viewModel.isLocationSafeToDelete()) {
            DialogFragment dialog = new ConfirmDeleteDialog();
            Bundle args = new Bundle();
            args.putString(ConfirmDeleteDialog.EXTRA_NAME, viewModel.getLocation().getValue().getLocationName());
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "confirm_delete");
        } else {
            Toast.makeText(this,
                    getString(R.string.db_error_delete_location, viewModel.getLocation().getValue().getLocationName()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Combines the location's address fields into one string, so it can
     * be displayed in a single TextView.
     */
    private String getAddressDisplayString(Location location) {
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
        if (viewModel.getLocation().getValue() == null) return;

        Intent mapsIntent = MapsHelper.getMapsIntent(viewModel.getLocation().getValue());
        startActivity(mapsIntent);
    }
}
