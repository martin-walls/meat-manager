package com.martinwalls.nea.ui.locations;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Location;
import com.martinwalls.nea.ui.misc.dialog.ConfirmCancelDialog;

import java.util.stream.Collectors;

public class NewLocationActivity extends AppCompatActivity
        implements ConfirmCancelDialog.ConfirmCancelListener {

    public static final String EXTRA_LOCATION_TYPE = "location_type";

    private DBHandler dbHandler;

    private ArrayAdapter<CharSequence> locationTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_locations);

        String locationType = getString(R.string.locations_type_storage);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            locationType = extras.getString(EXTRA_LOCATION_TYPE, locationType);
        }

        getSupportActionBar().setTitle(getString(R.string.locations_add_new_title, locationType.toLowerCase()));

        dbHandler = new DBHandler(this);

        // location type spinner
        Spinner locationTypeSpn = findViewById(R.id.spn_location_type);
        locationTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        locationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationTypeAdapter.addAll(Location.LocationType.getLocationTypeStrings());
        locationTypeAdapter.notifyDataSetChanged();
        locationTypeSpn.setAdapter(locationTypeAdapter);

        locationTypeSpn.setSelection(Location.LocationType.valueOf(locationType).ordinal());
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
        getMenuInflater().inflate(R.menu.activity_edit_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (addLocationToDb()) {
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.db_error_insert, "location"),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_cancel:
                if (!areAllFieldsEmpty()) {
                    showConfirmCancelDialog();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //doc needed
    @Override
    public void onConfirmCancelYesAction() {
        finish();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = findViewById(R.id.root_layout);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showConfirmCancelDialog() {
        DialogFragment dialog = new ConfirmCancelDialog();
        dialog.show(getSupportFragmentManager(), "confirm_cancel");
    }

    private boolean addLocationToDb() {
        boolean isValid = true;
        Location newLocation = new Location();

        TextInputEditText editTextName = findViewById(R.id.edit_text_location_name);
        TextInputLayout inputLayoutName = findViewById(R.id.input_layout_location_name);

        Spinner locationTypeSpn = findViewById(R.id.spn_location_type);

        TextInputEditText editTextAddr1= findViewById(R.id.edit_text_addr_1);
        TextInputLayout inputLayoutAddr1 = findViewById(R.id.input_layout_addr_1);

        TextInputEditText editTextAddr2= findViewById(R.id.edit_text_addr_2);

        TextInputEditText editTextCity= findViewById(R.id.edit_text_city);

        TextInputEditText editTextPostcode= findViewById(R.id.edit_text_postcode);
        TextInputLayout inputLayoutPostcode = findViewById(R.id.input_layout_postcode);

        TextInputEditText editTextCountry= findViewById(R.id.edit_text_country);
        TextInputLayout inputLayoutCountry = findViewById(R.id.input_layout_country);

        TextInputEditText editTextEmail= findViewById(R.id.edit_text_email);
        TextInputLayout inputLayoutEmail = findViewById(R.id.input_layout_email);

        TextInputEditText editTextPhone= findViewById(R.id.edit_text_phone);

        if (TextUtils.isEmpty(editTextName.getText())) {
            inputLayoutName.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else if (dbHandler.getAllLocations().stream().map(Location::getLocationName).collect(Collectors.toList())
                .contains(editTextName.getText().toString())) {
            inputLayoutName.setError(getString(R.string.input_error_duplicate));
            isValid = false;
        } else {
            inputLayoutName.setError(null);
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
        if (!TextUtils.isEmpty(editTextEmail.getText())
                && !Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText()).matches()) {
            inputLayoutEmail.setError(getString(R.string.input_error_invalid_email));
            isValid = false;
        } else {
            inputLayoutEmail.setError(null);
        }

        if (isValid) {
            newLocation.setLocationName(editTextName.getText().toString());
            newLocation.setLocationType(Location.LocationType.values()[locationTypeSpn.getSelectedItemPosition()]);
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

    private boolean areAllFieldsEmpty() {
        TextInputEditText editTextName = findViewById(R.id.edit_text_location_name);
        TextInputEditText editTextAddr1= findViewById(R.id.edit_text_addr_1);
        TextInputEditText editTextAddr2= findViewById(R.id.edit_text_addr_2);
        TextInputEditText editTextCity= findViewById(R.id.edit_text_city);
        TextInputEditText editTextPostcode= findViewById(R.id.edit_text_postcode);
        TextInputEditText editTextCountry= findViewById(R.id.edit_text_country);
        TextInputEditText editTextEmail= findViewById(R.id.edit_text_email);
        TextInputEditText editTextPhone= findViewById(R.id.edit_text_phone);

        return TextUtils.isEmpty(editTextName.getText())
                && TextUtils.isEmpty(editTextAddr1.getText())
                && TextUtils.isEmpty(editTextAddr2.getText())
                && TextUtils.isEmpty(editTextCity.getText())
                && TextUtils.isEmpty(editTextPostcode.getText())
                && TextUtils.isEmpty(editTextCountry.getText())
                && TextUtils.isEmpty(editTextEmail.getText())
                && TextUtils.isEmpty(editTextPhone.getText());
    }
}
