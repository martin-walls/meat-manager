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
import com.martinwalls.nea.Utils;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.components.SwipeToDeleteCallback;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.db.models.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocationsActivity extends AppCompatActivity {

    private LocationsAdapter locationsAdapter;
    private List<Location> locationList = new ArrayList<>();

    private DBHandler dbHandler;

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

        locationsAdapter = new LocationsAdapter(locationList, this);
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
            Intent editLocationIntent = new Intent(this, EditLocationsActivity.class);
            startActivity(editLocationIntent);
        });
    }

    @Override
    protected void onResume() {
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

    private void loadLocations() {
        locationList.clear();
        locationList.addAll(Utils.mergeSort(dbHandler.getAllLocations(), new Comparator<Location>() {
            @Override
            public int compare(Location location1, Location location2) {
                return location1.getLocationName().compareTo(location2.getLocationName());
            }
        }));
        locationsAdapter.notifyDataSetChanged();
    }

    //todo be able to edit locations
}
