package com.martinwalls.nea.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.martinwalls.nea.ConfirmDeleteDialog;
import com.martinwalls.nea.ProductsAddedAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Order;
import com.martinwalls.nea.util.undo.DeleteOrderAction;
import com.martinwalls.nea.util.undo.UndoStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderDetailActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.ConfirmDeleteListener {

    public static final String EXTRA_ORDER_ID = "order_id";

    public static final int REQUEST_REFRESH_ON_DONE = 1;

    private final String DATE_FORMAT = "dd MMMM yyyy, HH:mm";

    private DBHandler dbHandler;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.order_details_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int orderId = extras.getInt(EXTRA_ORDER_ID);
            order = dbHandler.getOrder(orderId);
        }

        ProductsAddedAdapter productsAdapter = new ProductsAddedAdapter(order.getProductList());
        RecyclerView productsRecyclerView = findViewById(R.id.recycler_view_products);
        productsRecyclerView.setAdapter(productsAdapter);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView destination = findViewById(R.id.destination);
        destination.setText(order.getDestName());

        TextView date = findViewById(R.id.date);
        date.setText(order.getOrderDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));

        if (order.getOrderDate().isBefore(LocalDateTime.now())) {
            date.setTextColor(getColor(R.color.error_red));
        }

        MaterialCheckBox checkboxCompleted = findViewById(R.id.checkbox_completed);
        checkboxCompleted.setChecked(order.isCompleted());

        checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            order.setCompleted(isChecked);
            dbHandler.updateOrder(order);
        });
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
        getMenuInflater().inflate(R.menu.activity_order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showConfirmDeleteDialog();
                return true;
            case R.id.action_edit:
                Intent editIntent = new Intent(this, EditOrderActivity.class);
                editIntent.putExtra(EditOrderActivity.EXTRA_EDIT_TYPE, EditOrderActivity.EDIT_TYPE_EDIT);
                editIntent.putExtra(EditOrderActivity.EXTRA_ORDER_ID, order.getOrderId());
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

    @Override
    public void onConfirmDelete() {
        boolean success = dbHandler.deleteOrder(order.getOrderId());
        if (success) {
            Toast.makeText(this, R.string.db_delete_order_success, Toast.LENGTH_SHORT).show();
            UndoStack.getInstance().push(new DeleteOrderAction(order));
            finish();
        } else {
            Toast.makeText(this, R.string.db_delete_order_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmDeleteDialog() {
        DialogFragment dialog = new ConfirmDeleteDialog();
        dialog.show(getSupportFragmentManager(), "confirm_delete");
    }
}
