package com.martinwalls.nea.contracts;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.ProductsAddedAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Contract;
import com.martinwalls.nea.models.Interval;

public class ContractDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTRACT_ID = "contract_id";

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
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_edit:
                Toast.makeText(this, "EDIT", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
