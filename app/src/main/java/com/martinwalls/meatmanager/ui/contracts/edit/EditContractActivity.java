package com.martinwalls.meatmanager.ui.contracts.edit;

import android.content.Intent;
import android.graphics.fonts.Font;
import android.opengl.ETC1;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.data.models.SearchItem;
import com.martinwalls.meatmanager.databinding.ActivityEditContractBinding;
import com.martinwalls.meatmanager.ui.InputFormActivity;
import com.martinwalls.meatmanager.ui.common.adapter.ProductsAddedAdapter;
import com.martinwalls.meatmanager.ui.common.adapter.SearchItemAdapter;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;

public class EditContractActivity extends InputFormActivity
        implements ProductsAddedAdapter.ProductsAddedAdapterListener {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContractBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSearchItemViewModel(EditContractSearchItemViewModel.class);

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
        setAddNewView(binding.addNew);
        setRootView((ViewGroup) binding.getRoot());


        if (MassUnit.getMassUnit(this) == MassUnit.LBS) {
            binding.inputLayoutQuantityMass.setHint(
                    getString(R.string.contracts_input_quantity_lbs));
        }


        // search results recycler view
        binding.recyclerViewResults.setEmptyView(binding.noResults);
        binding.recyclerViewResults.setAdapter(getSearchItemAdapter());
        binding.recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        setSearchResultsLayout(binding.searchResultsLayout);


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
        cancelSearch();
    }

    @Override
    protected void addNewItemFromSearch(String searchType) {
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
        addViewToHide(INPUT_PRODUCT, R.id.input_layout_product);
        addViewToHide(INPUT_QUANTITY, R.id.input_row_quantity);
        addViewToHide(INPUT_DESTINATION, R.id.input_layout_destination);
        addViewToHide(INPUT_REPEAT_INTERVAL, R.id.input_layout_repeat_interval);
        addViewToHide(INPUT_REPEAT_ON, R.id.input_repeat_on);
        addViewToHide(INPUT_REMINDER, R.id.input_reminder);

        addViewToHide("add_product_btn", R.id.add_product);
        addViewToHide("products_added_recycler_view", R.id.products_added_recycler_view);
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

}
