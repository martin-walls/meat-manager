package com.martinwalls.nea.stock;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.db.models.Location;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditLocationsActivity extends AppCompatActivity {

    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_locations);

        getSupportActionBar().setTitle(R.string.locations_add_new_title);

        dbHandler = new DBHandler(this);

        // get enum values as a list of strings
        List<String> locationTypesList = Stream.of(Location.LocationType.values())
                .map(Location.LocationType::name)
                .collect(Collectors.toList());
        ArrayAdapter<String> autocompleteAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationTypesList);
        AutoCompleteTextView editTextLocationType = findViewById(R.id.edit_text_location_type);
        editTextLocationType.setAdapter(autocompleteAdapter);
        editTextLocationType.setThreshold(0);

        editTextLocationType.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editTextLocationType.showDropDown();
                hideKeyboard();
            }
        });
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
        getMenuInflater().inflate(R.menu.activity_edit_locations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (addLocationToDb()) {
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.db_error_insert, "location"), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_cancel:
                //todo confirm cancel
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = findViewById(R.id.root_layout);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean addLocationToDb() {
        boolean isValid = true;
        Location newLocation = new Location();

        TextInputLayout inputLayoutName = findViewById(R.id.input_layout_location_name);
        TextInputEditText editTextName = findViewById(R.id.edit_text_location_name);

        TextInputLayout inputLayoutType = findViewById(R.id.input_layout_location_type);
        AutoCompleteTextView editTextType= findViewById(R.id.edit_text_location_type);

        TextInputLayout inputLayoutAddr1 = findViewById(R.id.input_layout_addr_1);
        TextInputEditText editTextAddr1= findViewById(R.id.edit_text_addr_1);

        TextInputEditText editTextAddr2= findViewById(R.id.edit_text_addr_2);

        TextInputEditText editTextCity= findViewById(R.id.edit_text_city);

        TextInputLayout inputLayoutPostcode = findViewById(R.id.input_layout_postcode);
        TextInputEditText editTextPostcode= findViewById(R.id.edit_text_postcode);

        TextInputLayout inputLayoutCountry = findViewById(R.id.input_layout_country);
        TextInputEditText editTextCountry= findViewById(R.id.edit_text_country);

        TextInputLayout inputLayoutEmail = findViewById(R.id.input_layout_email);
        TextInputEditText editTextEmail= findViewById(R.id.edit_text_email);

        TextInputEditText editTextPhone= findViewById(R.id.edit_text_phone);

        //todo check for adding duplicate name
        if (TextUtils.isEmpty(editTextName.getText())) {
            inputLayoutName.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutName.setError(null);
        }
        if (editTextType.getText().length() == 0) {
            inputLayoutType.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutType.setError(null);
        }
        if (TextUtils.isEmpty(editTextAddr1.getText())) {
            inputLayoutAddr1.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutAddr1.setError(null);
        }
        if (TextUtils.isEmpty(editTextPostcode.getText())) {
            inputLayoutPostcode.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutPostcode.setError(null);
        }
        if (TextUtils.isEmpty(editTextCountry.getText())) {
            inputLayoutCountry.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutCountry.setError(null);
        }
        // check for valid email address
        // possibly need null check first?
        if (!TextUtils.isEmpty(editTextEmail.getText()) && !Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText()).matches()) {
            inputLayoutEmail.setError(getString(R.string.input_error_email_invalid));
            isValid = false;
        } else {
            inputLayoutEmail.setError(null);
        }

        if (isValid) {
            newLocation.setLocationName(editTextName.getText().toString());
            newLocation.setLocationType(Location.LocationType.parseLocationType(editTextType.getText().toString()));
            newLocation.setAddrLine1(editTextAddr1.getText().toString());
            newLocation.setAddrLine2(editTextAddr2.getText() == null ? "" : editTextAddr2.getText().toString());
            newLocation.setCity(editTextCity.getText() == null ? "" : editTextCity.getText().toString());
            newLocation.setPostcode(editTextPostcode.getText().toString());
            newLocation.setCountry(editTextCountry.getText().toString());
            newLocation.setEmail(editTextEmail.getText() == null ? "" : editTextEmail.getText().toString());
            newLocation.setPhone(editTextPhone.getText() == null ? "" : editTextPhone.getText().toString());

            return dbHandler.addLocation(newLocation);
        }
        return false;
    }
}
