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
import java.util.Objects;

public class NewStockActivity extends AppCompatActivity {

    private HashMap<String, Integer> inputViews = new HashMap<>();

    private SearchItemAdapter itemAdapter;
    private ArrayList<String> searchResultList = new ArrayList<>();
    private RelativeLayout searchResultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // store reference to each row to show/hide later
        inputViews.put("product", R.id.input_layout_product);
        inputViews.put("supplier", R.id.input_layout_supplier);
        inputViews.put("quantity", R.id.input_row_quantity);
        inputViews.put("location", R.id.input_layout_location);
        inputViews.put("destination", R.id.input_layout_destination);
        inputViews.put("quality", R.id.input_layout_quality);

        // setup recycler view
        itemAdapter = new SearchItemAdapter(searchResultList);
        TextView emptyView = findViewById(R.id.no_results);
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setEmptyView(emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        searchResultsLayout = findViewById(R.id.search_results_layout);


        // todo add new product option at top of list - show what the user is typing with add new option if not exists



        setListeners("product",
                (TextInputLayout) findViewById(R.id.input_layout_product),
                (TextInputEditText) findViewById(R.id.edit_text_product));

        setListeners("supplier",
                (TextInputLayout) findViewById(R.id.input_layout_supplier),
                (TextInputEditText) findViewById(R.id.edit_text_supplier));

        setListeners("location",
                (TextInputLayout) findViewById(R.id.input_layout_location),
                (TextInputEditText) findViewById(R.id.edit_text_location));

        setListeners("destination",
                (TextInputLayout) findViewById(R.id.input_layout_destination),
                (TextInputEditText) findViewById(R.id.edit_text_destination));

        setListeners("quality",
                (TextInputLayout) findViewById(R.id.input_layout_quality),
                (TextInputEditText) findViewById(R.id.edit_text_quality));
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

    private void setListeners(final String name, final TextInputLayout inputLayout, final TextInputEditText editText) {
        inputLayout.setEndIconVisible(false);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputLayout.setEndIconVisible(true);
                    openSearch(name);
                }
            }
        });

        inputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                editText.clearFocus();
                inputLayout.setEndIconVisible(false);
                cancelSearch();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void openSearch(String rowName) {

        for (Integer view : inputViews.values()) {
            if (!view.equals(inputViews.get(rowName))) {
                findViewById(view).setVisibility(View.GONE);
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

        searchResultsLayout.setVisibility(View.VISIBLE);
    }

    private void cancelSearch() {
        for (int view : inputViews.values()) {
            findViewById(view).setVisibility(View.VISIBLE);
        }

        searchResultsLayout.setVisibility(View.GONE);
    }
}
