package com.martinwalls.nea.stock;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.martinwalls.nea.R;
import com.martinwalls.nea.SampleData;
import com.martinwalls.nea.SearchItem;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.db.models.Product;
import com.martinwalls.nea.db.models.StockItem;
import com.martinwalls.nea.components.AddNewTextView;
import com.martinwalls.nea.components.CustomRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class NewStockActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener,
        AddNewProductDialog.AddNewProductListener,
        AddNewMeatTypeDialog.AddNewMeatTypeListener {

    private DBHandler dbHandler;

    private HashMap<String, Integer> inputViews = new HashMap<>();

    private SearchItemAdapter itemAdapter;
    private ArrayList<SearchItem> searchItemList = new ArrayList<>();

    private LinearLayout searchResultsLayout;
    private AddNewTextView addNewView;
    private ViewGroup rootView;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_SUPPLIER = "supplier";
    private final String INPUT_LOCATION = "location";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_QUALITY = "quality";
    //todo refactor to enum

    private String currentSearchType = INPUT_PRODUCT; // default value so it's not null

    //todo refactor to class
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
        //todo make this an enum??
        inputViews.put("product", R.id.input_layout_product);
        inputViews.put("supplier", R.id.input_layout_supplier);
        inputViews.put("quantity", R.id.input_row_quantity);
        inputViews.put("location", R.id.input_layout_location);
        inputViews.put("destination", R.id.input_layout_destination);
        inputViews.put("quality", R.id.input_layout_quality);

        addNewView = findViewById(R.id.add_new);
        addNewView.setOnClickListener(v -> addNewItem((AddNewTextView) v));

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
                (TextInputLayout) findViewById(R.id.input_layout_product),
                (TextInputEditText) findViewById(R.id.edit_text_product));

        setListeners(INPUT_SUPPLIER,
                (TextInputLayout) findViewById(R.id.input_layout_supplier),
                (TextInputEditText) findViewById(R.id.edit_text_supplier));

        setListeners(INPUT_LOCATION,
                (TextInputLayout) findViewById(R.id.input_layout_location),
                (TextInputEditText) findViewById(R.id.edit_text_location));

        setListeners(INPUT_DESTINATION,
                (TextInputLayout) findViewById(R.id.input_layout_destination),
                (TextInputEditText) findViewById(R.id.edit_text_destination));

        setListeners(INPUT_QUALITY,
                (TextInputLayout) findViewById(R.id.input_layout_quality),
                (TextInputEditText) findViewById(R.id.edit_text_quality));
    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        dbHandler.close();
//    }

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
                //todo confirm cancel
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
        int i;
        switch (rowName) {
            case INPUT_PRODUCT:
                for (Product product : dbHandler.getAllProducts()) {
                    searchItemList.add(new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case INPUT_SUPPLIER:
            case INPUT_DESTINATION:
            case INPUT_LOCATION:
                i = 0;
                for (String location : SampleData.getSampleLocations()) {
                    searchItemList.add(new SearchItem(location, i));
                    i++;
                }
                break;
            case INPUT_QUALITY:
                i = 0;
                for (String quality : SampleData.getSampleQualities()) {
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
                .setStartDelay(150)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
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

    private void addNewItem(AddNewTextView textView) {
        String searchItemType = textView.getSearchItemType();
        DialogFragment dialog = new AddNewProductDialog();
        switch (searchItemType) {
            case INPUT_PRODUCT:
                dialog.show(getSupportFragmentManager(), "add_new_product");
                break;
            default:
                //todo implement the rest of the "add new" dialogs
                Toast.makeText(this, searchItemType, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = findViewById(R.id.root_layout);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onItemSelected(SearchItem item, String searchItemType) {
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
        boolean completed = dbHandler.addProduct(newProduct);
        Toast.makeText(this, completed ? "true" : "false", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddNewMeatTypeDoneAction(String meatType) {
        dbHandler.addMeatType(meatType);
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

        if (isValid) {
            newStockItem.setProduct(dbHandler.getProduct(selectedProductId));
            newStockItem.setSupplierId(selectedSupplierId);
            newStockItem.setMass(Double.parseDouble(editMass.getText().toString()));
            if (editNumBoxes.getText().length() != 0) {
                newStockItem.setNumBoxes(Integer.parseInt(editNumBoxes.getText().toString()));
            }
            newStockItem.setLocationId(selectedLocationId);
            if (editDest.getText().length() != 0) {
                newStockItem.setDestId(selectedDestId);
            }
            newStockItem.setQuality(StockItem.Quality.parseQuality(editQuality.getText().toString()));

            return dbHandler.addStockItem(newStockItem);
        }

        return false;
        //todo error check in db for same stock item
    }
}
