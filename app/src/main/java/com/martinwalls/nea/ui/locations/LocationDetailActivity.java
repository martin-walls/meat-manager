package com.martinwalls.nea.ui.locations;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Location;

public class LocationDetailActivity extends AppCompatActivity {

    public static final String EXTRA_LOCATION_ID = "location_id";

    public static final int REQUEST_REFRESH_ON_DONE = 1;

    private DBHandler dbHandler;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.location_details_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int locationId = extras.getInt(EXTRA_LOCATION_ID);
            location = dbHandler.getLocation(locationId);
        }

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
                deleteLocation();
                return true;
            case R.id.action_edit:
                Intent editIntent = new Intent(this, EditLocationActivity.class);
                editIntent.putExtra(EditLocationActivity.EXTRA_LOCATION_ID, location.getLocationId());
                startActivityForResult(editIntent, REQUEST_REFRESH_ON_DONE);
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

    /**
     * Initialises fields with data from the {@link Location} being shown.
     */
    private void fillData() {
        TextView name = findViewById(R.id.name);
        name.setText(location.getLocationName());

        TextView locationType = findViewById(R.id.location_type);
        locationType.setText(location.getLocationType().name());

        TextView address = findViewById(R.id.address);
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
        address.setText(builder.toString());

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
     * Checks if this {@link Location} is safe to delete, removes it from
     * the database if it is.
     */
    private void deleteLocation() {
        if (dbHandler.isLocationSafeToDelete(location.getLocationId())) {
            boolean success = dbHandler.deleteLocation(location.getLocationId());
            if (success) {
                Toast.makeText(this, R.string.location_delete_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.location_delete_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.location_delete_error, Toast.LENGTH_SHORT).show();
        }
    }
}
