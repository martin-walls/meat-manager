package com.martinwalls.nea.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.martinwalls.nea.ConfirmDeleteDialog;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.StockItem;
import com.martinwalls.nea.util.undo.DeleteStockAction;
import com.martinwalls.nea.util.undo.UndoStack;

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.stock_details_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dbHandler = new DBHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int stockId = extras.getInt(EXTRA_STOCK_ID);
            stockItem = dbHandler.getStockItem(stockId);
        }

        TextView productName = findViewById(R.id.product_name);
        productName.setText(stockItem.getProduct().getProductName());

        TextView location = findViewById(R.id.location);
        location.setText(stockItem.getLocationName());

        EditText quantityMass = findViewById(R.id.edit_text_quantity_mass);
        quantityMass.setText(String.valueOf(stockItem.getMass()));

        EditText quantityBoxes = findViewById(R.id.edit_text_quantity_boxes);
        if (stockItem.getNumBoxes() != -1) {
            quantityBoxes.setText(String.valueOf(stockItem.getNumBoxes()));
        }

        TextView supplier = findViewById(R.id.supplier);
        supplier.setText(stockItem.getSupplierName());

        TextView destination = findViewById(R.id.destination);
        destination.setText(stockItem.getDestName());

        TextView quality = findViewById(R.id.quality);
        quality.setText(stockItem.getQuality().getDisplayName());
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
                Intent editIntent = new Intent(this, EditStockActivity.class);
                editIntent.putExtra(EditStockActivity.EXTRA_EDIT_TYPE, EditStockActivity.EDIT_TYPE_EDIT);
                editIntent.putExtra(EditStockActivity.EXTRA_STOCK_ID, stockItem.getStockId());
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
        boolean success = dbHandler.deleteStockItem(stockItem.getStockId());
        if (success) {
            Toast.makeText(this, getString(R.string.db_delete_stock_success,
                    stockItem.getProduct().getProductName(), stockItem.getLocationName()), Toast.LENGTH_SHORT).show();
            UndoStack.getInstance().push(new DeleteStockAction(stockItem));
            finish();
        } else {
            Toast.makeText(this, getString(R.string.db_error_delete, stockItem.getProduct().getProductName()),
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showConfirmDeleteDialog() {
        DialogFragment dialog = new ConfirmDeleteDialog();
        Bundle args = new Bundle();
        args.putString(ConfirmDeleteDialog.EXTRA_NAME, stockItem.getProduct().getProductName());
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "confirm_delete");
    }
}
