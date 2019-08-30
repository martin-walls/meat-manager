package com.martinwalls.nea.orders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;
import com.martinwalls.nea.SearchItem;
import com.martinwalls.nea.Utils;
import com.martinwalls.nea.components.AddNewTextView;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Location;
import com.martinwalls.nea.models.Order;
import com.martinwalls.nea.models.Product;
import com.martinwalls.nea.models.ProductQuantity;
import com.martinwalls.nea.stock.AddNewProductDialog;
import com.martinwalls.nea.stock.EditLocationsActivity;
import com.martinwalls.nea.stock.SearchItemAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class NewOrderActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener,
        AddNewProductDialog.AddNewProductListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private DBHandler dbHandler;

    private HashMap<String, Integer> inputViews = new HashMap<>();

    private SearchItemAdapter itemAdapter;
    private List<SearchItem> searchItemList = new ArrayList<>();

    private LinearLayout searchResultsLayout;
    private AddNewTextView addNewView;
    private ViewGroup rootView;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_QUANTITY = "quantity";
    private final String BTN_ADD_PRODUCT = "add_product";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_DATE = "date";

    private String currentSearchType = INPUT_PRODUCT; // default initial value

    private int selectedProductId;
    private int selectedDestId;
    private LocalDate selectedDate;
    private LocalDateTime selectedDateTime;

    private List<ProductQuantity> productsAlreadyAddedList = new ArrayList<>();

    private final String DATE_FORMAT = "dd MMMM yyyy, HH:mm";

    private final int REQUEST_REFRESH_ON_DONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        getSupportActionBar().setTitle(R.string.orders_new_title);

        inputViews.put(INPUT_PRODUCT, R.id.input_layout_product);
        inputViews.put(INPUT_QUANTITY, R.id.input_row_quantity);
        inputViews.put(BTN_ADD_PRODUCT, R.id.add_product);
        inputViews.put(INPUT_DESTINATION, R.id.input_layout_destination);
        inputViews.put(INPUT_DATE, R.id.input_layout_date);

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

        TextInputEditText editTextDate = findViewById(R.id.edit_text_date);
        editTextDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        TextView addProductBtn = findViewById(R.id.add_product);
        addProductBtn.setOnClickListener(v -> {
            Toast.makeText(this, "ADD PRODUCT", Toast.LENGTH_SHORT).show();
            addProduct();
            //todo
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
        getMenuInflater().inflate(R.menu.activity_new_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (addOrderToDb()) {
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
            productsAlreadyAddedList.add(product);

            LayoutInflater inflater = LayoutInflater.from(this);
            ViewGroup productsAlreadyAdded = findViewById(R.id.products_already_added);

            View productView = inflater.inflate(R.layout.item_product, productsAlreadyAdded, false);
            TextView productName = productView.findViewById(R.id.product_name);
            productName.setText(product.getProduct().getProductName());
            TextView productMass = productView.findViewById(R.id.mass);
            productMass.setText(String.valueOf(product.getQuantityMass()));
            TextView productNumBoxes = productView.findViewById(R.id.num_boxes);
            if (product.getQuantityBoxes() < 0) {
                productNumBoxes.setVisibility(GONE);
            } else {
                productNumBoxes.setText(String.valueOf(product.getQuantityBoxes()));
            }

            productView.setPadding(Utils.convertDpToPixelSize(8, this),
                    Utils.convertDpToPixelSize(16, this),
                    Utils.convertDpToPixelSize(8, this),
                    Utils.convertDpToPixelSize(16, this));

            productsAlreadyAdded.addView(productView);
        }
    }

    private ProductQuantity getProductFromInputsAndClear() {
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
                findViewById(view).setVisibility(GONE);
            }
        }

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

        searchResultsLayout.setVisibility(GONE);

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        selectedDate = LocalDate.of(year, month, day);
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(this, this, 12, 0, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar now = Calendar.getInstance();
        //noinspection MagicConstant
        if (selectedDate.getYear() == now.get(Calendar.YEAR) && selectedDate.getMonthValue() == now.get(Calendar.MONTH)
                && selectedDate.getDayOfMonth() == now.get(Calendar.DAY_OF_MONTH)
                && (hourOfDay < now.get(Calendar.HOUR_OF_DAY)
                || (hourOfDay == now.get(Calendar.HOUR_OF_DAY) && minute <= now.get(Calendar.MINUTE) + 10))) {
            Toast.makeText(this, R.string.input_error_time_must_be_future, Toast.LENGTH_SHORT)
                    .show();

            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(this, this, 12, 0, true);
            timePickerDialog.show();
        } else {
            selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(hourOfDay, minute));
            String formatDate = selectedDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            TextInputEditText editTextDate = findViewById(R.id.edit_text_date);
            editTextDate.setText(formatDate);
        }
    }

    private boolean addOrderToDb() {
        boolean isValid = true;
        Order newOrder = new Order();

        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        TextInputLayout inputLayoutProduct = findViewById(R.id.input_layout_product);

        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputLayout inputLayoutMass = findViewById(R.id.input_layout_quantity_mass);

        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        TextInputLayout inputLayoutDest = findViewById(R.id.input_layout_destination);

        TextInputEditText editTextDate = findViewById(R.id.edit_text_date);
        TextInputLayout inputLayoutDate = findViewById(R.id.input_layout_date);

        if (productsAlreadyAddedList.size() == 0 && TextUtils.isEmpty(editTextProduct.getText())) {
            inputLayoutProduct.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else {
            inputLayoutProduct.setError(null);
        }
        if (productsAlreadyAddedList.size() == 0 && TextUtils.isEmpty(editTextMass.getText())) {
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
        if (TextUtils.isEmpty(editTextDate.getText())) {
            inputLayoutDate.setError(getString(R.string.input_error_blank_date));
            isValid = false;
        } else {
            inputLayoutDate.setError(null);
        }

        if (isValid) {
            List<ProductQuantity> productList = new ArrayList<>();
            productList.add(new ProductQuantity(dbHandler.getProduct(selectedProductId),
                    Double.parseDouble(editTextMass.getText().toString()),
                    TextUtils.isEmpty(editTextNumBoxes.getText()) ? -1
                            : Integer.parseInt(editTextNumBoxes.getText().toString())));
            productList.addAll(productsAlreadyAddedList);
            newOrder.setProductList(productList);
            newOrder.setDest(dbHandler.getLocation(selectedDestId));
            newOrder.setOrderDate(LocalDateTime.parse(editTextDate.getText().toString(),
                    DateTimeFormatter.ofPattern(DATE_FORMAT)));
            newOrder.setCompleted(false);

            return dbHandler.addOrder(newOrder);
        }
        return false;
    }
}
