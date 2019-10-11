package com.martinwalls.nea.stock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.AddNewProductDialog;
import com.martinwalls.nea.ConfirmCancelDialog;
import com.martinwalls.nea.R;
import com.martinwalls.nea.SearchItemAdapter;
import com.martinwalls.nea.SimpleTextWatcher;
import com.martinwalls.nea.components.AddNewTextView;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Location;
import com.martinwalls.nea.models.Product;
import com.martinwalls.nea.models.SearchItem;
import com.martinwalls.nea.models.StockItem;
import com.martinwalls.nea.util.Utils;
import com.martinwalls.nea.util.undo.AddStockAction;
import com.martinwalls.nea.util.undo.UndoStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class NewStockActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener,
        AddNewProductDialog.AddNewProductListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    private final int REQUEST_REFRESH_ON_DONE = 1;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_SUPPLIER = "supplier";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_LOCATION = "location";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_QUALITY = "quality";

    private String currentSearchType = INPUT_PRODUCT; // default value so it's not null

    private DBHandler dbHandler;

    private HashMap<String, Integer> inputViews = new HashMap<>();

    private SearchItemAdapter itemAdapter;
    private ArrayList<SearchItem> searchItemList = new ArrayList<>();

    private LinearLayout searchResultsLayout;
    private AddNewTextView addNewView;
    private ViewGroup rootView;

    // store ids of selected items
    private int selectedProductId;
    private int selectedSupplierId;
    private int selectedLocationId;
    private int selectedDestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        getSupportActionBar().setTitle(R.string.stock_new);

        dbHandler = new DBHandler(this);

        // store reference to each row to show/hide later
        inputViews.put(INPUT_PRODUCT, R.id.input_layout_product);
        inputViews.put(INPUT_SUPPLIER, R.id.input_layout_supplier);
        inputViews.put(INPUT_QUANTITY, R.id.input_row_quantity);
        inputViews.put(INPUT_LOCATION, R.id.input_layout_location);
        inputViews.put(INPUT_DESTINATION, R.id.input_layout_destination);
        inputViews.put(INPUT_QUALITY, R.id.input_layout_quality);

        addNewView = findViewById(R.id.add_new);
        addNewView.setOnClickListener(v -> addNewItem());

        rootView = findViewById(R.id.root_layout);

        // setup recycler view
        itemAdapter = new SearchItemAdapter(searchItemList, currentSearchType, this);
        TextView emptyView = findViewById(R.id.no_results);
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsLayout = findViewById(R.id.search_results_layout);


        setListeners(INPUT_PRODUCT,
                findViewById(R.id.input_layout_product),
                findViewById(R.id.edit_text_product));

        setListeners(INPUT_SUPPLIER,
                findViewById(R.id.input_layout_supplier),
                findViewById(R.id.edit_text_supplier));

        setListeners(INPUT_LOCATION,
                findViewById(R.id.input_layout_location),
                findViewById(R.id.edit_text_location));

        setListeners(INPUT_DESTINATION,
                findViewById(R.id.input_layout_destination),
                findViewById(R.id.edit_text_destination));

        setListeners(INPUT_QUALITY,
                findViewById(R.id.input_layout_quality),
                findViewById(R.id.edit_text_quality));
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
        getMenuInflater().inflate(R.menu.activity_new_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (addStockToDb()) {
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.db_error_insert, "stock"), Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            cancelSearch();
            openSearch(currentSearchType);
        }
    }

    @Override
    public void onSearchItemSelected(SearchItem item, String searchItemType) {
        switch (searchItemType) {
            case INPUT_PRODUCT:
                selectedProductId = item.getId();
                break;
            case INPUT_SUPPLIER:
                selectedSupplierId = item.getId();
                break;
            case INPUT_LOCATION:
                selectedLocationId = item.getId();
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
            openSearch(currentSearchType);
        }
    }

    @Override
    public void onConfirmCancelYesAction() {
        finish();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = findViewById(R.id.root_layout);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showConfirmCancelDialog() {
        DialogFragment dialog = new ConfirmCancelDialog();
        dialog.show(getSupportFragmentManager(), "confirm_cancel");
    }

    private void setListeners(final String name, final TextInputLayout inputLayout, final TextInputEditText editText) {
        inputLayout.setEndIconVisible(false);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                inputLayout.setEndIconVisible(true);
                openSearch(name);
            }
        });

        inputLayout.setEndIconOnClickListener(v -> {
                editText.setText("");
                editText.clearFocus();
                inputLayout.setEndIconVisible(false);
                cancelSearch();
        });

        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.getFilter().filter(s);
            }
        });
    }

    private void openSearch(String rowName) {
        Transition moveTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_open);
        TransitionManager.beginDelayedTransition(rootView, moveTransition);
        for (Integer view : inputViews.values()) {
            if (!view.equals(inputViews.get(rowName))) {
                findViewById(view).setVisibility(View.GONE);
            }
        }

        searchItemList.clear();
        switch (rowName) {
            case INPUT_PRODUCT:
                for (Product product : Utils.mergeSort(dbHandler.getAllProducts(), Product.comparatorAlpha())) {
                    searchItemList.add(new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case INPUT_SUPPLIER:
                for (Location location : Utils.mergeSort(dbHandler.getAllLocations(Location.LocationType.Supplier), Location.comparatorAlpha())) {
                    searchItemList.add(new SearchItem(location.getLocationName(), location.getLocationId()));
                }
                break;
            case INPUT_DESTINATION:
                for (Location location : Utils.mergeSort(dbHandler.getAllLocations(Location.LocationType.Destination), Location.comparatorAlpha())) {
                    searchItemList.add(new SearchItem(location.getLocationName(), location.getLocationId()));
                }
                break;
            case INPUT_LOCATION:
                for (Location location : Utils.mergeSort(dbHandler.getAllLocations(Location.LocationType.Storage), Location.comparatorAlpha())) {
                    searchItemList.add(new SearchItem(location.getLocationName(), location.getLocationId()));
                }
                break;
            case INPUT_QUALITY:
                int i = 0;
                for (String quality : StockItem.Quality.getQualityStrings()) {
                    searchItemList.add(new SearchItem(quality, i));
                    i++;
                }
                break;
        }
        currentSearchType = rowName;
        itemAdapter.setSearchItemType(currentSearchType);
        itemAdapter.notifyDataSetChanged();

        // filter data at start for when text is already entered
        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        itemAdapter.getFilter().filter(editText.getText());

        addNewView.setVisibility(rowName.equals(INPUT_QUALITY) ? View.GONE : View.VISIBLE);
        addNewView.setText(getString(R.string.search_add_new, rowName));
        addNewView.setSearchItemType(rowName);

        searchResultsLayout.setAlpha(0f);
        searchResultsLayout.setVisibility(View.VISIBLE);

        searchResultsLayout.animate()
                .alpha(1f)
                .setStartDelay(getResources().getInteger(R.integer.search_results_fade_delay))
                .setDuration(getResources().getInteger(R.integer.search_results_fade_duration))
                .setListener(null);
    }

    private void cancelSearch() {
        Transition closeTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_close);
        TransitionManager.beginDelayedTransition(rootView, closeTransition);
        for (int view : inputViews.values()) {
            View viewToShow = findViewById(view);
            viewToShow.setVisibility(View.VISIBLE);
        }

        searchResultsLayout.setVisibility(View.GONE);

        searchItemList.clear();
        itemAdapter.notifyDataSetChanged();

        hideKeyboard();
    }

    private void addNewItem() {
        String searchItemType = addNewView.getSearchItemType();
        switch (searchItemType) {
            case INPUT_PRODUCT:
                DialogFragment dialog = new AddNewProductDialog();
                dialog.show(getSupportFragmentManager(), "add_new_product");
                break;
            case INPUT_SUPPLIER:
                Intent newSupplierIntent = new Intent(this, EditLocationsActivity.class);
                newSupplierIntent.putExtra(EditLocationsActivity.EXTRA_LOCATION_TYPE, Location.LocationType.Supplier.name());
                startActivityForResult(newSupplierIntent, REQUEST_REFRESH_ON_DONE);
                break;
            case INPUT_LOCATION:
                Intent newLocationIntent = new Intent(this, EditLocationsActivity.class);
                newLocationIntent.putExtra(EditLocationsActivity.EXTRA_LOCATION_TYPE, Location.LocationType.Storage.name());
                startActivityForResult(newLocationIntent, REQUEST_REFRESH_ON_DONE);
                break;
            case INPUT_DESTINATION:
                Intent newDestIntent = new Intent(this, EditLocationsActivity.class);
                newDestIntent.putExtra(EditLocationsActivity.EXTRA_LOCATION_TYPE, Location.LocationType.Destination.name());
                startActivityForResult(newDestIntent, REQUEST_REFRESH_ON_DONE);
                break;
        }
    }

    private boolean addStockToDb() {
        boolean isValid = true;
        StockItem newStockItem = new StockItem();

        TextInputEditText editProduct = findViewById(R.id.edit_text_product);
        TextInputLayout inputLayoutProduct = findViewById(R.id.input_layout_product);

        TextInputEditText editSupplier = findViewById(R.id.edit_text_supplier);
        TextInputLayout inputLayoutSupplier = findViewById(R.id.input_layout_supplier);

        TextInputEditText editMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputLayout inputLayoutMass = findViewById(R.id.input_layout_quantity_mass);

        TextInputEditText editNumBoxes = findViewById(R.id.edit_text_quantity_boxes);

        TextInputEditText editLocation = findViewById(R.id.edit_text_location);
        TextInputLayout inputLayoutLocation = findViewById(R.id.input_layout_location);

        TextInputEditText editDest = findViewById(R.id.edit_text_destination);

        TextInputEditText editQuality = findViewById(R.id.edit_text_quality);
        TextInputLayout inputLayoutQuality = findViewById(R.id.input_layout_quality);

        if (editProduct.getText().length() == 0) {
            inputLayoutProduct.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutProduct.setError(null);
        }
        if (editSupplier.getText().length() == 0) {
            inputLayoutSupplier.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutSupplier.setError(null);
        }
        if (editMass.getText().length() == 0) {
            inputLayoutMass.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutMass.setError(null);
        }
        if (editLocation.getText().length() == 0) {
            inputLayoutLocation.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutLocation.setError(null);
        }
        if (editQuality.getText().length() == 0) {
            inputLayoutQuality.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutQuality.setError(null);
        }

        if (dbHandler.getAllStock().stream().map(stockItem -> new int[] {stockItem.getProduct().getProductId(), stockItem.getLocationId()})
                .anyMatch(x -> Arrays.equals(x, new int[] {selectedProductId, selectedLocationId}))) {
            inputLayoutLocation.setError(getString(R.string.input_error_stock_already_in_location));
            isValid = false;
        }


        if (isValid) {
            newStockItem.setProduct(dbHandler.getProduct(selectedProductId));
            newStockItem.setSupplier(dbHandler.getLocation(selectedSupplierId));
            newStockItem.setMass(Double.parseDouble(editMass.getText().toString()));
            if (!TextUtils.isEmpty(editNumBoxes.getText())) {
                newStockItem.setNumBoxes(Integer.parseInt(editNumBoxes.getText().toString()));
            } else {
                newStockItem.setNumBoxes(-1);
            }
            newStockItem.setLocation(dbHandler.getLocation(selectedLocationId));
            if (!TextUtils.isEmpty(editDest.getText())) {
                newStockItem.setDest(dbHandler.getLocation(selectedDestId));
            } else {
                newStockItem.setDestId(-1);
            }
            newStockItem.setQuality(StockItem.Quality.parseQuality(editQuality.getText().toString()));


            int newRowId = dbHandler.addStockItem(newStockItem);

            newStockItem.setStockId(newRowId);
            UndoStack.getInstance().push(new AddStockAction(newStockItem));

            return newRowId != -1;
        }

        return false;
    }

    private boolean areAllFieldsEmpty() {
        TextInputEditText editProduct = findViewById(R.id.edit_text_product);
        TextInputEditText editSupplier = findViewById(R.id.edit_text_supplier);
        TextInputEditText editMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputEditText editNumBoxes = findViewById(R.id.edit_text_quantity_boxes);
        TextInputEditText editLocation = findViewById(R.id.edit_text_location);
        TextInputEditText editDest = findViewById(R.id.edit_text_destination);
        TextInputEditText editQuality = findViewById(R.id.edit_text_quality);

        return TextUtils.isEmpty(editProduct.getText())
                && TextUtils.isEmpty(editSupplier.getText())
                && TextUtils.isEmpty(editMass.getText())
                && TextUtils.isEmpty(editNumBoxes.getText())
                && TextUtils.isEmpty(editLocation.getText())
                && TextUtils.isEmpty(editDest.getText())
                && TextUtils.isEmpty(editQuality.getText());
    }
}
