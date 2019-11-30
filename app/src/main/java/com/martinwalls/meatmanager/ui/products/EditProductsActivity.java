package com.martinwalls.meatmanager.ui.products;

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
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.ui.meatTypes.AddNewMeatTypeDialog;
import com.martinwalls.meatmanager.ui.misc.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.misc.RecyclerViewDivider;
import com.martinwalls.meatmanager.ui.misc.SwipeToDeleteCallback;
import com.martinwalls.meatmanager.util.EasyPreferences;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class EditProductsActivity extends AppCompatActivity
        implements AddNewProductDialog.AddNewProductListener,
        AddNewMeatTypeDialog.AddNewMeatTypeListener {

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
//        prefs = EasyPreferences.createForDefaultPreferences(this);
        prefs = EasyPreferences.getInstance(this);

        initProductsList();
        loadProducts();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showAddNewProductDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(this);
        }
        if (prefs == null) {
            prefs = EasyPreferences.getInstance(this);
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
        switch (prefs.getInt(R.string.pref_products_sort_by, SortUtils.SORT_MEAT_TYPE)) {
            case SortUtils.SORT_MEAT_TYPE:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_meat_type).setChecked(true);
                break;
            case SortUtils.SORT_NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_name).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                setSortMode(SortUtils.SORT_NAME);
                return true;
            case R.id.action_sort_by_meat_type:
                setSortMode(SortUtils.SORT_MEAT_TYPE);
                return true;
            case R.id.action_add_meat_type:
                showAddNewMeatTypeDialog();
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

    @Override
    public void onAddNewMeatTypeDoneAction(String meatType) {
        boolean success = dbHandler.addMeatType(meatType);
        if (success) {
            Toast.makeText(this, getString(R.string.add_meat_type_success, meatType),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.add_meat_type_error, meatType),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialises the products list. Doesn't load any data, this is done with
     * {@link #loadProducts()}.
     */
    private void initProductsList() {
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView emptyView = findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        productsAdapter = new ProductsAdapter(productList, this);
        recyclerView.setAdapter(productsAdapter);

        // item dividers
        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(this, R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // swipe to delete action
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToDeleteCallback(productsAdapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Gets all products in the database, sorts them and updates the
     * layout to show the update data.
     */
    private void loadProducts() {
        int sortBy = prefs.getInt(R.string.pref_products_sort_by, SortUtils.SORT_MEAT_TYPE);
        productList.clear();
        productList.addAll(SortUtils.mergeSort(dbHandler.getAllProducts(),
                sortBy == SortUtils.SORT_NAME
                        ? Product.comparatorAlpha()
                        : Product.comparatorMeatType()));
        productsAdapter.notifyDataSetChanged();
    }

    /**
     * Shows an {@link AddNewProductDialog} to allow the user to add a new product.
     */
    private void showAddNewProductDialog() {
        DialogFragment addNewProductDialog = new AddNewProductDialog();
        addNewProductDialog.show(getSupportFragmentManager(), "add_new_product");
    }

    /**
     * Shows an {@link AddNewMeatTypeDialog} to allow the user to add a new
     * meat type.
     */
    private void showAddNewMeatTypeDialog() {
        DialogFragment addNewMeatTypeDialog = new AddNewMeatTypeDialog();
        addNewMeatTypeDialog.show(getSupportFragmentManager(), "add_new_meat_type");
    }

    /**
     * Sets the sort mode for products, and reloads the data.
     */
    private void setSortMode(int sortMode) {
        prefs.setInt(R.string.pref_products_sort_by, sortMode);
        invalidateOptionsMenu();
        loadProducts();
    }
}
