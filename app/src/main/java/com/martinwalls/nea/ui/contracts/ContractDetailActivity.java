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
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.data.models.Interval;
import com.martinwalls.nea.ui.ProductsAddedAdapter;
import com.martinwalls.nea.ui.misc.dialog.ConfirmDeleteDialog;
import com.martinwalls.nea.util.undo.contracts.DeleteContractAction;
import com.martinwalls.nea.util.undo.UndoStack;

public class ContractDetailActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.ConfirmDeleteListener {

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

        ProductsAddedAdapter productsAdapter = new ProductsAddedAdapter(contract.getProductList());
        RecyclerView productsRecyclerView = findViewById(R.id.recycler_view_products);
        productsRecyclerView.setAdapter(productsAdapter);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fillData();
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
                editIntent.putExtra(EditContractActivity.EXTRA_EDIT_TYPE, EditContractActivity.EDIT_TYPE_EDIT);
                editIntent.putExtra(EditContractActivity.EXTRA_CONTRACT_ID, contract.getContractId());
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
            Toast.makeText(this, R.string.db_delete_contract_success, Toast.LENGTH_SHORT).show();
            UndoStack.getInstance().push(new DeleteContractAction(contract));
            finish();
        } else {
            Toast.makeText(this, R.string.db_delete_contract_error, Toast.LENGTH_SHORT).show();
        }
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
     * Initialises fields with data from the {@link Contract} being shown.
     */
    private void fillData() {
        if (contract == null) {
            return;
        }

        TextView destination = findViewById(R.id.destination);
        destination.setText(contract.getDestName());

        TextView repeat = findViewById(R.id.repeat);
        String repeatStr;
        String repeatOnStr;
        Interval repeatInterval = contract.getRepeatInterval();
        if (repeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnStr = getResources().getStringArray(R.array.weekdays)[contract.getRepeatOn() - 1];
        } else {
            repeatOnStr = "day " + contract.getRepeatOn();
        }
        if (repeatInterval.getValue() == 1) {
            repeatStr = getString(R.string.contracts_repeat_display_one,
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        } else {
            repeatStr = getString(R.string.contracts_repeat_display_multiple,
                    repeatInterval.getValue(), repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
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
                nextRepeat.setText(getString(R.string.contracts_next_repeat_on, contract.getDaysToNextRepeat()));
                break;
        }

        TextView reminder = findViewById(R.id.reminder);
        int reminderDaysBefore = contract.getReminder();
        if (reminderDaysBefore > 0) {
            reminder.setText(getResources().getQuantityString(R.plurals.contract_reminder_display,
                    reminderDaysBefore, reminderDaysBefore));
        } else {
            reminder.setText(R.string.contract_reminder_display_off);
        }
    }
}
