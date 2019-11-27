package com.martinwalls.meatmanager.ui.contracts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.*;
import com.martinwalls.meatmanager.ui.InputFormActivity;
import com.martinwalls.meatmanager.ui.ProductsAddedAdapter;
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
import com.martinwalls.meatmanager.util.undo.contracts.AddContractAction;
import com.martinwalls.meatmanager.util.undo.contracts.EditContractAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class EditContractActivity extends InputFormActivity
        implements ProductsAddedAdapter.ProductsAddedAdapterListener,
        AddNewProductDialog.AddNewProductListener,
        RepeatIntervalDialog.RepeatIntervalDialogListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    public static final String EXTRA_EDIT_TYPE = "edit_type";
    public static final int EDIT_TYPE_NEW = 0;
    public static final int EDIT_TYPE_EDIT = 1;
    public static final String EXTRA_CONTRACT_ID = "contract_id";

    private final int REQUEST_NEW_DESTINATION = 1;

    private final int DEFAULT_REMINDER = 1;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_REPEAT_INTERVAL = "repeat_interval";
    private final String INPUT_REPEAT_ON = "repeat_on";
    private final String INPUT_REMINDER = "reminder";

    private int editType = EDIT_TYPE_NEW;

    private DBHandler dbHandler;
    private Contract contractToEdit;

    private ProductsAddedAdapter productsAddedAdapter;
    private List<ProductQuantity> productsAddedList = new ArrayList<>();
    private RecyclerView productsAddedRecyclerView;

    private TextView addProductBtn;

    private EditText editTextReminder;

    // store ids/values of selected items
    private int selectedProductId;
    private int selectedDestId;
    private Interval selectedRepeatInterval;

    private ArrayAdapter<CharSequence> repeatOnSpnAdapter;
    private boolean isSpinnerInitialised = false;

    private boolean isWeek = true;

    private boolean hasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contract);

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editType = extras.getInt(EXTRA_EDIT_TYPE, EDIT_TYPE_NEW);
            if (editType == EDIT_TYPE_EDIT) {
                int contractId = extras.getInt(EXTRA_CONTRACT_ID);
                contractToEdit = dbHandler.getContract(contractId);
            }
        }

        getSupportActionBar().setTitle(editType == EDIT_TYPE_NEW
                ? R.string.contract_new_title
                : R.string.contract_edit_title);

        setCurrentSearchType(INPUT_PRODUCT);

        // initialise views in the layout, including listeners
        initViews();

        if (editType == EDIT_TYPE_NEW) {
            findViewById(R.id.product_btn_done).setVisibility(View.GONE);
            setReminderInputValue(DEFAULT_REMINDER);
        } else {
            findViewById(R.id.product_inputs).setVisibility(View.GONE);

            fillFields();

            ImageButton productBtnDone = findViewById(R.id.product_btn_done);
            productBtnDone.setOnClickListener(v -> {
                // store product details and hide input views again
                addProductToProductsAddedList();
                findViewById(R.id.product_inputs).setVisibility(View.GONE);
            });
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
                            getString(R.string.db_error_insert, "contract"),
                            Toast.LENGTH_SHORT).show();
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
        if (resultCode == RESULT_OK && requestCode == REQUEST_NEW_DESTINATION) {
            selectedDestId = data.getIntExtra(NewLocationActivity.RESULT_ID, -1);
            Location newDest = dbHandler.getLocation(selectedDestId);
            TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
            editTextDest.setText(newDest.getLocationName());
            editTextDest.clearFocus();
            cancelSearch();
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
            selectedProductId = newProduct.getProductId();
            TextInputEditText editTextProduct = findViewById(R.id.edit_text_product);
            editTextProduct.setText(newProduct.getProductName());
            editTextProduct.clearFocus();
            cancelSearch();
        }
    }

    @Override
    public void onProductAddedDelete(int position) {
        productsAddedList.remove(position);
        productsAddedAdapter.notifyItemRemoved(position);
    }

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
     * This is called when a radio button is clicked in the {@link RepeatIntervalDialog}.
     * Sets the contract's repeat interval to the chosen value.
     */
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

    /**
     * Called when the user enters a custom repeat interval in the
     * {@link RepeatIntervalDialog}. Sets the contract's repeat interval to 
     * this chosen value.
     */
    @Override
    public void onCustomIntervalSelected(Interval interval) {
        setSelectedRepeatInterval(interval);
    }

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

        editTextReminder = findViewById(R.id.edit_text_reminder);

        checkMassUnit();

        initSearchLayout();
        initSearchFieldListeners();

        initProductsAddedView();
        initRepeatIntervalInput();
        initRepeatOnInput();
        initReminderInput();
    }

    /**
     * Retrieves the current mass unit set by the user preferences, and updates
     * the input fields accordingly.
     */
    private void checkMassUnit() {
        if (MassUnit.getMassUnit(this) == MassUnit.LBS) {
            TextInputLayout inputLayoutQuantityMass =
                    findViewById(R.id.input_layout_quantity_mass);
            inputLayoutQuantityMass.setHint(getString(R.string.contracts_input_quantity_lbs));
        }
    }

    /**
     * Sets which views should be hidden when a search view is opened.
     */
    private void setViewsToHide() {
        addViewToHide(INPUT_PRODUCT, R.id.input_layout_product);
        addViewToHide(INPUT_QUANTITY, R.id.input_row_quantity);
        addViewToHide(INPUT_DESTINATION, R.id.input_layout_destination);
        addViewToHide(INPUT_REPEAT_INTERVAL, R.id.input_layout_repeat_interval);
        addViewToHide(INPUT_REPEAT_ON, R.id.input_repeat_on);
        addViewToHide(INPUT_REMINDER, R.id.input_reminder);

        addViewToHide("add_product_btn", R.id.add_product);
        addViewToHide("products_added_recycler_view", R.id.products_added_recycler_view);
    }

    /**
     * Initialises the search layout which is shown when the user clicks on a
     * relevant input field. These are set in {@link #initSearchFieldListeners()}.
     */
    private void initSearchLayout() {
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        TextView emptyView = findViewById(R.id.no_results);
        recyclerView.setEmptyView(emptyView);
        setSearchItemAdapter(new SearchItemAdapter(
                getSearchItemList(), getCurrentSearchType(), this));
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

        setListeners(INPUT_DESTINATION,
                findViewById(R.id.input_layout_destination),
                findViewById(R.id.edit_text_destination));
    }

    /**
     * Initialises the view to show a list of products the user has already
     * added to the contract, if they add more than one.
     */
    private void initProductsAddedView() {
        productsAddedAdapter = new ProductsAddedAdapter(productsAddedList, this,
                editType == EDIT_TYPE_EDIT, true);
        productsAddedRecyclerView = findViewById(R.id.products_added_recycler_view);
        productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addProductBtn = findViewById(R.id.add_product);
        addProductBtn.setOnClickListener(v -> addProductToProductsAddedList());
    }

    /**
     * Initialises the repeat interval input field to open a {@link RepeatIntervalDialog}
     * when the user clicks it.
     */
    private void initRepeatIntervalInput() {
        // show dialog when user selects repeat interval input field
        TextInputEditText editTextRepeatInterval =
                findViewById(R.id.edit_text_repeat_interval);
        editTextRepeatInterval.setOnClickListener(v -> showRepeatIntervalDialog());
    }

    /**
     * Initialises the repeat on input field.
     */
    private void initRepeatOnInput() {
        Spinner repeatOnSpn = findViewById(R.id.spn_repeat_on);
        repeatOnSpnAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        repeatOnSpnAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        repeatOnSpnAdapter.addAll(getResources().getStringArray(R.array.weekdays));
        repeatOnSpnAdapter.notifyDataSetChanged();
        repeatOnSpn.setAdapter(repeatOnSpnAdapter);

        // hide input at start
        findViewById(R.id.input_repeat_on).setVisibility(View.GONE);
    }

    /**
     * Initialises the reminder input field, including increment and
     * decrement buttons.
     */
    private void initReminderInput() {
        TextView txtReminderDaysBefore = findViewById(R.id.reminder_text_days_before);
        TextView hintReminder = findViewById(R.id.text_reminder);
        // set correct plural form based on reminder input
        editTextReminder.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || s.toString().equals("0")) {
                    hintReminder.setText(R.string.contracts_reminder_off_hint);
                } else {
                    hintReminder.setText(R.string.contracts_reminder_hint);
                }
                if (!TextUtils.isEmpty(s)) {
                    txtReminderDaysBefore.setText(getResources().getQuantityString(
                            R.plurals.contracts_reminder_days_before,
                            Integer.parseInt(s.toString())));
                }
            }
        });

        // set plus and minus buttons
        ImageButton btnReminderMinus = findViewById(R.id.btn_reminder_minus);
        btnReminderMinus.setOnClickListener(v -> decrementReminder());

        ImageButton btnReminderPlus = findViewById(R.id.btn_reminder_plus);
        btnReminderPlus.setOnClickListener(v -> incrementReminder());
    }

    /**
     * Sets the reminder input field to the specified value.
     */
    private void setReminderInputValue(int value) {
        editTextReminder.setText(String.valueOf(value));
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

        TextInputEditText editTextRepeat = findViewById(R.id.edit_text_repeat_interval);
        editTextRepeat.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });

        Spinner spinnerRepeatOn = findViewById(R.id.spn_repeat_on);
        spinnerRepeatOn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (!isSpinnerInitialised) {
                    isSpinnerInitialised = true;
                    return;
                }
                hasChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        EditText editTextReminder = findViewById(R.id.edit_text_reminder);
        editTextReminder.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanged = true;
            }
        });
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
     * Initialises input fields with data from the {@link Contract} being edited.
     */
    private void fillFields() {
        productsAddedList.clear();

        productsAddedList.addAll(contractToEdit.getProductList());
        productsAddedAdapter.notifyDataSetChanged();

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        editTextDest.setText(contractToEdit.getDestName());

        setSelectedRepeatInterval(contractToEdit.getRepeatInterval());

        Spinner repeatOnSpn = findViewById(R.id.spn_repeat_on);
        repeatOnSpn.setSelection(contractToEdit.getRepeatOn() - 1);

        TextView hintReminder = findViewById(R.id.text_reminder);
        int reminderDays = contractToEdit.getReminder();
        if (reminderDays > 0) {
            setReminderInputValue(contractToEdit.getReminder());
            hintReminder.setText(R.string.contracts_reminder_hint);
        } else {
            hintReminder.setText(R.string.contracts_reminder_off_hint);
        }

        selectedDestId = contractToEdit.getDestId();
    }

    /**
     * Shows a {@link RepeatIntervalDialog} so the user can select a repeat
     * interval for this contract.
     */
    private void showRepeatIntervalDialog() {
        DialogFragment dialog = new RepeatIntervalDialog();
        Bundle args = new Bundle();
        if (selectedRepeatInterval != null) {
            if (selectedRepeatInterval.hasValues(1, Interval.TimeUnit.WEEK)) {
                // 1 week
                args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                        RepeatIntervalDialog.OPTION_WEEK);
            } else if (selectedRepeatInterval.hasValues(2, Interval.TimeUnit.WEEK)) {
                // 2 weeks
                args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                        RepeatIntervalDialog.OPTION_TWO_WEEK);
            } else if (selectedRepeatInterval.hasValues(1, Interval.TimeUnit.MONTH)) {
                // 1 month
                args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                        RepeatIntervalDialog.OPTION_MONTH);
            } else {
                // custom interval
                args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                        RepeatIntervalDialog.OPTION_CUSTOM);
                args.putSerializable(RepeatIntervalDialog.EXTRA_CUSTOM_INTERVAL,
                        selectedRepeatInterval);
            }
        }
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "repeat_interval");
    }

    /**
     * Stores the repeat interval the user has entered.
     */
    private void setSelectedRepeatInterval(Interval interval) {
        // repeat on input is hidden until repeat interval is set
        LinearLayout inputRepeatOn = findViewById(R.id.input_repeat_on);
        inputRepeatOn.setVisibility(View.VISIBLE);

        selectedRepeatInterval = interval;
        TextInputEditText editTextRepeatInterval =
                findViewById(R.id.edit_text_repeat_interval);
        if (interval.getValue() == 1) {
            editTextRepeatInterval.setText(getString(
                    R.string.contracts_repeat_interval_display_one,
                    interval.getUnit().name().toLowerCase()));
        } else {
            editTextRepeatInterval.setText(getString(
                    R.string.contracts_repeat_interval_display_multiple,
                    interval.getValue(), interval.getUnit().name().toLowerCase()));
        }
        if (!isWeek && interval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnSpnAdapter.clear();
            repeatOnSpnAdapter.addAll(getResources().getStringArray(R.array.weekdays));
            repeatOnSpnAdapter.notifyDataSetChanged();
            isWeek = true;
        } else if (isWeek && interval.getUnit() == Interval.TimeUnit.MONTH) {
            repeatOnSpnAdapter.clear();
            for (int i = 1; i <= 31; i++) {
                repeatOnSpnAdapter.add("Day " + i);
            }
            repeatOnSpnAdapter.notifyDataSetChanged();
            isWeek = false;
        }

        TextView repeatOnTxt = findViewById(R.id.text_repeat_on);
        if (interval.getUnit() == Interval.TimeUnit.WEEK) {
            if (interval.getValue() == 1) {
                repeatOnTxt.setText(R.string.contracts_repeat_on_week);
            } else if (interval.getValue() == 2) {
                repeatOnTxt.setText(R.string.contracts_repeat_on_two_week);
            } else {
                repeatOnTxt.setText(R.string.contracts_repeat_on_default);
            }
        } else if (interval.getUnit() == Interval.TimeUnit.MONTH) {
            if (interval.getValue() == 1) {
                repeatOnTxt.setText(R.string.contracts_repeat_on_month);
            } else {
                repeatOnTxt.setText(R.string.contracts_repeat_on_default);
            }
        } else {
            repeatOnTxt.setText(R.string.contracts_repeat_on_default);
        }
    }

    /**
     * Increments the value in the reminder input field.
     */
    private void incrementReminder() {
        EditText editTextReminder = findViewById(R.id.edit_text_reminder);
        if (TextUtils.isEmpty(editTextReminder.getText())) {
            setReminderInputValue(1);
        } else {
            int currentReminder = Integer.valueOf(editTextReminder.getText().toString());
            setReminderInputValue(currentReminder + 1);
        }
    }

    /**
     * Decrements the value in the reminder input field, if it is greater than 0.
     */
    private void decrementReminder() {
        EditText editTextReminder = findViewById(R.id.edit_text_reminder);
        if (!TextUtils.isEmpty(editTextReminder.getText())) {
            int currentReminder = Integer.valueOf(editTextReminder.getText().toString());
            if (currentReminder > 0) {
                setReminderInputValue(currentReminder - 1);
            }
        }
    }

    /**
     * Stores the {@link Contract} in the database if it is valid.
     * First checks each input field to check it is valid. If a field is
     * invalid, an appropriate error message is shown to help the user 
     * correct the error. If all the fields have valid data, either adds a new
     * contract or edits the existing one depending on the edit mode of this
     * activity.
     */
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

        TextInputEditText editTextRepeatInterval =
                findViewById(R.id.edit_text_repeat_interval);
        TextInputLayout inputLayoutRepeatInterval =
                findViewById(R.id.input_layout_repeat_interval);

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
                productsAddedList.add(new ProductQuantity(
                        dbHandler.getProduct(selectedProductId),
                        Utils.getKgsFromCurrentMassUnit(this,
                                Double.parseDouble(editTextMass.getText().toString())),
                        TextUtils.isEmpty(editTextNumBoxes.getText()) ? -1
                                : Integer.parseInt(editTextNumBoxes.getText().toString())));
            }
            newContract.setProductList(productsAddedList);
            newContract.setDest(dbHandler.getLocation(selectedDestId));
            newContract.setRepeatInterval(selectedRepeatInterval);
            newContract.setRepeatOn(repeatOnSpn.getSelectedItemPosition() + 1);

            newContract.setStartDate(LocalDate.now());
            if (TextUtils.isEmpty(editTextReminder.getText())
                    || editTextReminder.getText().toString().equals("0")) {
                newContract.setReminder(-1);
            } else {
                newContract.setReminder(
                        Integer.parseInt(editTextReminder.getText().toString()));
            }

            if (editType == EDIT_TYPE_NEW) {
                int newRowId = dbHandler.addContract(newContract);
                newContract.setContractId(newRowId);
                UndoStack.getInstance().push(new AddContractAction(newContract));
                return newRowId != -1;
            } else {
                newContract.setContractId(contractToEdit.getContractId());
                boolean success = dbHandler.updateContract(newContract);
                UndoStack.getInstance().push(
                        new EditContractAction(contractToEdit, newContract));
                return success;
            }
        }
        return false;
    }
}
