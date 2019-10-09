package com.martinwalls.nea.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.util.Utils;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.StockItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StockFragment extends Fragment {

    private final int REQUEST_REFRESH_ON_DONE = 1;

    private DBHandler dbHandler;

    private StockItemAdapter stockAdapter;
    private List<StockItem> stockList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.stock_title);

        View fragmentView = inflater.inflate(R.layout.fragment_stock, container, false);

        dbHandler = new DBHandler(getContext());

        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        stockAdapter = new StockItemAdapter(stockList);
        recyclerView.setAdapter(stockAdapter);
        loadStock();

        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent newStockIntent = new Intent(getContext(), NewStockActivity.class);
            startActivityForResult(newStockIntent, REQUEST_REFRESH_ON_DONE);
        });

        return fragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            loadStock();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(getContext());
        }
        loadStock();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_stock, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_undo:
                Toast.makeText(getContext(), "UNDO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_redo:
                Toast.makeText(getContext(), "REDO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_edit_products:
                Intent productsIntent = new Intent(getContext(), EditProductsActivity.class);
                startActivity(productsIntent);
                return true;
            case R.id.action_edit_locations:
                Intent locationsIntent = new Intent(getContext(), LocationsActivity.class);
                startActivity(locationsIntent);
                return true;
            case R.id.action_edit_meat_types:
                Intent meatTypesIntent = new Intent(getContext(), EditMeatTypesActivity.class);
                startActivity(meatTypesIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadStock() {
        stockList.clear();
        stockList.addAll(Utils.mergeSort(dbHandler.getAllStock(), new Comparator<StockItem>() {
            @Override
            public int compare(StockItem stock1, StockItem stock2) {
                return stock1.getProduct().getProductName().compareTo(stock2.getProduct().getProductName());
            }
        }));
        stockAdapter.notifyDataSetChanged();
    }
}
