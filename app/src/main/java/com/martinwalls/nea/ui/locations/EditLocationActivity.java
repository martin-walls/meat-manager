package com.martinwalls.nea.ui.locations;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Location;
import com.martinwalls.nea.ui.misc.dialog.ConfirmCancelDialog;

import java.util.stream.Collectors;

public class EditLocationActivity extends AppCompatActivity
        implements ConfirmCancelDialog.ConfirmCancelListener {

    public static final String EXTRA_LOCATION_ID = "location_id";

    private DBHandler dbHandler;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_locations);


        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int locationId = extras.getInt(EXTRA_LOCATION_ID);
            location = dbHandler.getLocation(locationId);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.location_edit_title, location.getLocationType().name().toLowerCase()));
        }

        // hide location type spinner, this can't be edited
        findViewById(R.id.row_location_type).setVisibility(View.GONE);

        fillFields();
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
                if (updateLocationInDb()) {
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.db_error_update, "location"),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_cancel:
                if (haveFieldsChanged()) {
                    showConfirmCancelDialog();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the user confirms the cancel action from {@link ConfirmCancelDialog}.
     * Ends the Activity.
     */
    @Override
    public void onConfirmCancelYesAction() {
        finish();
    }

    /**
     * Shows a {@link ConfirmCancelDialog} asking the user to confirm
     * the cancel action.
     */
    private void showConfirmCancelDialog() {
        DialogFragment dialog = new ConfirmCancelDialog();
        dialog.show(getSupportFragmentManager(), "confirm_cancel");
    }

    /**
     * Initialises input fields with data from the {@link Location} being edited.
     */
    private void fillFields() {
        TextInputEditText editTextName = findViewById(R.id.edit_text_location_name);
        editTextName.setText(location.getLocationName());

        TextInputEditText editTextAddr1= findViewById(R.id.edit_text_addr_1);
        editTextAddr1.setText(location.getAddrLine1());

        TextInputEditText editTextAddr2= findViewById(R.id.edit_text_addr_2);
        editTextAddr2.setText(location.getAddrLine2());

        TextInputEditText editTextCity= findViewById(R.id.edit_text_city);
        editTextCity.setText(location.getCity());

        TextInputEditText editTextPostcode= findViewById(R.id.edit_text_postcode);
        editTextPostcode.setText(location.getPostcode());

        TextInputEditText editTextCountry= findViewById(R.id.edit_text_country);
        editTextCountry.setText(location.getCountry());

        TextInputEditText editTextEmail= findViewById(R.id.edit_text_email);
        editTextEmail.setText(location.getEmail());

        TextInputEditText editTextPhone= findViewById(R.id.edit_text_phone);
        editTextPhone.setText(location.getPhone());
    }

    /**
     * Checks whether the user has edited any of the input fields, which
     * determines whether to show a {@link ConfirmCancelDialog} when the
     * user wants to go back.
     */
    private boolean haveFieldsChanged() {
        TextInputEditText editTextName = findViewById(R.id.edit_text_location_name);
        TextInputEditText editTextAddr1= findViewById(R.id.edit_text_addr_1);
        TextInputEditText editTextAddr2= findViewById(R.id.edit_text_addr_2);
        TextInputEditText editTextCity= findViewById(R.id.edit_text_city);
        TextInputEditText editTextPostcode= findViewById(R.id.edit_text_postcode);
        TextInputEditText editTextCountry= findViewById(R.id.edit_text_country);
        TextInputEditText editTextEmail= findViewById(R.id.edit_text_email);
        TextInputEditText editTextPhone= findViewById(R.id.edit_text_phone);

        return !TextUtils.equals(editTextName.getText(), location.getLocationName())
                || !TextUtils.equals(editTextAddr1.getText(), location.getAddrLine1())
                || !TextUtils.equals(editTextAddr2.getText(), location.getAddrLine2())
                || !TextUtils.equals(editTextCity.getText(), location.getCity())
                || !TextUtils.equals(editTextPostcode.getText(), location.getPostcode())
                || !TextUtils.equals(editTextCountry.getText(), location.getCountry())
                || !TextUtils.equals(editTextEmail.getText(), location.getEmail())
                || !TextUtils.equals(editTextPhone.getText(), location.getPhone());
    }

    /**
     * Stores the {@link Location} in the database if it is valid.
     * First checks each input field to check it is valid. It a field is
     * invalid, an appropriate error message is shown to help the user correct
     * the error. If all fields have valid data, edits the existing location
     * in the database to the new values.
     */
    private boolean updateLocationInDb() {
        boolean isValid = true;

        TextInputEditText editTextName = findViewById(R.id.edit_text_location_name);
        TextInputLayout inputLayoutName = findViewById(R.id.input_layout_location_name);

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
                .contains(editTextName.getText().toString())
                && !TextUtils.equals(editTextName.getText(), location.getLocationName())) {
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
            location.setLocationName(editTextName.getText().toString());
            location.setAddrLine1(editTextAddr1.getText().toString());
            location.setAddrLine2(editTextAddr2.getText() == null ? "" : editTextAddr2.getText().toString());
            location.setCity(editTextCity.getText() == null ? "" : editTextCity.getText().toString());
            location.setPostcode(editTextPostcode.getText().toString());
            location.setCountry(editTextCountry.getText().toString());
            location.setEmail(editTextEmail.getText() == null ? "" : editTextEmail.getText().toString());
            location.setPhone(editTextPhone.getText() == null ? "" : editTextPhone.getText().toString());

            return dbHandler.updateLocation(location);
        }
        return false;
    }
}
