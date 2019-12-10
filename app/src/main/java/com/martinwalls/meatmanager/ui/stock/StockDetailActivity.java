package com.martinwalls.meatmanager.ui.stock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.ui.locations.LocationDetailActivity;
import com.martinwalls.meatmanager.ui.common.dialog.ConfirmDeleteDialog;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.Utils;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.stock.DeleteStockAction;

public class StockDetailActivity extends AppCompatActivity 
        implements ConfirmDeleteDialog.ConfirmDeleteListener {

    public static final String EXTRA_STOCK_ID = "stock_id";

    public static final int REQUEST_REFRESH_ON_DONE = 1;

    private DBHandler dbHandler;
    private StockItem stockItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        getSupportActionBar().setTitle(R.string.stock_details_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int stockId = extras.getInt(EXTRA_STOCK_ID);
            stockItem = dbHandler.getStockItem(stockId);
        }

        TextView location = findViewById(R.id.location);
        LinearLayout supplier = findViewById(R.id.row_supplier);
        LinearLayout destination = findViewById(R.id.row_destination);

        location.setOnClickListener(v -> openLocationDetailPage(stockItem.getLocationId()));
        supplier.setOnClickListener(v -> openLocationDetailPage(stockItem.getSupplierId()));
        destination.setOnClickListener(v -> openLocationDetailPage(stockItem.getDestId()));

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
        getMenuInflater().inflate(R.menu.activity_stock_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showConfirmDeleteDialog();
                return true;
            case R.id.action_edit:
                startEditStockActivity();
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
        boolean success = dbHandler.deleteStockItem(stockItem.getStockId());
        if (success) {
            Toast.makeText(this,
                    getString(R.string.db_delete_stock_success,
                            stockItem.getProduct().getProductName(),
                            stockItem.getLocationName()),
                    Toast.LENGTH_SHORT).show();
            UndoStack.getInstance().push(new DeleteStockAction(stockItem));
            finish();
        } else {
            Toast.makeText(this,
                    getString(R.string.db_error_delete,
                            stockItem.getProduct().getProductName()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialises fields with data from the {@link StockItem} being shown.
     */
    private void fillData() {
        TextView productName = findViewById(R.id.product_name);
        productName.setText(stockItem.getProduct().getProductName());

        TextView location = findViewById(R.id.location);
        location.setText(stockItem.getLocationName());

        TextView quantityMass = findViewById(R.id.edit_text_quantity_mass);
        MassUnit massUnit = MassUnit.getMassUnit(this);

        quantityMass.setText(getString(
                massUnit == MassUnit.KG
                        ? R.string.amount_kg
                        : R.string.amount_lbs,
                Utils.getMassDisplayValue(this, stockItem.getMass(), 3)));

        TextView quantityBoxes = findViewById(R.id.edit_text_quantity_boxes);
        LinearLayout rowBoxes = findViewById(R.id.row_boxes);
        if (stockItem.getNumBoxes() != -1) {
            quantityBoxes.setText(getResources().getQuantityString(R.plurals.amount_boxes,
                    stockItem.getNumBoxes(), stockItem.getNumBoxes()));
            rowBoxes.setVisibility(View.VISIBLE);
        } else {
            rowBoxes.setVisibility(View.GONE);
        }

        TextView supplier = findViewById(R.id.supplier);
        supplier.setText(stockItem.getSupplierName());

        TextView destination = findViewById(R.id.destination);
        LinearLayout rowDestination = findViewById(R.id.row_destination);
        if (!TextUtils.isEmpty(stockItem.getDestName())) {
            destination.setText(stockItem.getDestName());
            rowDestination.setVisibility(View.VISIBLE);
        } else {
            rowDestination.setVisibility(View.GONE);
        }

        TextView quality = findViewById(R.id.quality);
        quality.setText(stockItem.getQuality().getDisplayName());
    }

    /**
     * Shows a {@link ConfirmDeleteDialog} asking the user to confirm the
     * delete action.
     */
    private void showConfirmDeleteDialog() {
        DialogFragment dialog = new ConfirmDeleteDialog();
        Bundle args = new Bundle();
        args.putString(ConfirmDeleteDialog.EXTRA_NAME,
                stockItem.getProduct().getProductName());
        dialog.setArguments(args);
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
     * Opens {@link EditStockActivity} to allow the user to edit this stock item.
     */
    private void startEditStockActivity() {
        Intent editIntent = new Intent(this, EditStockActivity.class);
        editIntent.putExtra(EditStockActivity.EXTRA_EDIT_TYPE,
                EditStockActivity.EDIT_TYPE_EDIT);
        editIntent.putExtra(EditStockActivity.EXTRA_STOCK_ID, stockItem.getStockId());
        startActivityForResult(editIntent, REQUEST_REFRESH_ON_DONE);
    }
}
