package com.martinwalls.meatmanager.ui.locations.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.databinding.ActivityLocationsBinding;
import com.martinwalls.meatmanager.ui.locations.detail.LocationDetailActivity;
import com.martinwalls.meatmanager.ui.locations.edit.NewLocationActivity;
import com.martinwalls.meatmanager.ui.common.recyclerview.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.common.recyclerview.RecyclerViewDivider;
import com.martinwalls.meatmanager.ui.common.recyclerview.SwipeToDeleteCallback;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.UndoableAction;
import com.martinwalls.meatmanager.util.undo.location.DeleteLocationAction;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity
        implements LocationsAdapter.LocationsAdapterListener {

    private ActivityLocationsBinding binding;
    private LocationsViewModel viewModel;
    private LocationsAdapter locationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(R.string.locations_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this).get(LocationsViewModel.class);

        binding.recyclerView.setEmptyView(binding.empty);

        // init adapter
        locationsAdapter = new LocationsAdapter(this, this);
        binding.recyclerView.setAdapter(locationsAdapter);

        binding.recyclerView.addItemDecoration(new RecyclerViewDivider(this, R.drawable.divider_thin));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToDeleteCallback(locationsAdapter, this));
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        // observe locations list
        viewModel.getLocationsObservable().observe(this,
                locations -> locationsAdapter.setLocationList(locations));

        binding.fab.setOnClickListener(v -> startNewLocationActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadLocations();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the detail screen for a location when it is clicked.
     */
    @Override
    public void onLocationClicked(Location location) {
        Intent detailsIntent = new Intent(this, LocationDetailActivity.class);
        detailsIntent.putExtra(LocationDetailActivity.EXTRA_LOCATION_ID,
                location.getLocationId());
        startActivity(detailsIntent);
    }

    @Override
    public boolean deleteLocation(Location location) {
        boolean success;
        if (!viewModel.isLocationSafeToDelete(location.getLocationId())) {
            Toast.makeText(this,
                    getString(R.string.db_error_delete_location, location.getLocationName()),
                    Toast.LENGTH_SHORT).show();
            success = false;
        } else {
            success = viewModel.deleteLocation(location.getLocationId());
            showUndoSnackbar();
        }
        viewModel.loadLocations();
        return success;
    }

    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo, v -> undoDeleteLocation());
        snackbar.show();
    }

    private void undoDeleteLocation() {
        UndoableAction lastAction = UndoStack.getInstance().getLastAction();
        // check whether last action added to undo stack was deleting a location
        if (lastAction instanceof DeleteLocationAction) {
            UndoStack.getInstance().undo(this);
        } else {
            Toast.makeText(this, getString(R.string.location_undo_delete_error), Toast.LENGTH_SHORT).show();
        }
        viewModel.loadLocations();
    }

    /**
     * Opens {@link NewLocationActivity} so the user can add a new {@link Location}.
     */
    private void startNewLocationActivity() {
        Intent editLocationIntent = new Intent(this, NewLocationActivity.class);
        startActivity(editLocationIntent);
    }
}
