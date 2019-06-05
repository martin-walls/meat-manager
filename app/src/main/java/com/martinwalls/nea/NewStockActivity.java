package com.martinwalls.nea;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class NewStockActivity extends AppCompatActivity {

    private SearchItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemAdapter = new SearchItemAdapter(new ArrayList<String>());

        final TextInputEditText inputProduct = findViewById(R.id.edit_text_product);
        final TextInputLayout inputLayoutProduct = findViewById(R.id.input_layout_product);
        inputLayoutProduct.setEndIconVisible(false);


        inputProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputLayoutProduct.setEndIconVisible(true);
                    inputProductOnClick();
                } else {
                    inputLayoutProduct.setEndIconVisible(false);
                    cancelInputProductOnClick();
                }
            }
        });

        inputLayoutProduct.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputProduct.setText("");
                inputProduct.clearFocus();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inputProductOnClick() {
//        DialogFragment dialog = new SelectItemDialog();
//        dialog.show(getSupportFragmentManager(), "select item dialog");

        TextInputLayout inputLayoutSupplier = findViewById(R.id.input_layout_supplier);
        inputLayoutSupplier.setVisibility(View.GONE);
        LinearLayout inputLayoutQuantity = findViewById(R.id.input_row_quantity);
        inputLayoutQuantity.setVisibility(View.GONE);
        TextInputLayout inputLayoutLocation = findViewById(R.id.input_layout_location);
        inputLayoutLocation.setVisibility(View.GONE);
        TextInputLayout inputLayoutDestination = findViewById(R.id.input_layout_destination);
        inputLayoutDestination.setVisibility(View.GONE);
        TextInputLayout inputLayoutQuality = findViewById(R.id.input_layout_quality);
        inputLayoutQuality.setVisibility(View.GONE);

        ArrayList<String> products = SampleData.getSampleProducts();

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setVisibility(View.VISIBLE);

        TextView noResults = findViewById(R.id.no_results);
        recyclerView.setEmptyView(noResults);

        itemAdapter = new SearchItemAdapter(products);
        recyclerView.setAdapter(itemAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void cancelInputProductOnClick() {
        TextInputLayout inputLayoutSupplier = findViewById(R.id.input_layout_supplier);
        inputLayoutSupplier.setVisibility(View.VISIBLE);
        LinearLayout inputLayoutQuantity = findViewById(R.id.input_row_quantity);
        inputLayoutQuantity.setVisibility(View.VISIBLE);
        TextInputLayout inputLayoutLocation = findViewById(R.id.input_layout_location);
        inputLayoutLocation.setVisibility(View.VISIBLE);
        TextInputLayout inputLayoutDestination = findViewById(R.id.input_layout_destination);
        inputLayoutDestination.setVisibility(View.VISIBLE);
        TextInputLayout inputLayoutQuality = findViewById(R.id.input_layout_quality);
        inputLayoutQuality.setVisibility(View.VISIBLE);

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setVisibility(View.GONE);
    }
}
