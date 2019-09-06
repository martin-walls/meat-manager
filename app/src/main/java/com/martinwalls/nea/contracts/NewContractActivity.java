package com.martinwalls.nea.contracts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;
import com.martinwalls.nea.Utils;
import com.martinwalls.nea.components.AddNewTextView;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.*;
import com.martinwalls.nea.ProductsAddedAdapter;
import com.martinwalls.nea.AddNewProductDialog;
import com.martinwalls.nea.stock.EditLocationsActivity;
import com.martinwalls.nea.SearchItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewContractActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener,
        ProductsAddedAdapter.ProductsAddedAdapterListener,
        AddNewProductDialog.AddNewProductListener {

    private DBHandler dbHandler;

    private HashMap<String, Integer> inputViews = new HashMap<>();

    private SearchItemAdapter itemAdapter;
    private List<SearchItem> searchItemList = new ArrayList<>();

    private LinearLayout searchResultsLayout;
    private AddNewTextView addNewView;
    private ViewGroup rootView;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_REPEAT_INTERVAL = "repeatInterval";
    private final String INPUT_REPEAT_ON = "repeatOn";
    private final String INPUT_REMINDER = "reminder";

    private String currentSearchType = INPUT_PRODUCT; // default value

    private int selectedProductId;
    private int selectedDestId;

    private TextView addProductBtn;

    private List<ProductQuantity> productsAddedList = new ArrayList<>();
    private ProductsAddedAdapter productsAddedAdapter;
    private RecyclerView productsAddedRecyclerView;

    private final int REQUEST_REFRESH_ON_DONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contract);
        getSupportActionBar().setTitle(R.string.contracts_title);

        inputViews.put(INPUT_PRODUCT, R.id.input_layout_product);
        inputViews.put(INPUT_QUANTITY, R.id.input_row_quantity);
        inputViews.put(INPUT_DESTINATION, R.id.input_layout_destination);
        inputViews.put(INPUT_REPEAT_INTERVAL, R.id.input_layout_repeat_interval);
        inputViews.put(INPUT_REPEAT_ON, R.id.input_layout_repeat_on);
        inputViews.put(INPUT_REMINDER, R.id.input_layout_reminder);

        addNewView = findViewById(R.id.add_new);
        addNewView.setOnClickListener(v -> addNewItem());

        rootView = findViewById(R.id.root_layout);

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

        setListeners(INPUT_DESTINATION,
                findViewById(R.id.input_layout_destination),
                findViewById(R.id.edit_text_destination));

        productsAddedAdapter = new ProductsAddedAdapter(productsAddedList, this);
        productsAddedRecyclerView = findViewById(R.id.products_added_recycler_view);
        productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addProductBtn = findViewById(R.id.add_product);
        addProductBtn.setOnClickListener(v -> addProduct());
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
                    Toast.makeText(this, getString(R.string.db_error_insert, "order"), Toast.LENGTH_SHORT).show();
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

    private void addProduct() {
        ProductQuantity product = getProductFromInputsAndClear();
        if (product != null) {
            productsAddedList.add(product);
            productsAddedAdapter.notifyItemInserted(productsAddedList.size());
        }
    }

    @Override
    public void deleteProductAdded(int position) {
        productsAddedList.remove(position);
        productsAddedAdapter.notifyItemRemoved(position);
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

    private void openSearch(String inputName) {
        Transition moveTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_open);
        TransitionManager.beginDelayedTransition(rootView, moveTransition);
        for (Integer view : inputViews.values()) {
            if (!view.equals(inputViews.get(inputName))) {
                findViewById(view).setVisibility(View.GONE);
            }
        }
        addProductBtn.setVisibility(View.GONE);
        productsAddedRecyclerView.setVisibility(View.GONE);

        searchItemList.clear();
        switch (inputName) {
            case INPUT_PRODUCT:
                for (Product product : Utils.mergeSort(dbHandler.getAllProducts(), Product.comparatorAlpha())) {
                    searchItemList.add(new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case INPUT_DESTINATION:
                for (Location location : Utils.mergeSort(dbHandler.getAllLocations(Location.LocationType.Destination),
                        Location.comparatorAlpha())) {
                    searchItemList.add(new SearchItem(location.getLocationName(), location.getLocationId()));
                }
                break;
        }
        currentSearchType = inputName;
        itemAdapter.setSearchItemType(currentSearchType);
        itemAdapter.notifyDataSetChanged();

        // filter already in case some text is already entered
        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        itemAdapter.getFilter().filter(editText.getText());

        addNewView.setVisibility(View.VISIBLE);
        addNewView.setText(getString(R.string.search_add_new, inputName));
        addNewView.setSearchItemType(inputName);

        searchResultsLayout.setAlpha(0f);
        searchResultsLayout.setVisibility(View.VISIBLE);

        searchResultsLayout.animate()
                .alpha(1f)
                .setStartDelay(150)
                .setDuration(200) //todo refactor to resource value
                .setListener(null);
    }

    private void cancelSearch() {
        Transition closeTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_close);
        TransitionManager.beginDelayedTransition(rootView, closeTransition);
        for (int view : inputViews.values()) {
            View viewToShow = findViewById(view);
            viewToShow.setVisibility(View.VISIBLE);
        }
        addProductBtn.setVisibility(View.VISIBLE);
        productsAddedRecyclerView.setVisibility(View.VISIBLE);

        searchResultsLayout.setVisibility(View.GONE);

        hideKeyboard();
    }

    private void addNewItem() {
        String searchItemType = addNewView.getSearchItemType();
        switch (searchItemType) {
            case INPUT_PRODUCT:
                DialogFragment dialog = new AddNewProductDialog();
                dialog.show(getSupportFragmentManager(), "add_new_product");
                break;
            case INPUT_DESTINATION:
                Intent newDestIntent = new Intent(this, EditLocationsActivity.class);
                newDestIntent.putExtra(EditLocationsActivity.EXTRA_LOCATION_TYPE,
                        Location.LocationType.Destination.name());
                startActivityForResult(newDestIntent, REQUEST_REFRESH_ON_DONE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            cancelSearch();
            openSearch(currentSearchType);
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

        TextInputEditText editTextRepeatOn = findViewById(R.id.edit_text_repeat_on);
        TextInputLayout inputLayoutRepeatOn = findViewById(R.id.input_layout_repeat_on);

        TextInputEditText editTextReminder = findViewById(R.id.edit_text_reminder);
        TextInputLayout inputLayoutReminder = findViewById(R.id.input_layout_reminder);

        //todo finish add contract to db
    }
}
