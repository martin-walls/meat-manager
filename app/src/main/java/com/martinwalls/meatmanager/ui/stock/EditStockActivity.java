package com.martinwalls.meatmanager.ui.stock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.data.models.SearchItem;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.ui.InputFormActivity;
import com.martinwalls.meatmanager.ui.SearchItemAdapter;
import com.martinwalls.meatmanager.ui.locations.NewLocationActivity;
import com.martinwalls.meatmanager.ui.misc.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.misc.dialog.ConfirmCancelDialog;
import com.martinwalls.meatmanager.ui.products.AddNewProductDialog;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.Utils;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.stock.AddStockAction;
import com.martinwalls.meatmanager.util.undo.stock.EditStockAction;

import java.util.Arrays;

public class EditStockActivity extends InputFormActivity
        implements AddNewProductDialog.AddNewProductListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    public static final String EXTRA_EDIT_TYPE = "edit_type";
    public static final int EDIT_TYPE_NEW = 0;
    public static final int EDIT_TYPE_EDIT = 1;
    public static final String EXTRA_STOCK_ID = "stock_id";

    private final int REQUEST_REFRESH_ON_DONE = 0;

    private final int REQUEST_NEW_SUPPLIER = 1;
    private final int REQUEST_NEW_STORAGE = 2;
    private final int REQUEST_NEW_DESTINATION = 3;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_SUPPLIER = "supplier";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_STORAGE = "location";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_QUALITY = "quality";

    private int editType = EDIT_TYPE_NEW;

    private DBHandler dbHandler;
    private StockItem stockToEdit;

    // store ids of selected items
    private int selectedProductId;
    private int selectedSupplierId;
    private int selectedStorageId;
    private int selectedDestId;

    private boolean hasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stock);

        dbHandler = new DBHandler(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editType = extras.getInt(EXTRA_EDIT_TYPE, EDIT_TYPE_NEW);
            if (editType == EDIT_TYPE_EDIT) {
                int stockId = extras.getInt(EXTRA_STOCK_ID);
                stockToEdit = dbHandler.getStockItem(stockId);
            }
        }

        getSupportActionBar().setTitle(editType == EDIT_TYPE_NEW
                ? R.string.stock_new_title
                : R.string.stock_edit_title);

        initViews();

        if (editType == EDIT_TYPE_EDIT) {
            findViewById(R.id.text_product).setVisibility(View.VISIBLE);
            findViewById(R.id.input_layout_product).setVisibility(View.GONE);
            fillFields();
        } else {
            findViewById(R.id.text_product).setVisibility(View.GONE);
            findViewById(R.id.input_layout_product).setVisibility(View.VISIBLE);
        }

        // listen for any changes made by the user
        setTextChangedListeners();
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
        getMenuInflater().inflate(R.menu.activity_edit_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                onDoneAction();
                return true;
            case R.id.action_cancel:
                onCancelAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int newLocationId = data.getIntExtra(NewLocationActivity.RESULT_ID, -1);
            Location newLocation = dbHandler.getLocation(newLocationId);
            switch (requestCode) {
                case REQUEST_NEW_SUPPLIER:
                    selectedSupplierId = newLocationId;
                    TextInputEditText editTextSupplier = findViewById(R.id.edit_text_supplier);
                    editTextSupplier.setText(newLocation.getLocationName());
                    editTextSupplier.clearFocus();
                    break;
                case REQUEST_NEW_STORAGE:
                    selectedStorageId = newLocationId;
                    TextInputEditText editTextStorage = findViewById(R.id.edit_text_location);
                    editTextStorage.setText(newLocation.getLocationName());
                    editTextStorage.clearFocus();
                    break;
                case REQUEST_NEW_DESTINATION:
                    selectedDestId = newLocationId;
                    TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
                    editTextDest.setText(newLocation.getLocationName());
                    editTextDest.clearFocus();
                    break;
            }
            cancelSearch();
        }
    }

    @Override
    protected void openSearch(String inputName) {
        super.openSearch(inputName);
        if (inputName.equals(INPUT_QUALITY)) {
            hideAddNewView();
        }
    }

    @Override
    protected void loadSearchItems(String searchType) {
        super.loadSearchItems(searchType);
        switch (searchType) {
            case INPUT_PRODUCT:
                for (Product product : SortUtils.mergeSort(
                        dbHandler.getAllProducts(), Product.comparatorAlpha())) {
                    addSearchItemToList(
                            new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case INPUT_SUPPLIER:
                for (Location location : SortUtils.mergeSort(
                        dbHandler.getAllLocations(Location.LocationType.Supplier),
                        Location.comparatorAlpha())) {
                    addSearchItemToList(
                            new SearchItem(
                                    location.getLocationName(), location.getLocationId()));
                }
                break;
            case INPUT_DESTINATION:
                for (Location location : SortUtils.mergeSort(
                        dbHandler.getAllLocations(Location.LocationType.Destination),
                        Location.comparatorAlpha())) {
                    addSearchItemToList(
                            new SearchItem(
                                    location.getLocationName(), location.getLocationId()));
                }
                break;
            case INPUT_STORAGE:
                for (Location location : SortUtils.mergeSort(
                        dbHandler.getAllLocations(Location.LocationType.Storage),
                        Location.comparatorAlpha())) {
                    addSearchItemToList(
                            new SearchItem(
                                    location.getLocationName(), location.getLocationId()));
                }
                break;
            case INPUT_QUALITY:
                int i = 0;
                for (String quality : StockItem.Quality.getQualityStrings()) {
                    addSearchItemToList(new SearchItem(quality, i));
                    i++;
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
            case INPUT_SUPPLIER:
                selectedSupplierId = item.getId();
                break;
            case INPUT_STORAGE:
                selectedStorageId = item.getId();
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
            selectedProductId = newProduct.getProductId();
            TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
            editTextProduct.setText(newProduct.getProductName());
            editTextProduct.clearFocus();
            cancelSearch();
        }
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
            case INPUT_SUPPLIER:
                Intent newSupplierIntent = new Intent(this, NewLocationActivity.class);
                newSupplierIntent.putExtra(NewLocationActivity.EXTRA_LOCATION_TYPE,
                        Location.LocationType.Supplier.name());
                startActivityForResult(newSupplierIntent, REQUEST_NEW_SUPPLIER);
                break;
            case INPUT_STORAGE:
                Intent newLocationIntent = new Intent(this, NewLocationActivity.class);
                newLocationIntent.putExtra(NewLocationActivity.EXTRA_LOCATION_TYPE,
                        Location.LocationType.Storage.name());
                startActivityForResult(newLocationIntent, REQUEST_NEW_STORAGE);
                break;
            case INPUT_DESTINATION:
                Intent newDestIntent = new Intent(this, NewLocationActivity.class);
                newDestIntent.putExtra(NewLocationActivity.EXTRA_LOCATION_TYPE,
                        Location.LocationType.Destination.name());
                startActivityForResult(newDestIntent, REQUEST_NEW_DESTINATION);
                break;
        }
    }

    /**
     * Initialises all the views in the layout, including any on click listeners
     * and other behaviours.
     */
    private void initViews() {
        setViewsToHide();

        setAddNewView(R.id.add_new);
        setRootView(R.id.root_layout);
        setCurrentSearchType(INPUT_PRODUCT);

        checkMassUnit();

        initSearchLayout();
        initSearchFieldListeners();
    }

    /**
     * Retrieves the current mass unit set by the user preferences, and updates
     * the input fields accordingly.
     */
    private void checkMassUnit() {
        if (MassUnit.getMassUnit(this) == MassUnit.LBS) {
            TextInputLayout inputLayoutQuantityMass =
                    findViewById(R.id.input_layout_quantity_mass);
            inputLayoutQuantityMass.setHint(getString(R.string.stock_input_quantity_lbs));
        }
    }

    /**
     * Sets which views should be hidden when a search view is opened.
     */
    private void setViewsToHide() {
        addViewToHide(INPUT_PRODUCT, R.id.input_layout_product);
        addViewToHide(INPUT_SUPPLIER, R.id.input_layout_supplier);
        addViewToHide(INPUT_QUANTITY, R.id.input_row_quantity);
        addViewToHide(INPUT_STORAGE, R.id.input_layout_location);
        addViewToHide(INPUT_DESTINATION, R.id.input_layout_destination);
        addViewToHide(INPUT_QUALITY, R.id.input_layout_quality);
    }

    /**
     * Initialises the search layout which is shown when the user clicks on a
     * relevant input field. These are set in {@link #initSearchFieldListeners()}.
     */
    private void initSearchLayout() {
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        TextView emptyView = findViewById(R.id.no_results);
        recyclerView.setEmptyView(emptyView);
        setSearchItemAdapter(
                new SearchItemAdapter(getSearchItemList(), getCurrentSearchType(), this));
        recyclerView.setAdapter(getSearchItemAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSearchResultsLayout(R.id.search_results_layout);
    }

    /**
     * Sets listeners for the input fields that should open the search layout
     * when clicked. This should include any field that references other objects
     * in the database that the user should choose from.
     */
    private void initSearchFieldListeners() {
        setListeners(INPUT_PRODUCT,
                findViewById(R.id.input_layout_product),
                findViewById(R.id.edit_text_product));

        setListeners(INPUT_SUPPLIER,
                findViewById(R.id.input_layout_supplier),
                findViewById(R.id.edit_text_supplier));

        setListeners(INPUT_STORAGE,
                findViewById(R.id.input_layout_location),
                findViewById(R.id.edit_text_location));

        setListeners(INPUT_DESTINATION,
                findViewById(R.id.input_layout_destination),
                findViewById(R.id.edit_text_destination));

        setListeners(INPUT_QUALITY,
                findViewById(R.id.input_layout_quality),
                findViewById(R.id.edit_text_quality));
    }

    /**
     * Sets listeners on input fields to detect when the user has made any
     * changes, determining whether or not to show call {@link #showConfirmCancelDialog()}
     * when the user wants to go back.
     */
    private void setTextChangedListeners() {
        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        editTextProduct.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        TextInputEditText editTextSupplier = findViewById(R.id.edit_text_supplier);
        editTextSupplier.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        editTextMass.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);
        editTextNumBoxes.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        TextInputEditText editTextLocation = findViewById(R.id.edit_text_location);
        editTextLocation.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        editTextDest.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        TextInputEditText editTextQuality = findViewById(R.id.edit_text_quality);
        editTextQuality.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });
    }

    /**
     * Initialises input fields with data from the {@link StockItem} being edited.
     */
    private void fillFields() {
        TextView textProduct = findViewById(R.id.text_product);
        textProduct.setText(stockToEdit.getProduct().getProductName());

        TextInputEditText editTextSupplier = findViewById(R.id.edit_text_supplier);
        editTextSupplier.setText(stockToEdit.getSupplierName());

        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        editTextMass.setText(Utils.getMassDisplayValue(this, stockToEdit.getMass(), 4));

        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);
        if (stockToEdit.getNumBoxes() != -1) {
            editTextNumBoxes.setText(String.valueOf(stockToEdit.getNumBoxes()));
        }

        TextInputEditText editTextLocation = findViewById(R.id.edit_text_location);
        editTextLocation.setText(stockToEdit.getLocationName());

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        editTextDest.setText(stockToEdit.getDestName());

        TextInputEditText editTextQuality = findViewById(R.id.edit_text_quality);
        editTextQuality.setText(stockToEdit.getQuality().getDisplayName());

        selectedProductId = stockToEdit.getProduct().getProductId();
        selectedSupplierId = stockToEdit.getSupplierId();
        selectedStorageId = stockToEdit.getLocationId();
        selectedDestId = stockToEdit.getDestId();
    }

    /**
     * This is called when the user clicks the done button in the action bar.
     * Tries to add the stock to the database, but if there are validation
     * errors shows an error message to the user.
     */
    private void onDoneAction() {
        if (addStockToDb()) {
            finish();
        } else {
            Toast.makeText(this,
                    getString(R.string.db_error_insert, "stock"), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * This is called when the user clicks the cancel button. Checks to see if
     * the user has made any changes, and shows a {@link ConfirmCancelDialog}
     * if this is the case.
     */
    private void onCancelAction() {
        if (hasChanged) {
            showConfirmCancelDialog();
        } else {
            finish();
        }
    }

    /**
     * Shows a {@link ConfirmCancelDialog} asking the user to confirm the
     * cancel action.
     */
    private void showConfirmCancelDialog() {
        DialogFragment dialog = new ConfirmCancelDialog();
        dialog.show(getSupportFragmentManager(), "confirm_cancel");
    }

    /**
     * Stores the {@link StockItem} in the database if it is valid.
     * First checks each input field to check it is valid. If a field is
     * invalid, an appropriate error message is shown to help the user 
     * correct the error. If all the fields have valid data, either adds a new
     * stock item or edits the existing one depending on the edit mode of this
     * activity.
     */
    private boolean addStockToDb() {
        boolean isValid = true;
        StockItem newStockItem = new StockItem();

        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        TextInputLayout inputLayoutProduct = findViewById(R.id.input_layout_product);

        TextInputEditText editTextSupplier = findViewById(R.id.edit_text_supplier);
        TextInputLayout inputLayoutSupplier = findViewById(R.id.input_layout_supplier);

        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputLayout inputLayoutMass = findViewById(R.id.input_layout_quantity_mass);

        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);

        TextInputEditText editTextLocation = findViewById(R.id.edit_text_location);
        TextInputLayout inputLayoutLocation = findViewById(R.id.input_layout_location);

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);

        TextInputEditText editTextQuality = findViewById(R.id.edit_text_quality);
        TextInputLayout inputLayoutQuality = findViewById(R.id.input_layout_quality);

        if (editType != EDIT_TYPE_EDIT) {
            if (editTextProduct.getText().length() == 0) {
                inputLayoutProduct.setError(getString(R.string.input_error_blank));
                isValid = false;
            } else {
                inputLayoutProduct.setError(null);
            }
        }
        if (editTextSupplier.getText().length() == 0) {
            inputLayoutSupplier.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutSupplier.setError(null);
        }
        if (editTextMass.getText().length() == 0) {
            inputLayoutMass.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutMass.setError(null);
        }
        if (editTextLocation.getText().length() == 0) {
            inputLayoutLocation.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutLocation.setError(null);
        }
        if (editTextQuality.getText().length() == 0) {
            inputLayoutQuality.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutQuality.setError(null);
        }

        // if new stock item or location has changed
        if (editType == EDIT_TYPE_NEW
                || (editType == EDIT_TYPE_EDIT
                    && selectedStorageId != stockToEdit.getLocationId())) {
            if (dbHandler.getAllStock().stream()
                    .map(stockItem -> new int[]{stockItem.getProduct().getProductId(),
                            stockItem.getSupplierId(), stockItem.getLocationId()})
                    .anyMatch(x -> Arrays.equals(x, new int[]{selectedProductId,
                            selectedSupplierId, selectedStorageId}))) {
                inputLayoutLocation.setError(
                        getString(R.string.input_error_stock_already_in_location));
                isValid = false;
            }
        }


        if (isValid) {
            newStockItem.setProduct(dbHandler.getProduct(selectedProductId));
            newStockItem.setSupplier(dbHandler.getLocation(selectedSupplierId));
            newStockItem.setMass(Utils.getKgsFromCurrentMassUnit(this,
                    Double.parseDouble(editTextMass.getText().toString())));
            if (!TextUtils.isEmpty(editTextNumBoxes.getText())) {
                newStockItem.setNumBoxes(
                        Integer.parseInt(editTextNumBoxes.getText().toString()));
            } else {
                newStockItem.setNumBoxes(-1);
            }
            newStockItem.setLocation(dbHandler.getLocation(selectedStorageId));
            if (!TextUtils.isEmpty(editTextDest.getText())) {
                newStockItem.setDest(dbHandler.getLocation(selectedDestId));
            } else {
                newStockItem.setDestId(-1);
            }
            newStockItem.setQuality(
                    StockItem.Quality.parseQuality(editTextQuality.getText().toString()));

            if (editType == EDIT_TYPE_NEW) {
                int newRowId = dbHandler.addStockItem(newStockItem);
                newStockItem.setStockId(newRowId);
                UndoStack.getInstance().push(new AddStockAction(newStockItem));
                return newRowId != -1;
            } else {
                newStockItem.setStockId(stockToEdit.getStockId());
                boolean success = dbHandler.updateStockItem(newStockItem);
                UndoStack.getInstance().push(new EditStockAction(stockToEdit, newStockItem));
                return success;
            }
        }
        return false;
    }
}
