package com.martinwalls.nea.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.util.Utils;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.components.SwipeToDeleteCallback;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Location;

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

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView emptyView = findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        locationsAdapter = new LocationsAdapter(locationList, this, this);
        recyclerView.setAdapter(locationsAdapter);
        loadLocations();

        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(this, R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(locationsAdapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent editLocationIntent = new Intent(this, NewLocationActivity.class);
            startActivity(editLocationIntent);
        });
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

    @Override
    public void onLocationClicked(Location location) {
        Intent detailsIntent = new Intent(this, LocationDetailActivity.class);
        detailsIntent.putExtra(LocationDetailActivity.EXTRA_LOCATION_ID, location.getLocationId());
        startActivity(detailsIntent);
    }

    private void loadLocations() {
        locationList.clear();
        locationList.addAll(Utils.mergeSort(dbHandler.getAllLocations(), Location.comparatorAlpha()));
        locationsAdapter.notifyDataSetChanged();
    }
}
