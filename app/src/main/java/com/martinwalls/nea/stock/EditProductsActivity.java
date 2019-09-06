package com.martinwalls.nea.stock;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.AddNewProductDialog;
import com.martinwalls.nea.R;
import com.martinwalls.nea.Utils;
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

    private ProductsAdapter productsAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);

        getSupportActionBar().setTitle(R.string.products_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);

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
    protected void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(this);
        }
        loadProducts();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProducts() {
        String sortBy = "meatType"; //todo shared preferences
        productList.clear();
        productList.addAll(Utils.mergeSort(dbHandler.getAllProducts(),
                sortBy == "name" ? Product.comparatorAlpha() : Product.comparatorMeatType()));
        productsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddNewProductDoneAction(Product newProduct) {
        dbHandler.addProduct(newProduct);
        loadProducts();
    }

    //todo be able to edit products ?-- may not be needed, can just delete and re-add
}
