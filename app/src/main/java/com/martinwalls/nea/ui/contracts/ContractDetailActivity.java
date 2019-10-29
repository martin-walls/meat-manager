package com.martinwalls.nea.ui.contracts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.data.models.Interval;
import com.martinwalls.nea.ui.ProductsAddedAdapter;

public class ContractDetailActivity extends AppCompatActivity {

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

        TextView destination = findViewById(R.id.destination);
        destination.setText(contract.getDestName());

        TextView repeat = findViewById(R.id.repeat);
        String repeatStr;
        String repeatOnStr;
        Interval repeatInterval = contract.getRepeatInterval();
        if (repeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnStr = getResources().getStringArray(R.array.weekdays)[contract.getRepeatOn()];
        } else {
            repeatOnStr = "day " + (contract.getRepeatOn() + 1);
        }
        if (repeatInterval.getValue() == 1) {
            repeatStr = getString(R.string.contracts_repeat_display_one,
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        } else {
            repeatStr = getString(R.string.contracts_repeat_display_multiple,
                    repeatInterval.getValue(), repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        }
        repeat.setText(repeatStr);
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
                Toast.makeText(this, "DELETE", Toast.LENGTH_SHORT).show();
                //todo delete contract
                return true;
            case R.id.action_edit:
//                Toast.makeText(this, "EDIT", Toast.LENGTH_SHORT).show();
                //todo edit contract
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
}
