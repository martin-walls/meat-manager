package com.martinwalls.nea.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Order;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.data.models.RelatedStock;
import com.martinwalls.nea.data.models.StockItem;
import com.martinwalls.nea.ui.ProductsAddedAdapter;
import com.martinwalls.nea.ui.RelatedStockAdapter;
import com.martinwalls.nea.ui.misc.dialog.ConfirmDeleteDialog;
import com.martinwalls.nea.ui.stock.StockDetailActivity;
import com.martinwalls.nea.util.SortUtils;
import com.martinwalls.nea.util.undo.UndoStack;
import com.martinwalls.nea.util.undo.orders.DeleteOrderAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity
        implements ConfirmDeleteDialog.ConfirmDeleteListener,
        RelatedStockAdapter.RelatedStockListener {

    public static final String EXTRA_ORDER_ID = "order_id";

    private static final int REQUEST_REFRESH_ON_DONE = 1;

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

        ProductsAddedAdapter productsAdapter =
                new ProductsAddedAdapter(order.getProductList());
        RecyclerView productsRecyclerView = findViewById(R.id.recycler_view_products);
        productsRecyclerView.setAdapter(productsAdapter);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        initStockForOrder();

        TextView stockSectionTitle = findViewById(R.id.stock_section_title);
        stockSectionTitle.setText(R.string.order_current_stock);

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
                editIntent.putExtra(EditOrderActivity.EXTRA_EDIT_TYPE,
                        EditOrderActivity.EDIT_TYPE_EDIT);
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
     * Initialises list of related stock for the {@link Order} being shown.
     */
    private void initStockForOrder() {
        List<StockItem> stockForOrder = SortUtils.mergeSort(
                dbHandler.getAllStockForOrder(order.getOrderId()),
                StockItem.comparatorLocation());

        // get list of related stock by product, with child stock items for each location
        List<RelatedStock> relatedStockList = new ArrayList<>();
        int thisProductId;
        int lastProductId = -1;
        for (StockItem stockItem : stockForOrder) {
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
        for (ProductQuantity productQuantity : order.getProductList()) {
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

        RelatedStockAdapter stockAdapter = new RelatedStockAdapter(relatedStockList, this);
        RecyclerView stockRecyclerView = findViewById(R.id.recycler_view_stock);
        stockRecyclerView.setAdapter(stockAdapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Initialises fields with data from the {@link Order} being shown.
     */
    private void fillFields() {
        TextView destination = findViewById(R.id.destination);
        destination.setText(order.getDestName());

        TextView date = findViewById(R.id.date);
        date.setText(order.getOrderDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));

        if (order.getOrderDate().isBefore(LocalDateTime.now())) {
            date.setTextColor(getColor(R.color.error_red));
        }

        CheckBox checkboxCompleted = findViewById(R.id.checkbox_completed);
        checkboxCompleted.setChecked(order.isCompleted());

        checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            order.setCompleted(isChecked);
            dbHandler.updateOrder(order);
        });
    }
}
