package com.martinwalls.nea.orders;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.martinwalls.nea.ConfirmCancelDialog;
import com.martinwalls.nea.ProductsAddedAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Order;
import com.martinwalls.nea.models.ProductQuantity;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EditOrderActivity extends AppCompatActivity
        implements ProductsAddedAdapter.ProductsAddedAdapterListener,
        ConfirmCancelDialog.ConfirmCancelListener {

    public static final String EXTRA_ORDER_ID = "order_id";

    private final String DATE_FORMAT = "dd MMMM yyyy, HH:mm";

    private DBHandler dbHandler;
    private Order order;

    private ProductsAddedAdapter productsAddedAdapter;
    private List<ProductQuantity> productsAddedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int orderId = extras.getInt(EXTRA_ORDER_ID);
            order = dbHandler.getOrder(orderId);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.order_edit_title);
        }

        productsAddedAdapter =
                new ProductsAddedAdapter(productsAddedList, this, false, true);
        RecyclerView productsAddedRecyclerView = findViewById(R.id.products_added_recycler_view);
        productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        getMenuInflater().inflate(R.menu.activity_edit_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                //todo done action
                return true;
            case R.id.action_cancel:
                if (true /*todo cancel condition*/) {
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
    public void onConfirmCancelYesAction() {
        finish();
    }

    private void showConfirmCancelDialog() {
        DialogFragment dialog = new ConfirmCancelDialog();
        dialog.show(getSupportFragmentManager(), "confirm_cancel");
    }

    private void fillFields() {
        productsAddedList.clear();
        productsAddedList.addAll(order.getProductList());
        productsAddedAdapter.notifyDataSetChanged();

        TextInputEditText editTextDest = findViewById(R.id.edit_text_destination);
        editTextDest.setText(order.getDestName());

        TextInputEditText editTextDate = findViewById(R.id.edit_text_date);
        editTextDate.setText(order.getOrderDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));

        MaterialCheckBox checkBoxCompleted = findViewById(R.id.checkbox_completed);
        checkBoxCompleted.setChecked(order.isCompleted());
    }
}
