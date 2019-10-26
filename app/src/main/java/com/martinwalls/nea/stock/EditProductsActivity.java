package com.martinwalls.nea.stock;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.AddNewProductDialog;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.R;
import com.martinwalls.nea.util.SortMode;
import com.martinwalls.nea.util.Utils;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.components.SwipeToDeleteCallback;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Product;

import java.util.ArrayList;
import java.util.List;

public class EditProductsActivity extends AppCompatActivity
        implements AddNewProductDialog.AddNewProductListener {

    private DBHandler dbHandler;
    private EasyPreferences prefs;

    private ProductsAdapter productsAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);

        getSupportActionBar().setTitle(R.string.products_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);
        prefs = EasyPreferences.createForDefaultPreferences(this);

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView emptyView = findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        productsAdapter = new ProductsAdapter(productList, this);
        recyclerView.setAdapter(productsAdapter);
        loadProducts();

        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(this, R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(productsAdapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            DialogFragment addNewProductDialog = new AddNewProductDialog();
            addNewProductDialog.show(getSupportFragmentManager(), "add_new_product");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(this);
        }
        loadProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_products, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        switch (prefs.getInt(R.string.pref_products_sort_by, SortMode.MEAT_TYPE)) {
            case SortMode.MEAT_TYPE:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_meat_type).setChecked(true);
                break;
            case SortMode.NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_name).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                prefs.setInt(R.string.pref_products_sort_by, SortMode.NAME);
                invalidateOptionsMenu();
                loadProducts();
                return true;
            case R.id.action_sort_by_meat_type:
                prefs.setInt(R.string.pref_products_sort_by, SortMode.MEAT_TYPE);
                invalidateOptionsMenu();
                loadProducts();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAddNewProductDoneAction(Product newProduct) {
        boolean success = dbHandler.addProduct(newProduct);
        loadProducts();
        if (!success) {
            Toast.makeText(this, "Error adding product", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProducts() {
        int sortBy = prefs.getInt(R.string.pref_products_sort_by, SortMode.MEAT_TYPE);
        productList.clear();
        productList.addAll(Utils.mergeSort(dbHandler.getAllProducts(),
                sortBy == SortMode.NAME ? Product.comparatorAlpha() : Product.comparatorMeatType()));
        productsAdapter.notifyDataSetChanged();
    }

    //todo not urgent: be able to edit products ? -- may not be needed, can just delete and re-add
}
