package com.martinwalls.nea;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class NewStockActivity extends AppCompatActivity {

    private HashMap<String, Integer> inputViews = new HashMap<>();

    private SearchItemAdapter itemAdapter;
    private ArrayList<String> searchResultList = new ArrayList<>();
    private TextView emptyView;
    private CustomRecyclerView recyclerView;
    private RelativeLayout recyclerViewWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputViews.put("product", R.id.input_layout_product);
        inputViews.put("supplier", R.id.input_layout_supplier);
        inputViews.put("quantity", R.id.input_row_quantity);
        inputViews.put("location", R.id.input_layout_location);
        inputViews.put("destination", R.id.input_layout_destination);
        inputViews.put("quality", R.id.input_layout_quality);

        // setup recycler view
        itemAdapter = new SearchItemAdapter(searchResultList);
        emptyView = findViewById(R.id.no_results);
        recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setEmptyView(emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewWrapper = findViewById(R.id.recycler_view_wrapper);


        final TextInputEditText inputProduct = findViewById(R.id.edit_text_product);
        final TextInputLayout inputLayoutProduct = findViewById(R.id.input_layout_product);
        inputLayoutProduct.setEndIconVisible(false);


        inputProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputLayoutProduct.setEndIconVisible(true);
                    inputOnClick("product");
                }
            }
        });

        inputLayoutProduct.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputProduct.setText("");
                inputProduct.clearFocus();
                inputLayoutProduct.setEndIconVisible(false);
                cancelInputProductOnClick();
            }
        });

        inputProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inputOnClick(String rowName) {

        for (String view : inputViews.keySet()) {
            if (!view.equals(rowName)) {
                //noinspection ConstantConditions
                findViewById(inputViews.get(view)).setVisibility(View.GONE);
            }
        }

        searchResultList.clear();
        switch (rowName) {
            case "product":
                searchResultList.addAll(SampleData.getSampleProducts());
            case "location":
                searchResultList.addAll(SampleData.getSampleLocations());
        }
        itemAdapter.notifyDataSetChanged();

        recyclerViewWrapper.setVisibility(View.VISIBLE);
    }

    private void cancelInputProductOnClick() {
        for (int view : inputViews.values()) {
            findViewById(view).setVisibility(View.VISIBLE);
        }

        recyclerViewWrapper.setVisibility(View.GONE);
    }
}
