package com.martinwalls.nea.contracts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.AddNewProductDialog;
import com.martinwalls.nea.ConfirmCancelDialog;
import com.martinwalls.nea.InputFormActivity;
import com.martinwalls.nea.ProductsAddedAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.SearchItemAdapter;
import com.martinwalls.nea.util.SimpleTextWatcher;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Contract;
import com.martinwalls.nea.models.Interval;
import com.martinwalls.nea.models.Location;
import com.martinwalls.nea.models.Product;
import com.martinwalls.nea.models.ProductQuantity;
import com.martinwalls.nea.models.SearchItem;
import com.martinwalls.nea.stock.NewLocationActivity;
import com.martinwalls.nea.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class NewContractActivity extends InputFormActivity
        implements ProductsAddedAdapter.ProductsAddedAdapterListener,
        AddNewProductDialog.AddNewProductListener,
        RepeatIntervalDialog.RepeatIntervalDialogListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    private final int REQUEST_REFRESH_ON_DONE = 1;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_REPEAT_INTERVAL = "repeat_interval";
    private final String INPUT_REPEAT_ON = "repeat_on";
    private final String INPUT_REMINDER = "reminder";

    private DBHandler dbHandler;

    private ProductsAddedAdapter productsAddedAdapter;
    private List<ProductQuantity> productsAddedList = new ArrayList<>();
    private RecyclerView productsAddedRecyclerView;

    private TextView addProductBtn;

    // store ids/values of selected items
    private int selectedProductId;
    private int selectedDestId;
    private Interval selectedRepeatInterval;

    private ArrayAdapter<CharSequence> repeatOnSpnAdapter;

    private boolean isWeek = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contract);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.contracts_title);
        }

        dbHandler = new DBHandler(this);

        addViewToHide(INPUT_PRODUCT, R.id.input_layout_product);
        addViewToHide(INPUT_QUANTITY, R.id.input_row_quantity);
        addViewToHide(INPUT_DESTINATION, R.id.input_layout_destination);
        addViewToHide(INPUT_REPEAT_INTERVAL, R.id.input_layout_repeat_interval);
        addViewToHide(INPUT_REPEAT_ON, R.id.input_repeat_on);
        addViewToHide(INPUT_REMINDER, R.id.input_reminder);

        addViewToHide("add_product_btn", R.id.add_product);
        addViewToHide("products_added_recycler_view", R.id.products_added_recycler_view);

        setAddNewView(R.id.add_new);

        setRootView(R.id.root_layout);

        setCurrentSearchType(INPUT_PRODUCT);

        setSearchItemAdapter(new SearchItemAdapter(getSearchItemList(), getCurrentSearchType(), this));
        TextView emptyView = findViewById(R.id.no_results);
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(getSearchItemAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSearchResultsLayout(R.id.search_results_layout);

        setListeners(INPUT_PRODUCT,
                findViewById(R.id.input_layout_product),
                findViewById(R.id.edit_text_product));

        setListeners(INPUT_DESTINATION,
                findViewById(R.id.input_layout_destination),
                findViewById(R.id.edit_text_destination));

        productsAddedAdapter = new ProductsAddedAdapter(productsAddedList, this);
        productsAddedRecyclerView = findViewById(R.id.products_added_recycler_view);
        productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addProductBtn = findViewById(R.id.add_product);
        addProductBtn.setOnClickListener(v -> addProduct());

        TextInputEditText editTextRepeatInterval = findViewById(R.id.edit_text_repeat_interval);
        editTextRepeatInterval.setOnClickListener(v -> {
            DialogFragment dialog = new RepeatIntervalDialog();
            Bundle args = new Bundle();
            if (selectedRepeatInterval != null) {
                if (selectedRepeatInterval.getValue() == 1
                        && selectedRepeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED, RepeatIntervalDialog.OPTION_WEEK);
                } else if (selectedRepeatInterval.getValue() == 2
                        && selectedRepeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED, RepeatIntervalDialog.OPTION_TWO_WEEK);
                } else if (selectedRepeatInterval.getValue() == 1
                        && selectedRepeatInterval.getUnit() == Interval.TimeUnit.MONTH) {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED, RepeatIntervalDialog.OPTION_MONTH);
                } else {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED, RepeatIntervalDialog.OPTION_CUSTOM);
                    args.putInt(RepeatIntervalDialog.EXTRA_TIME_VALUE, selectedRepeatInterval.getValue());
                    args.putString(RepeatIntervalDialog.EXTRA_TIME_UNIT, selectedRepeatInterval.getUnit().name());
                }
            }
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "repeat_interval");
        });

        Spinner repeatOnSpn = findViewById(R.id.spn_repeat_on);
        repeatOnSpnAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        repeatOnSpnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatOnSpnAdapter.addAll(getResources().getStringArray(R.array.weekdays));
        repeatOnSpnAdapter.notifyDataSetChanged();
        repeatOnSpn.setAdapter(repeatOnSpnAdapter);

        EditText editTextReminder = findViewById(R.id.edit_text_reminder);
        TextView txtReminderDaysBefore = findViewById(R.id.reminder_text_days_before);
        TextView txtReminder = findViewById(R.id.text_reminder);
        editTextReminder.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || s.toString().equals("0")) {
                    txtReminder.setText(R.string.contracts_reminder_off);
                } else {
                    txtReminder.setText(R.string.contracts_reminder);
                }
                if (!TextUtils.isEmpty(s)) {
                    txtReminderDaysBefore.setText(getResources().getQuantityString(
                            R.plurals.contracts_reminder_days_before, Integer.parseInt(s.toString())));
                }
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
        getMenuInflater().inflate(R.menu.activity_new_contract, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (addContractToDb()) {
                    finish();
                } else {
                    Toast.makeText(this,
                            getString(R.string.db_error_insert, "order"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            cancelSearch();
            openSearch(getCurrentSearchType());
        }
    }

    @Override
    protected void loadSearchItems(String searchType) {
        super.loadSearchItems(searchType);
        switch (searchType) {
            case INPUT_PRODUCT:
                for (Product product : Utils.mergeSort(dbHandler.getAllProducts(), Product.comparatorAlpha())) {
                    addSearchItemToList(new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case INPUT_DESTINATION:
                for (Location location : Utils.mergeSort(dbHandler.getAllLocations(Location.LocationType.Destination),
                        Location.comparatorAlpha())) {
                    addSearchItemToList(new SearchItem(location.getLocationName(), location.getLocationId()));
                }
                break;
        }
    }

    @Override
    public void onSearchItemSelected(SearchItem item, String searchItemType) {
        switch (searchItemType) {
            case INPUT_PRODUCT:
                selectedProductId = item.getId();
                break;
            case INPUT_DESTINATION:
                selectedDestId = item.getId();
                break;
        }

        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        editText.setText(item.getName());
        editText.clearFocus();
        cancelSearch();
    }

    @Override
    public void onAddNewProductDoneAction(Product newProduct) {
        boolean successful = dbHandler.addProduct(newProduct);
        if (successful) {
            // refresh list
            cancelSearch();
            openSearch(getCurrentSearchType());
        }
    }

    @Override
    public void onProductAddedDelete(int position) {
        productsAddedList.remove(position);
        productsAddedAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onRadioBtnClicked(int id) {
        switch (id) {
            case RepeatIntervalDialog.OPTION_WEEK:
                setSelectedRepeatInterval(new Interval(1, Interval.TimeUnit.WEEK));
                break;
            case RepeatIntervalDialog.OPTION_TWO_WEEK:
                setSelectedRepeatInterval(new Interval(2, Interval.TimeUnit.WEEK));
                break;
            case RepeatIntervalDialog.OPTION_MONTH:
                setSelectedRepeatInterval(new Interval(1, Interval.TimeUnit.MONTH));
                break;
        }
    }

    @Override
    public void onCustomIntervalSelected(Interval interval) {
        setSelectedRepeatInterval(interval);
    }

    @Override
    public void onConfirmCancelYesAction() {
        finish();
    }

    @Override
    protected void addNewItemFromSearch(String searchType) {
        switch (searchType) {
            case INPUT_PRODUCT:
                DialogFragment dialog = new AddNewProductDialog();
                dialog.show(getSupportFragmentManager(), "add_new_product");
                break;
            case INPUT_DESTINATION:
                Intent newDestIntent = new Intent(this, NewLocationActivity.class);
                newDestIntent.putExtra(NewLocationActivity.EXTRA_LOCATION_TYPE,
                        Location.LocationType.Destination.name());
                startActivityForResult(newDestIntent, REQUEST_REFRESH_ON_DONE);
                break;
        }
    }

    private void showConfirmCancelDialog() {
        DialogFragment dialog = new ConfirmCancelDialog();
        dialog.show(getSupportFragmentManager(), "confirm_cancel");
    }

    private void addProduct() {
        ProductQuantity product = getProductFromInputsAndClear();
        if (product != null) {
            productsAddedList.add(product);
            productsAddedAdapter.notifyItemInserted(productsAddedList.size());
        }
    }

    private ProductQuantity getProductFromInputsAndClear() {
        hideKeyboard();

        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        TextInputLayout inputLayoutProduct = findViewById(R.id.input_layout_product);

        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputLayout inputLayoutMass = findViewById(R.id.input_layout_quantity_mass);

        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);

        boolean isValid = true;

        if (TextUtils.isEmpty(editTextProduct.getText())) {
            inputLayoutProduct.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutProduct.setError(null);
        }
        for (ProductQuantity productQuantity : productsAddedList) {
            if (productQuantity.getProduct().getProductName().equals(editTextProduct.getText().toString())) {
                inputLayoutProduct.setError(getString(R.string.input_error_duplicate));
                isValid = false;
                break;
            }
        }
        if (TextUtils.isEmpty(editTextMass.getText())) {
            inputLayoutMass.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutMass.setError(null);
        }

        if (isValid) {
            ProductQuantity productQuantity = new ProductQuantity(dbHandler.getProduct(selectedProductId),
                    Double.parseDouble(editTextMass.getText().toString()),
                    TextUtils.isEmpty(editTextNumBoxes.getText()) ? -1 :
                            Integer.parseInt(editTextNumBoxes.getText().toString()));
            editTextProduct.setText("");
            editTextProduct.clearFocus();
            editTextMass.setText("");
            editTextMass.clearFocus();
            editTextNumBoxes.setText("");
            editTextNumBoxes.clearFocus();
            return productQuantity;
        } else {
            return null;
        }
    }

    private void setSelectedRepeatInterval(Interval interval) {
        // repeat on input is hidden until repeat interval is set
        LinearLayout inputRepeatOn = findViewById(R.id.input_repeat_on);
        inputRepeatOn.setVisibility(View.VISIBLE);

        selectedRepeatInterval = interval;
        TextInputEditText editTextRepeatInterval = findViewById(R.id.edit_text_repeat_interval);
        if (interval.getValue() == 1) {
            editTextRepeatInterval.setText(getString(R.string.contracts_repeat_interval_display_one,
                    interval.getUnit().name().toLowerCase()));
        } else {
            editTextRepeatInterval.setText(getString(R.string.contracts_repeat_interval_display_multiple,
                    interval.getValue(), interval.getUnit().name().toLowerCase()));
        }
        TextView repeatOnTxt = findViewById(R.id.text_repeat_on);
        if (!isWeek && interval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnSpnAdapter.clear();
            repeatOnSpnAdapter.addAll(getResources().getStringArray(R.array.weekdays));
            repeatOnSpnAdapter.notifyDataSetChanged();
            repeatOnTxt.setText(R.string.contracts_repeat_on_week);
            isWeek = true;
        } else if (isWeek && interval.getUnit() == Interval.TimeUnit.MONTH) {
            repeatOnSpnAdapter.clear();
            for (int i = 1; i <= 31; i++) {
                repeatOnSpnAdapter.add("Day " + i);
            }
            repeatOnSpnAdapter.notifyDataSetChanged();
            repeatOnTxt.setText(R.string.contracts_repeat_on_month);
            isWeek = false;
        }
    }

    private boolean addContractToDb() {
        boolean isValid = true;
        Contract newContract = new Contract();

        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        TextInputLayout inputLayoutProduct = findViewById(R.id.input_layout_product);

        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputLayout inputLayoutMass = findViewById(R.id.input_layout_quantity_mass);

        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        TextInputLayout inputLayoutDest = findViewById(R.id.input_layout_destination);

        TextInputEditText editTextRepeatInterval = findViewById(R.id.edit_text_repeat_interval);
        TextInputLayout inputLayoutRepeatInterval = findViewById(R.id.input_layout_repeat_interval);

        Spinner repeatOnSpn = findViewById(R.id.spn_repeat_on);

        EditText editTextReminder = findViewById(R.id.edit_text_reminder);

        if (productsAddedList.size() == 0 && TextUtils.isEmpty(editTextProduct.getText())) {
            inputLayoutProduct.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutProduct.setError(null);
        }
        if (productsAddedList.size() == 0 && TextUtils.isEmpty(editTextMass.getText())) {
            inputLayoutMass.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutMass.setError(null);
        }
        if (TextUtils.isEmpty(editTextDest.getText())) {
            inputLayoutDest.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutDest.setError(null);
        }
        if (TextUtils.isEmpty(editTextRepeatInterval.getText())) {
            inputLayoutRepeatInterval.setError(getString(R.string.input_error_blank_date));
            isValid = false;
        } else {
            inputLayoutRepeatInterval.setError(null);
        }

        if (isValid) {
            if (!TextUtils.isEmpty(editTextProduct.getText())) {
                productsAddedList.add(new ProductQuantity(dbHandler.getProduct(selectedProductId),
                        Double.parseDouble(editTextMass.getText().toString()),
                        TextUtils.isEmpty(editTextNumBoxes.getText()) ? -1
                                : Integer.parseInt(editTextNumBoxes.getText().toString())));
                newContract.setProductList(productsAddedList);
                newContract.setDest(dbHandler.getLocation(selectedDestId));
                newContract.setRepeatInterval(selectedRepeatInterval);
                newContract.setRepeatOn(repeatOnSpn.getSelectedItemPosition());
                if (TextUtils.isEmpty(editTextReminder.getText())
                        || editTextReminder.getText().toString().equals("0")) {
                    newContract.setReminder(-1);
                } else {
                    newContract.setReminder(Integer.parseInt(editTextReminder.getText().toString()));
                }

                return dbHandler.addContract(newContract);
            }
        }
        return false;
    }

    private boolean areAllFieldsEmpty() {
        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);
        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        TextInputEditText editTextRepeatInterval = findViewById(R.id.edit_text_repeat_interval);

        return TextUtils.isEmpty(editTextProduct.getText())
                && TextUtils.isEmpty(editTextMass.getText())
                && TextUtils.isEmpty(editTextNumBoxes.getText())
                && TextUtils.isEmpty(editTextDest.getText())
                && TextUtils.isEmpty(editTextRepeatInterval.getText());
    }
}
