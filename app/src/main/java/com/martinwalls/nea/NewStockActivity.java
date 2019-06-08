package com.martinwalls.nea;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class NewStockActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener {

    private HashMap<String, Integer> inputViews = new HashMap<>();

    private SearchItemAdapter itemAdapter;
    private ArrayList<String> searchResultList = new ArrayList<>();
    private LinearLayout searchResultsLayout;
    private AddNewTextView addNewView;

    private final String INPUT_PRODUCT = "product";
    private final String INPUT_SUPPLIER = "supplier";
    private final String INPUT_LOCATION = "location";
    private final String INPUT_DESTINATION = "destination";
    private final String INPUT_QUALITY = "quality";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        // store reference to each row to show/hide later
        inputViews.put("product", R.id.input_layout_product);
        inputViews.put("supplier", R.id.input_layout_supplier);
        inputViews.put("quantity", R.id.input_row_quantity);
        inputViews.put("location", R.id.input_layout_location);
        inputViews.put("destination", R.id.input_layout_destination);
        inputViews.put("quality", R.id.input_layout_quality);

        addNewView = findViewById(R.id.add_new);
        addNewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem((AddNewTextView) v);
            }
        });

        // setup recycler view
        itemAdapter = new SearchItemAdapter(searchResultList, this, this);
        TextView emptyView = findViewById(R.id.no_results);
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view_results);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setEmptyView(emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        searchResultsLayout = findViewById(R.id.search_results_layout);


        setListeners(INPUT_PRODUCT,
                (TextInputLayout) findViewById(R.id.input_layout_product),
                (TextInputEditText) findViewById(R.id.edit_text_product));

        setListeners(INPUT_SUPPLIER,
                (TextInputLayout) findViewById(R.id.input_layout_supplier),
                (TextInputEditText) findViewById(R.id.edit_text_supplier));

        setListeners(INPUT_LOCATION,
                (TextInputLayout) findViewById(R.id.input_layout_location),
                (TextInputEditText) findViewById(R.id.edit_text_location));

        setListeners(INPUT_DESTINATION,
                (TextInputLayout) findViewById(R.id.input_layout_destination),
                (TextInputEditText) findViewById(R.id.edit_text_destination));

        setListeners(INPUT_QUALITY,
                (TextInputLayout) findViewById(R.id.input_layout_quality),
                (TextInputEditText) findViewById(R.id.edit_text_quality));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                //todo handle done action
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case R.id.action_cancel:
                //todo confirm cancel
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
        addNewView.setVisibility(rowName.equals(INPUT_QUALITY) ? View.GONE : View.VISIBLE);
        addNewView.setText(getString(R.string.search_add_new, rowName));
        addNewView.setSearchItemType(rowName);

        for (Integer view : inputViews.values()) {
            if (!view.equals(inputViews.get(rowName))) {
                findViewById(view).setVisibility(View.GONE);
            }
        }

        searchResultList.clear();
        switch (rowName) {
            case INPUT_PRODUCT:
                searchResultList.addAll(SampleData.getSampleProducts());
                break;
            case INPUT_SUPPLIER:
            case INPUT_DESTINATION:
            case INPUT_LOCATION:
                searchResultList.addAll(SampleData.getSampleLocations());
                break;
            case INPUT_QUALITY:
                searchResultList.addAll(SampleData.getSampleQualities());
                break;
        }
        itemAdapter.notifyDataSetChanged();

        // filter data at start for when text is already entered
        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        itemAdapter.getFilter().filter(editText.getText());

        searchResultsLayout.setVisibility(View.VISIBLE);
    }

    private void cancelSearch() {
        for (int view : inputViews.values()) {
            findViewById(view).setVisibility(View.VISIBLE);
        }

        searchResultsLayout.setVisibility(View.GONE);

        searchResultList.clear();
        itemAdapter.notifyDataSetChanged();

        hideKeyboard();
    }

    private void addNewItem(AddNewTextView textView) {
        String searchItemType = textView.getSearchItemType();
        DialogFragment dialog = new AddNewProductDialog();
        switch (searchItemType) {
            case INPUT_PRODUCT:
                dialog.show(getSupportFragmentManager(), "add_new_product");
                break;
            default:
                Toast.makeText(this, searchItemType, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = findViewById(R.id.root_layout);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onItemSelected(String name) {
        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        editText.setText(name);
        editText.clearFocus();
        cancelSearch();
    }
}
