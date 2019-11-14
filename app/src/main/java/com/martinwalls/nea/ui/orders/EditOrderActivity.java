package com.martinwalls.nea.ui.orders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.ui.products.AddNewProductDialog;
import com.martinwalls.nea.ui.misc.dialog.ConfirmCancelDialog;
import com.martinwalls.nea.ui.InputFormActivity;
import com.martinwalls.nea.ui.ProductsAddedAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.ui.SearchItemAdapter;
import com.martinwalls.nea.ui.misc.CustomRecyclerView;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Location;
import com.martinwalls.nea.data.models.Order;
import com.martinwalls.nea.data.models.Product;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.data.models.SearchItem;
import com.martinwalls.nea.ui.locations.NewLocationActivity;
import com.martinwalls.nea.util.MassUnit;
import com.martinwalls.nea.util.SimpleTextWatcher;
import com.martinwalls.nea.util.SortUtils;
import com.martinwalls.nea.util.Utils;
import com.martinwalls.nea.util.undo.orders.AddOrderAction;
import com.martinwalls.nea.util.undo.orders.EditOrderAction;
import com.martinwalls.nea.util.undo.UndoStack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditOrderActivity extends InputFormActivity
        implements AddNewProductDialog.AddNewProductListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        ProductsAddedAdapter.ProductsAddedAdapterListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    public static final String EXTRA_EDIT_TYPE = "edit_type";
    public static final int EDIT_TYPE_NEW = 0;
    public static final int EDIT_TYPE_EDIT = 1;
    public static final String EXTRA_ORDER_ID = "order_id";

    private final int REQUEST_REFRESH_ON_DONE = 1;
    private final String DATE_FORMAT = "dd MMMM yyyy, HH:mm";

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_DATE = "date";

    private int editType = EDIT_TYPE_NEW;

    private DBHandler dbHandler;
    private Order orderToEdit;

    private ProductsAddedAdapter productsAddedAdapter;
    private List<ProductQuantity> productsAddedList = new ArrayList<>();

    private TextView addProductBtn;

    // store ids/values of selected items
    private int selectedProductId;
    private int selectedDestId;
    private LocalDate selectedDate;
    private LocalDateTime selectedDateTime;

    private boolean hasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editType = extras.getInt(EXTRA_EDIT_TYPE, EDIT_TYPE_NEW);
            if (editType == EDIT_TYPE_EDIT) {
                int orderId = extras.getInt(EXTRA_ORDER_ID);
                orderToEdit = dbHandler.getOrder(orderId);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(editType == EDIT_TYPE_NEW
                    ? R.string.orders_new_title
                    : R.string.order_edit_title);
        }

        addViewToHide(INPUT_PRODUCT, R.id.input_layout_product);
        addViewToHide(INPUT_QUANTITY, R.id.input_row_quantity);
        addViewToHide(INPUT_DESTINATION, R.id.input_layout_destination);
        addViewToHide(INPUT_DATE, R.id.input_layout_date);

        addViewToHide("add_product_btn", R.id.add_product);
        addViewToHide("products_added_recycler_view", R.id.products_added_recycler_view);
        if (editType == EDIT_TYPE_EDIT) {
            addViewToHide("completed_checkbox", R.id.row_completed);
        }

        setAddNewView(R.id.add_new);
        setRootView(R.id.root_layout);

        setCurrentSearchType(INPUT_PRODUCT);

        setSearchItemAdapter(
                new SearchItemAdapter(getSearchItemList(), getCurrentSearchType(), this));
        TextView emptyView = findViewById(R.id.no_results);
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(getSearchItemAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSearchResultsLayout(R.id.search_results_layout);

        if (MassUnit.getMassUnit(this) == MassUnit.LBS) {
            TextInputLayout inputLayoutQuantityMass =
                    findViewById(R.id.input_layout_quantity_mass);
            inputLayoutQuantityMass.setHint(getString(R.string.orders_input_quantity_lbs));
        }

        setListeners(INPUT_PRODUCT,
                findViewById(R.id.input_layout_product),
                findViewById(R.id.edit_text_product));

        setListeners(INPUT_DESTINATION,
                findViewById(R.id.input_layout_destination),
                findViewById(R.id.edit_text_destination));

        productsAddedAdapter =
                new ProductsAddedAdapter(productsAddedList, this,
                        //todo edit button for new product as well as edit
                        editType == EDIT_TYPE_EDIT, true);
        RecyclerView productsAddedRecyclerView =
                findViewById(R.id.products_added_recycler_view);
        productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextInputEditText editTextDate = findViewById(R.id.edit_text_date);
        editTextDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(this, this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        addProductBtn = findViewById(R.id.add_product);
        addProductBtn.setOnClickListener(v -> addProductToProductsAddedList());

        if (editType == EDIT_TYPE_NEW) {
            findViewById(R.id.checkbox_completed).setVisibility(View.GONE);
            findViewById(R.id.product_btn_done).setVisibility(View.GONE);
        } else {
            findViewById(R.id.product_inputs).setVisibility(View.GONE);

            fillFields();

            ImageButton productBtnDone = findViewById(R.id.product_btn_done);
            productBtnDone.setOnClickListener(v -> {
                addProductToProductsAddedList();
                findViewById(R.id.product_inputs).setVisibility(View.GONE);
            });
        }

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
                    Toast.makeText(this,
                            getString(R.string.db_error_insert, "order"), Toast.LENGTH_SHORT)
                            .show();
                }
                return true;
            case R.id.action_cancel:
                if (hasChanged) {
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
                for (Product product : SortUtils.mergeSort(
                        dbHandler.getAllProducts(), Product.comparatorAlpha())) {
                    addSearchItemToList(
                            new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case INPUT_DESTINATION:
                for (Location location : SortUtils.mergeSort(
                        dbHandler.getAllLocations(Location.LocationType.Destination),
                        Location.comparatorAlpha())) {
                    addSearchItemToList(new SearchItem(
                            location.getLocationName(), location.getLocationId()));
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

    /**
     * Removes the product added to the order at the specified position.
     */
    @Override
    public void onProductAddedDelete(int position) {
        productsAddedList.remove(position);
        productsAddedAdapter.notifyItemRemoved(position);
    }

    /**
     * Fills the product and mass input fields with the values stored in the
     * product added at the specified position. This allows the user to edit
     * the product again if they want to change which product it is or how much
     * of it they want to add to the order.
     */
    @Override
    public void onProductAddedEdit(int position) {
        ProductQuantity productAdded = productsAddedList.get(position);

        findViewById(R.id.product_inputs).setVisibility(View.VISIBLE);

        TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
        editTextProduct.setText(productAdded.getProduct().getProductName());

        TextInputEditText editTextQuantityMass = findViewById(R.id.edit_text_quantity_mass);
        editTextQuantityMass.setText(
                Utils.getMassDisplayValue(this, productAdded.getQuantityMass(), 4));

        if (productAdded.getQuantityBoxes() != -1) {
            TextInputEditText editTextQuantityBoxes =
                    findViewById(R.id.edit_text_quantity_boxes);
            editTextQuantityBoxes.setText(String.valueOf(productAdded.getQuantityBoxes()));
        }

        productsAddedList.remove(position);
        productsAddedAdapter.notifyItemRemoved(position);

        selectedProductId = productAdded.getProduct().getProductId();
    }

    /**
     * This is called when the {@link DatePickerDialog} returns, when the user
     * has chosen a date for the order. Stores the chosen date and shows a
     * {@link TimePickerDialog} so the user can set a time for the order.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // (month + 1) adjusts for DatePicker returning month in range 0-11,
        // LocalDate uses 1-12
        selectedDate = LocalDate.of(year, month + 1, day);
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(this, this, 12, 0, true);
        timePickerDialog.show();
    }

    /**
     * This is called then the {@link TimePickerDialog} returns, when the user
     * has chosen a time for the order. Stores the chosen time, and combines
     * this with the chosen date to set the date and time for the order.
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 10);
        Calendar orderDateTime = Calendar.getInstance();
        orderDateTime.set(
                selectedDate.getYear(),
                selectedDate.getMonthValue() - 1,
                selectedDate.getDayOfMonth(),
                hourOfDay, minute);
        if (
//                selectedDate.getYear() == now.get(Calendar.YEAR)
//                && selectedDate.getMonthValue() == now.get(Calendar.MONTH)
//                && selectedDate.getDayOfMonth() == now.get(Calendar.DAY_OF_MONTH)
//                && (hourOfDay < now.get(Calendar.HOUR_OF_DAY)
//                || (hourOfDay == now.get(Calendar.HOUR_OF_DAY)
//                && minute <= now.get(Calendar.MINUTE) + 10))
                orderDateTime.before(now) //testme
        ) {
            Toast.makeText(this, R.string.input_error_time_must_be_future, Toast.LENGTH_SHORT)
                    .show();

            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(this, this, 12, 0, true);
            timePickerDialog.show();
        } else {
            selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(hourOfDay, minute));
            String formatDate = selectedDateTime.format(
                    DateTimeFormatter.ofPattern(DATE_FORMAT));
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

    /**
     * Shows a {@link ConfirmCancelDialog} asking the user to confirm the
     * cancel action.
     */
    private void showConfirmCancelDialog() {
        DialogFragment dialog = new ConfirmCancelDialog();
        dialog.show(getSupportFragmentManager(), "confirm_cancel");
    }

    /**
     * Gets the product and mass currently entered into the input fields
     * and adds a {@link ProductQuantity} with these values to the contract.
     * Clears the input fields so another product can be added.
     */
    private void addProductToProductsAddedList() {
        ProductQuantity product = getProductFromInputsAndClear();
        if (product != null) {
            productsAddedList.add(product);
            productsAddedAdapter.notifyItemInserted(productsAddedList.size());
        }
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

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        editTextDest.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        TextInputEditText editTextDate = findViewById(R.id.edit_text_date);
        editTextDate.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        CheckBox checkboxCompleted = findViewById(R.id.checkbox_completed);
        checkboxCompleted.setOnCheckedChangeListener(
                (buttonView, isChecked) -> hasChanged = true);
    }

    /**
     * Gets the product and mass currently entered into the input fields and
     * returns this as a {@link ProductQuantity} object. Clears the input fields.
     */
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
            if (productQuantity.getProduct().getProductName()
                    .equals(editTextProduct.getText().toString())) {
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
            ProductQuantity productQuantity = new ProductQuantity(
                    dbHandler.getProduct(selectedProductId),
                    Utils.getKgsFromCurrentMassUnit(this,
                            Double.parseDouble(editTextMass.getText().toString())),
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

    /**
     * Initialises input fields with data from the {@link Order} being edited.
     */
    private void fillFields() {
        productsAddedList.clear();

        productsAddedList.addAll(orderToEdit.getProductList());
        productsAddedAdapter.notifyDataSetChanged();

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        editTextDest.setText(orderToEdit.getDestName());

        TextInputEditText editTextDate = findViewById(R.id.edit_text_date);
        editTextDate.setText(
                orderToEdit.getOrderDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));

        CheckBox checkBoxCompleted = findViewById(R.id.checkbox_completed);
        checkBoxCompleted.setChecked(orderToEdit.isCompleted());

        selectedDestId = orderToEdit.getDestId();
        selectedDateTime = orderToEdit.getOrderDate();

    }

    /**
     * Stores the {@link Order} in the database if it is valid.
     * First checks each input field to check it is valid. If a field is invalid,
     * an appropriate message is shown to help the user correct the error. If all
     * the fields have valid data, either adds a new order or edits the existing
     * one depending on the edit mode of this activity.
     */
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
                        Utils.getKgsFromCurrentMassUnit(this,
                                Double.parseDouble(editTextMass.getText().toString())),
                        TextUtils.isEmpty(editTextNumBoxes.getText()) ? -1
                                : Integer.parseInt(editTextNumBoxes.getText().toString())));
            }
            productList.addAll(productsAddedList);
            newOrder.setProductList(productList);
            newOrder.setDest(dbHandler.getLocation(selectedDestId));
            newOrder.setOrderDate(selectedDateTime);
            newOrder.setCompleted(false);

            if (editType == EDIT_TYPE_NEW) {
                int newRowId = dbHandler.addOrder(newOrder);
                newOrder.setOrderId(newRowId);
                UndoStack.getInstance().push(new AddOrderAction(newOrder));
                return newRowId != -1;
            } else {
                newOrder.setOrderId(orderToEdit.getOrderId());
                CheckBox checkboxCompleted = findViewById(R.id.checkbox_completed);
                newOrder.setCompleted(checkboxCompleted.isChecked());
                boolean success = dbHandler.updateOrder(newOrder);

                UndoStack.getInstance().push(new EditOrderAction(orderToEdit, newOrder));

                return success;
            }
        }
        return false;
    }
}
