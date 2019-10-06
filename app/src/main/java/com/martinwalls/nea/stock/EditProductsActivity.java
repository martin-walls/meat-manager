package com.martinwalls.nea.stock;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.AddNewProductDialog;
import com.martinwalls.nea.BaseActivity;
import com.martinwalls.nea.EasyPreferences;
import com.martinwalls.nea.R;
import com.martinwalls.nea.Utils;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.components.SwipeToDeleteCallback;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Product;

import java.util.ArrayList;
import java.util.List;

public class EditProductsActivity extends BaseActivity
        implements AddNewProductDialog.AddNewProductListener {

    private final int SORT_BY_NAME = 0;
    private final int SORT_BY_MEAT_TYPE = 1;

    private DBHandler dbHandler;
    private EasyPreferences preferences;

    private ProductsAdapter productsAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);

        getSupportActionBar().setTitle(R.string.products_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);
        preferences = EasyPreferences.createForDefaultPreferences(this);

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
        switch (preferences.getInt(R.string.pref_products_sort_by, SORT_BY_MEAT_TYPE)) {
            case SORT_BY_MEAT_TYPE:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_meat_type).setChecked(true);
                break;
            case SORT_BY_NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_name).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                preferences.setInt(R.string.pref_products_sort_by, SORT_BY_NAME);
                invalidateOptionsMenu();
                loadProducts();
                return true;
            case R.id.action_sort_by_meat_type:
                preferences.setInt(R.string.pref_products_sort_by, SORT_BY_MEAT_TYPE);
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
        dbHandler.addProduct(newProduct);
        loadProducts();
    }

    private void loadProducts() {
        int sortBy = preferences.getInt(R.string.pref_products_sort_by, SORT_BY_MEAT_TYPE);
        productList.clear();
        productList.addAll(Utils.mergeSort(dbHandler.getAllProducts(),
                sortBy == SORT_BY_NAME ? Product.comparatorAlpha() : Product.comparatorMeatType()));
        productsAdapter.notifyDataSetChanged();
    }

    //todo be able to edit products ?-- may not be needed, can just delete and re-add
}
