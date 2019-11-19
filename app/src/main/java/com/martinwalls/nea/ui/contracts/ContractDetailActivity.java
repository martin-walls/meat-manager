package com.martinwalls.nea.ui.contracts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.*;
import com.martinwalls.nea.ui.ProductsAddedAdapter;
import com.martinwalls.nea.ui.RelatedStockAdapter;
import com.martinwalls.nea.ui.misc.dialog.ConfirmDeleteDialog;
import com.martinwalls.nea.ui.stock.StockDetailActivity;
import com.martinwalls.nea.util.SortUtils;
import com.martinwalls.nea.util.undo.UndoStack;
import com.martinwalls.nea.util.undo.contracts.DeleteContractAction;

import java.util.ArrayList;
import java.util.List;

public class ContractDetailActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.ConfirmDeleteListener,
        RelatedStockAdapter.RelatedStockListener {

    public static final String EXTRA_CONTRACT_ID = "contract_id";

    private static final int REQUEST_REFRESH_ON_DONE = 1;

    private DBHandler dbHandler;
    private Contract contract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);
        getSupportActionBar().setTitle(R.string.contract_details_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int contractId = extras.getInt(EXTRA_CONTRACT_ID);
            contract = dbHandler.getContract(contractId);
        }

        ProductsAddedAdapter productsAdapter =
                new ProductsAddedAdapter(contract.getProductList());
        RecyclerView productsRecyclerView = findViewById(R.id.recycler_view_products);
        productsRecyclerView.setAdapter(productsAdapter);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        initStockForContract();

        TextView relatedStockTitle = findViewById(R.id.related_stock_title);
        relatedStockTitle.setText(R.string.related_stock_title);

        fillFields();
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
                editIntent.putExtra(EditContractActivity.EXTRA_CONTRACT_ID,
                        contract.getContractId());
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
     * Deletes the {@link Contract} being edited from the database. This is 
     * called when the user confirms the delete action from the 
     * {@link ConfirmDeleteDialog}.
     */
    @Override
    public void onConfirmDelete() {
        boolean success = dbHandler.deleteContract(contract.getContractId());
        if (success) {
            Toast.makeText(this, R.string.db_delete_contract_success, Toast.LENGTH_SHORT)
                    .show();
            UndoStack.getInstance().push(new DeleteContractAction(contract));
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
     * Initialises list of related stock for the {@link Contract} being shown.
     */
    private void initStockForContract() {
        List<StockItem> stockForContract = SortUtils.mergeSort(
                dbHandler.getAllStockForContract(contract.getContractId()),
                StockItem.comparatorLocation());

        // get list of related stock by product, with child stock items for each location
        List<RelatedStock> relatedStockList = new ArrayList<>();
        int thisProductId;
        int lastProductId = -1;
        for (StockItem stockItem : stockForContract) {
            thisProductId = stockItem.getProduct().getProductId();
            if (relatedStockList.size() == 0 || thisProductId != lastProductId) {
                RelatedStock relatedStock = new RelatedStock();
                relatedStock.setProduct(stockItem.getProduct());
                relatedStock.addStockItem(stockItem);
                relatedStockList.add(relatedStock);
            } else {
                relatedStockList.get(relatedStockList.size() - 1).addStockItem(stockItem);
            }
            lastProductId = thisProductId;
        }

        // show items for products that have no stock
        for (ProductQuantity productQuantity : contract.getProductList()) {
            boolean hasRelatedStock = false;
            for (RelatedStock relatedStock : relatedStockList) {
                if (relatedStock.getProduct().getProductId()
                        == productQuantity.getProduct().getProductId()) {
                    hasRelatedStock = true;
                    break;
                }
            }
            if (!hasRelatedStock) {
                relatedStockList.add(new RelatedStock(productQuantity.getProduct()));
            }
        }

        RelatedStockAdapter relatedStockAdapter
                = new RelatedStockAdapter(relatedStockList, this);
        RecyclerView relatedStockRecyclerView = findViewById(R.id.recycler_view_related_stock);
        relatedStockRecyclerView.setAdapter(relatedStockAdapter);
        relatedStockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Initialises fields with data from the {@link Contract} being shown.
     */
    private void fillFields() {
        TextView destination = findViewById(R.id.destination);
        destination.setText(contract.getDestName());

        TextView repeat = findViewById(R.id.repeat);
        String repeatStr;
        String repeatOnStr;
        Interval repeatInterval = contract.getRepeatInterval();
        if (repeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnStr = getResources()
                    .getStringArray(R.array.weekdays)[contract.getRepeatOn() - 1];
        } else {
            repeatOnStr = "day " + contract.getRepeatOn();
        }
        if (repeatInterval.getValue() == 1) {
            repeatStr = getString(R.string.contracts_repeat_display_one,
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        } else {
            repeatStr = getString(R.string.contracts_repeat_display_multiple,
                    repeatInterval.getValue(),
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        }
        repeat.setText(repeatStr);

        TextView nextRepeat = findViewById(R.id.next_repeat);
        int daysToNextRepeat = contract.getDaysToNextRepeat();
        switch (daysToNextRepeat) {
            case 0:
                nextRepeat.setText(R.string.contracts_next_repeat_today);
                break;
            case 1:
                nextRepeat.setText(R.string.contracts_next_repeat_tomorrow);
                break;
            default:
                nextRepeat.setText(getString(R.string.contracts_next_repeat_on,
                        contract.getDaysToNextRepeat()));
                break;
        }

        TextView reminder = findViewById(R.id.reminder);
        int reminderDaysBefore = contract.getReminder();
        if (reminderDaysBefore > 0) {
            reminder.setText(getResources()
                    .getQuantityString(R.plurals.contract_reminder_display,
                            reminderDaysBefore, reminderDaysBefore));
        } else {
            reminder.setText(R.string.contract_reminder_display_off);
        }
    }
}
