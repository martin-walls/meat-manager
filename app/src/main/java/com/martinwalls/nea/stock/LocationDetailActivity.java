package com.martinwalls.nea.stock;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Location;

public class LocationDetailActivity extends AppCompatActivity {

    public static final String EXTRA_LOCATION_ID = "location_id";

    private DBHandler dbHandler;
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

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
