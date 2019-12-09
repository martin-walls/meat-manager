package com.martinwalls.meatmanager.ui.contracts.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.*;
import com.martinwalls.meatmanager.databinding.ActivityContractDetailBinding;
import com.martinwalls.meatmanager.databinding.ItemSectionDividerBinding;
import com.martinwalls.meatmanager.ui.common.adapter.ProductsAddedAdapter;
import com.martinwalls.meatmanager.ui.common.adapter.RelatedStockAdapter;
import com.martinwalls.meatmanager.ui.contracts.edit.EditContractActivity;
import com.martinwalls.meatmanager.ui.locations.detail.LocationDetailActivity;
import com.martinwalls.meatmanager.ui.common.dialog.ConfirmDeleteDialog;
import com.martinwalls.meatmanager.ui.stock.detail.StockDetailActivity;

public class ContractDetailActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.ConfirmDeleteListener,
        RelatedStockAdapter.RelatedStockListener {

    public static final String EXTRA_CONTRACT_ID = "contract_id";

    private static final int REQUEST_REFRESH_ON_DONE = 1;

    private ContractDetailViewModel viewModel;

    private ActivityContractDetailBinding binding;

    private ProductsAddedAdapter productsAdapter;
    private RelatedStockAdapter relatedStockAdapter;

    private int contractId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContractDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle(R.string.contract_details_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contractId = extras.getInt(EXTRA_CONTRACT_ID);
        }

        ContractDetailViewModelFactory factory =
                new ContractDetailViewModelFactory(getApplication(), contractId);
        viewModel = ViewModelProviders.of(this, factory)
                .get(ContractDetailViewModel.class);

        initProductsAddedView();
        initRelatedStockView();

        viewModel.getContract().observe(this, contract -> {
            contractId = contract.getContractId();
            productsAdapter.setProductList(contract.getProductList());

            binding.destination.setText(contract.getDestName());
            binding.destination.setOnClickListener(
                    v -> openLocationDetailPage(contract.getDestId()));
            setRepeatText(contract.getRepeatInterval(), contract.getRepeatOn());
            setNextRepeatText(contract.getDaysToNextRepeat());
            setReminderText(contract.getReminder());
        });

        viewModel.getRelatedStock().observe(this,
                stockItems -> relatedStockAdapter.setRelatedStockList(stockItems));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contract_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showConfirmDeleteDialog();
                return true;
            case R.id.action_edit:
                Intent editIntent = new Intent(this, EditContractActivity.class);
                editIntent.putExtra(EditContractActivity.EXTRA_EDIT_TYPE,
                        EditContractActivity.EDIT_TYPE_EDIT);
                editIntent.putExtra(EditContractActivity.EXTRA_CONTRACT_ID, contractId);
                startActivityForResult(editIntent, REQUEST_REFRESH_ON_DONE);
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            recreate();
        }
    }

    /**
     * Deletes the {@link Contract} being edited. This is
     * called when the user confirms the delete action from the 
     * {@link ConfirmDeleteDialog}.
     */
    @Override
    public void onConfirmDelete() {
        boolean success = viewModel.deleteContract();

        if (success) {
            Toast.makeText(this, R.string.db_delete_contract_success, Toast.LENGTH_SHORT)
                    .show();
            finish();
        } else {
            Toast.makeText(this, R.string.db_delete_contract_error, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onStockItemClicked(int stockId) {
        Intent stockDetailIntent = new Intent(this, StockDetailActivity.class);
        stockDetailIntent.putExtra(StockDetailActivity.EXTRA_STOCK_ID, stockId);
        startActivity(stockDetailIntent);
    }

    /**
     * Shows a {@link ConfirmDeleteDialog} asking the user to confirm the 
     * delete action.
     */
    private void showConfirmDeleteDialog() {
        DialogFragment dialog = new ConfirmDeleteDialog();
        dialog.show(getSupportFragmentManager(), "confirm_delete");
    }

    /**
     * Opens the detail page for the location with the given ID.
     */
    private void openLocationDetailPage(int locationId) {
        Intent detailIntent = new Intent(this, LocationDetailActivity.class);
        detailIntent.putExtra(LocationDetailActivity.EXTRA_LOCATION_ID, locationId);
        startActivityForResult(detailIntent, REQUEST_REFRESH_ON_DONE);
    }

    /**
     * Initialises view to show all products added to this contract.
     */
    private void initProductsAddedView() {
        productsAdapter = new ProductsAddedAdapter();
        binding.recyclerViewProducts.setAdapter(productsAdapter);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Initialises list of related stock for the {@link Contract} being shown.
     */
    private void initRelatedStockView() {
        // set section title
//        binding.relatedStockTitle.dividerText.setText(R.string.related_stock_title); //fixme

        relatedStockAdapter = new RelatedStockAdapter(this);
        binding.recyclerViewRelatedStock.setAdapter(relatedStockAdapter);
        binding.recyclerViewRelatedStock.setLayoutManager(new LinearLayoutManager(this));
        // disable animations for layout changes
        binding.recyclerViewRelatedStock.setItemAnimator(null);
    }

    /**
     * Sets text of the repeat field, showing the repeat interval and
     * repeat on.
     */
    private void setRepeatText(Interval repeatInterval, int repeatOn) {
        String repeatStr;
        String repeatOnStr;
        if (repeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnStr = getResources()
                    .getStringArray(R.array.weekdays)[repeatOn - 1];
        } else {
            repeatOnStr = "day " + repeatOn;
        }
        if (repeatInterval.getValue() == 1) {
            repeatStr = getString(R.string.contracts_repeat_display_one,
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        } else {
            repeatStr = getString(R.string.contracts_repeat_display_multiple,
                    repeatInterval.getValue(),
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        }
        binding.repeatInterval.setText(repeatStr);
    }

    /**
     * Sets text of the next repeat field, that is, how many days in advance
     * the next repeat is.
     */
    private void setNextRepeatText(int daysToNextRepeat) {
        switch (daysToNextRepeat) {
            case 0:
                binding.nextRepeat.setText(R.string.contracts_next_repeat_today);
                break;
            case 1:
                binding.nextRepeat.setText(R.string.contracts_next_repeat_tomorrow);
                break;
            default:
                binding.nextRepeat.setText(getString(
                        R.string.contracts_next_repeat_on, daysToNextRepeat));
                break;
        }
    }

    /**
     * Sets text of the reminder field.
     */
    private void setReminderText(int reminderDaysBefore) {
        if (reminderDaysBefore > 0) {
            binding.reminder.setText(getResources()
                    .getQuantityString(R.plurals.contract_reminder_display,
                            reminderDaysBefore, reminderDaysBefore));
        } else {
            binding.reminder.setText(R.string.contract_reminder_display_off);
        }
    }
}
