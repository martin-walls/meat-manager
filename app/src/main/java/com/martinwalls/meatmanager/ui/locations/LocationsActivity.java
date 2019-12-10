package com.martinwalls.meatmanager.ui.locations;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.ui.common.recyclerview.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.common.recyclerview.RecyclerViewDivider;
import com.martinwalls.meatmanager.ui.common.recyclerview.SwipeToDeleteCallback;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity
        implements LocationsAdapter.LocationsAdapterListener {

    private DBHandler dbHandler;

    private LocationsAdapter locationsAdapter;
    private List<Location> locationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        getSupportActionBar().setTitle(R.string.locations_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);

        initLocationsListView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startNewLocationActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(this);
        }
        loadLocations();
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

    /**
     * Initialises the view to display the list of locations.
     */
    private void initLocationsListView() {
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);

        TextView emptyView = findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        locationsAdapter = new LocationsAdapter(locationList, this, this);
        recyclerView.setAdapter(locationsAdapter);
        loadLocations();

        // add item dividers
        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(this, R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set swipe to delete action
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToDeleteCallback(locationsAdapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Opens {@link NewLocationActivity} so the user can add a new {@link Location}.
     */
    private void startNewLocationActivity() {
        Intent editLocationIntent = new Intent(this, NewLocationActivity.class);
        startActivity(editLocationIntent);
    }

    /**
     * Gets all locations from the database and updates the layout.
     */
    private void loadLocations() {
        locationList.clear();
        locationList.addAll(SortUtils.mergeSort(dbHandler.getAllLocations(),
                Location.comparatorAlpha()));
        locationsAdapter.notifyDataSetChanged();
    }
}
