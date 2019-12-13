package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.data.models.SearchItem;
import com.martinwalls.meatmanager.databinding.ActivityEditContractBinding;
import com.martinwalls.meatmanager.ui.common.adapter.ProductsAddedAdapter;
import com.martinwalls.meatmanager.ui.common.adapter.SearchItemAdapter;
import com.martinwalls.meatmanager.ui.common.dialog.ConfirmCancelDialog;
import com.martinwalls.meatmanager.ui.products.AddNewProductDialog;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;

import java.util.HashMap;

public class EditContractActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener,
        ProductsAddedAdapter.ProductsAddedAdapterListener,
        AddNewProductDialog.AddNewProductListener,
        RepeatIntervalDialog.RepeatIntervalDialogListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    public static final String EXTRA_EDIT_TYPE = "edit_type";
    public static final String EXTRA_CONTRACT_ID = "contract_id";

    public static final int EDIT_TYPE_NEW = 0;
    public static final int EDIT_TYPE_EDIT = 1;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_QUANTITY = "quantity";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_REPEAT_INTERVAL = "repeat_interval";
    private final String INPUT_REPEAT_ON = "repeat_on";
    private final String INPUT_REMINDER = "reminder";

    private EditContractViewModel viewModel;

    private ActivityEditContractBinding binding;



    // adapters
    private ProductsAddedAdapter productsAddedAdapter;
    private ArrayAdapter<CharSequence> repeatOnSpnAdapter;


    private SearchItemAdapter searchItemAdapter;

    private HashMap<String, Integer> viewsToHide = new HashMap<>();
    private String currentSearchType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContractBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setSearchItemViewModel(EditContractSearchItemViewModel.class);

        Bundle extras = getIntent().getExtras();
        int editType = EDIT_TYPE_NEW;
        int contractId = -1;
        if (extras != null) {
            editType = extras.getInt(EXTRA_EDIT_TYPE, EDIT_TYPE_NEW);
            if (editType == EDIT_TYPE_EDIT) {
                contractId = extras.getInt(EXTRA_CONTRACT_ID);
            }
        }

        EditContractViewModelFactory factory =
                new EditContractViewModelFactory(getApplication(), editType);
        if (editType == EDIT_TYPE_EDIT) {
            factory.setContractId(contractId);
        }

        viewModel = ViewModelProviders.of(this, factory)
                .get(EditContractViewModel.class);

        // set page title
        getSupportActionBar().setTitle(viewModel.isEditMode()
                ? R.string.contract_new_title
                : R.string.contract_edit_title);


        setViewsToHide();

        binding.addNew.setOnClickListener(
                v -> addNewItemFromSearch(binding.addNew.getSearchItemType()));


//        setRootView((ViewGroup) binding.getRoot());


        if (MassUnit.getMassUnit(this) == MassUnit.LBS) {
            binding.inputLayoutQuantityMass.setHint(
                    getString(R.string.contracts_input_quantity_lbs));
        }


        // search results recycler view
        binding.recyclerViewResults.setEmptyView(binding.noResults);
        searchItemAdapter = new SearchItemAdapter(this);
        binding.recyclerViewResults.setAdapter(searchItemAdapter);
        binding.recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getSearchItemList().observe(this,
                searchItems -> searchItemAdapter.setSearchItems(searchItems));


        setListeners(INPUT_PRODUCT, binding.inputLayoutProduct, binding.editTextProduct);
        setListeners(INPUT_DESTINATION,
                binding.inputLayoutDestination, binding.editTextDestination);


        productsAddedAdapter = new ProductsAddedAdapter(
                this, editType == EDIT_TYPE_EDIT, true);
        binding.productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        binding.productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.addProduct.setOnClickListener(v -> {
            //todo add product btn listener
        });


        binding.editTextRepeatInterval.setOnClickListener(v -> {
            //todo show repeat interval dialog
        });


        repeatOnSpnAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        repeatOnSpnAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        repeatOnSpnAdapter.addAll(getResources().getStringArray(R.array.weekdays));
        repeatOnSpnAdapter.notifyDataSetChanged();
        binding.spnRepeatOn.setAdapter(repeatOnSpnAdapter);


        binding.inputRepeatOn.setVisibility(View.GONE);



        binding.editTextReminder.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || s.toString().equals("0")) {
                    binding.textReminder.setText(R.string.contracts_reminder_off_hint);
                } else {
                    binding.textReminder.setText(R.string.contracts_reminder_hint);
                }
                if (!TextUtils.isEmpty(s)) {
                    binding.reminderTextDaysBefore.setText(getResources().getQuantityString(
                            R.plurals.contracts_reminder_days_before,
                            Integer.parseInt(s.toString())));
                }
            }
        });


        binding.btnReminderMinus.setOnClickListener(v -> decrementReminder());
        binding.btnReminderPlus.setOnClickListener(v -> incrementReminder());


        if (editType == EDIT_TYPE_NEW) {
            binding.productBtnDone.setVisibility(View.GONE);
            binding.editTextReminder.setText("1"); //todo method setReminderInputValue(int)
        } else {
            binding.productInputs.setVisibility(View.GONE);

            binding.productBtnDone.setOnClickListener(v -> {
                //todo add product to products added list (VM?)
                binding.productInputs.setVisibility(View.GONE);
            });
        }





        viewModel.getContract().observe(this, contract -> {

            //todo products added list

            binding.editTextDestination.setText(contract.getDestName());

            // todo

            binding.spnRepeatOn.setSelection(contract.getRepeatOn() - 1);


        });



        viewModel.getSelectedRepeatInterval().observe(this, interval -> {

        });

        //todo text changed listeners (VM?)
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
//                if (addContractToDb()) {
//                    finish();
//                } else {
//                    Toast.makeText(this,
//                            getString(R.string.db_error_insert, "contract"),
//                            Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
//                }
                return true;
            case R.id.action_cancel:
