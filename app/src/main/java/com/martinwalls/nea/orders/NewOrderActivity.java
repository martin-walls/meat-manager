package com.martinwalls.nea.orders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Location;
import com.martinwalls.nea.models.Order;
import com.martinwalls.nea.models.Product;
import com.martinwalls.nea.models.ProductQuantity;
import com.martinwalls.nea.models.SearchItem;
import com.martinwalls.nea.stock.NewLocationActivity;
import com.martinwalls.nea.util.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewOrderActivity extends InputFormActivity
        implements AddNewProductDialog.AddNewProductListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        ProductsAddedAdapter.ProductsAddedAdapterListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    private final int REQUEST_REFRESH_ON_DONE = 1;
    private final String DATE_FORMAT = "dd MMMM yyyy, HH:mm"; //todo move to Utils?

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_DATE = "date";

    private DBHandler dbHandler;

    private ProductsAddedAdapter productsAddedAdapter;
    private List<ProductQuantity> productsAddedList = new ArrayList<>();
    private RecyclerView productsAddedRecyclerView;

    private TextView addProductBtn;

    // store ids/values of selected items
    private int selectedProductId;
    private int selectedDestId;
    private LocalDate selectedDate;
    private LocalDateTime selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.orders_new_title);
        }

        addViewToHide(INPUT_PRODUCT, R.id.input_layout_product);
        addViewToHide(INPUT_QUANTITY, R.id.input_row_quantity);
        addViewToHide(INPUT_DESTINATION, R.id.input_layout_destination);
        addViewToHide(INPUT_DATE, R.id.input_layout_date);

        addViewToHide("add_product_btn", R.id.add_product);
        addViewToHide("products_added_recycler_view", R.id.products_added_recycler_view);

        setAddNewView(R.id.add_new);

        setRootView(R.id.root_layout);

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
    public void deleteProductAdded(int position) {
        productsAddedList.remove(position);
        productsAddedAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // (month + 1) adjusts for DatePicker returning month in range 0-11, LocalDate uses 1-12
        selectedDate = LocalDate.of(year, month + 1, day);
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
        if (TextUtils.isEmpty(editTextDate.getText())) {
            inputLayoutDate.setError(getString(R.string.input_error_blank_date));
            isValid = false;
        } else {
            inputLayoutDate.setError(null);
        }

        if (isValid) {
            List<ProductQuantity> productList = new ArrayList<>();
            if (!TextUtils.isEmpty(editTextProduct.getText())) {
                productList.add(new ProductQuantity(dbHandler.getProduct(selectedProductId),
                        Double.parseDouble(editTextMass.getText().toString()),
                        TextUtils.isEmpty(editTextNumBoxes.getText()) ? -1
                                : Integer.parseInt(editTextNumBoxes.getText().toString())));
            }
            productList.addAll(productsAddedList);
            newOrder.setProductList(productList);
            newOrder.setDest(dbHandler.getLocation(selectedDestId));
            newOrder.setOrderDate(LocalDateTime.parse(editTextDate.getText().toString(),
                    DateTimeFormatter.ofPattern(DATE_FORMAT)));
            newOrder.setCompleted(false);

            return dbHandler.addOrder(newOrder);
        }
        return false;
    }

    private boolean areAllFieldsEmpty() {
        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        TextInputEditText editTextMass = findViewById(R.id.edit_text_quantity_mass);
        TextInputEditText editTextNumBoxes = findViewById(R.id.edit_text_quantity_boxes);
        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        TextInputEditText editTextDate = findViewById(R.id.edit_text_date);

        return TextUtils.isEmpty(editTextProduct.getText())
                && TextUtils.isEmpty(editTextMass.getText())
                && TextUtils.isEmpty(editTextNumBoxes.getText())
                && TextUtils.isEmpty(editTextDest.getText())
                && TextUtils.isEmpty(editTextDate.getText());
    }
}