//                if (hasChanged) {
//                    showConfirmCancelDialog();
//                } else {
//                    finish();
//                }
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //todo
    }

    @Override
    public void onSearchItemSelected(SearchItem item, String searchItemType) {
        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        editText.setText(item.getName());
        editText.clearFocus();
        closeSearch();
    }

    @Override
    public void onAddNewProductDoneAction(Product newProduct) {
        boolean success = viewModel.addProduct(newProduct);
        if (success) {
            viewModel.setSelectedProductId(newProduct.getProductId());
            binding.editTextProduct.setText(newProduct.getProductName());
            binding.editTextProduct.clearFocus();
            closeSearch();
        }
    }

    @Override
    public void onProductAddedDelete(int position) {
        viewModel.removeProductAdded(position);
        productsAddedAdapter.notifyItemRemoved(position); //todo in vm observer
    }

//    @Override
    private void addNewItemFromSearch(String searchType) {
        switch (searchType) {
            case INPUT_PRODUCT:
//                DialogFragment dialog = new AddNewProductDialog();
//                dialog.show(getSupportFragmentManager(), "add_new_product");
                Toast.makeText(this, "new product", Toast.LENGTH_SHORT).show();
                break;
            case INPUT_DESTINATION:
//                Intent newDestIntent = new Intent(this, NewLocationActivity.class);
//                newDestIntent.putExtra(NewLocationActivity.EXTRA_LOCATION_TYPE,
//                        Location.LocationType.Destination.name());
//                startActivityForResult(newDestIntent, REQUEST_NEW_DESTINATION);
                Toast.makeText(this, "new destination", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * Sets which views should be hidden when a search view is opened.
     */
    private void setViewsToHide() {
        viewsToHide.put(INPUT_PRODUCT, R.id.input_layout_product);
        viewsToHide.put(INPUT_QUANTITY, R.id.input_row_quantity);
        viewsToHide.put(INPUT_DESTINATION, R.id.input_layout_destination);
        viewsToHide.put(INPUT_REPEAT_INTERVAL, R.id.input_layout_repeat_interval);
        viewsToHide.put(INPUT_REPEAT_ON, R.id.input_repeat_on);
        viewsToHide.put(INPUT_REMINDER, R.id.input_reminder);

        viewsToHide.put("add_product_btn", R.id.add_product);
        viewsToHide.put("products_added_recycler_view", R.id.products_added_recycler_view);
    }

    private void setListeners(String name,
                              TextInputLayout inputLayout, TextInputEditText editText) {
        //todo custom view for these?

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
            closeSearch();
        });

        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchItemAdapter.getFilter().filter(s);
            }
        });
    }

    private void openSearch(String name) {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.search_open);
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), transition);
        for (int view : viewsToHide.values()) {
            if (view != viewsToHide.get(name)) {
                findViewById(view).setVisibility(View.GONE);
            }
        }

        viewModel.loadSearchItems(name);

        currentSearchType = name;
        searchItemAdapter.setSearchItemType(name);

        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        searchItemAdapter.getFilter().filter(editText.getText());

        binding.addNew.setVisibility(View.VISIBLE);
        binding.addNew.setText(getString(R.string.search_add_new, name.toLowerCase()));
        binding.addNew.setSearchItemType(name);

        binding.searchResultsLayout.setAlpha(0);
        binding.searchResultsLayout.setVisibility(View.VISIBLE);
        binding.searchResultsLayout.animate()
                .alpha(1)
                .setStartDelay(getResources().getInteger(R.integer.search_results_fade_delay))
                .setDuration(getResources().getInteger(R.integer.search_results_fade_duration))
                .setListener(null);
    }

    private void closeSearch() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.search_close);
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), transition);
        for (int view : viewsToHide.values()) {
            findViewById(view).setVisibility(View.VISIBLE);
        }

        binding.searchResultsLayout.setVisibility(View.GONE);

        hideKeyboard();
    }

    private void decrementReminder() {
        if (!TextUtils.isEmpty(binding.editTextReminder.getText())) {
            int currentReminder = Integer.parseInt(
                    binding.editTextReminder.getText().toString());
            if (currentReminder > 0) {
                binding.editTextReminder.setText(String.valueOf(currentReminder - 1));
            }
        }
    }

    private void incrementReminder() {
        if (TextUtils.isEmpty(binding.editTextReminder.getText())) {
            binding.editTextReminder.setText("1");
        } else {
            int currentReminder = Integer.parseInt(binding.editTextReminder.getText().toString());
            binding.editTextReminder.setText(String.valueOf(currentReminder + 1));
        }
    }

    protected final void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        }
    }
}
