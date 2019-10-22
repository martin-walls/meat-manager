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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.StockItem;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.SortMode;
import com.martinwalls.nea.util.Utils;
import com.martinwalls.nea.util.undo.UndoStack;

import java.util.ArrayList;
import java.util.List;

public class StockFragment extends Fragment
        implements StockItemAdapter.StockItemAdapterListener {

    private final int REQUEST_REFRESH_ON_DONE = 1;

    private final int SORT_BY_DEFAULT = SortMode.NAME;

    private DBHandler dbHandler;
    private EasyPreferences prefs;

    private StockItemAdapter stockAdapter;
    private List<StockItem> stockList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.stock_title);

        View fragmentView = inflater.inflate(R.layout.fragment_stock, container, false);

        dbHandler = new DBHandler(getContext());
        prefs = EasyPreferences.createForDefaultPreferences(getContext());

        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        stockAdapter = new StockItemAdapter(stockList, this);
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        switch (prefs.getInt(R.string.pref_stock_sort_by, SORT_BY_DEFAULT)) {
            case SortMode.NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_name).setChecked(true);
                break;
            case SortMode.LOCATION:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_location).setChecked(true);
                break;
            case SortMode.AMOUNT_DESC:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_amount_desc).setChecked(true);
                break;
            case SortMode.AMOUNT_ASC:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_amount_asc).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                UndoStack.getInstance().undo(getContext());
                loadStock();
                return true;
            case R.id.action_redo:
                UndoStack.getInstance().redo(getContext());
                loadStock();
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
            case R.id.action_sort_by_name:
                setSortMode(SortMode.NAME);
                return true;
            case R.id.action_sort_by_location:
                setSortMode(SortMode.LOCATION);
                return true;
            case R.id.action_sort_by_amount_desc:
                setSortMode(SortMode.AMOUNT_DESC);
                return true;
            case R.id.action_sort_by_amount_asc:
                setSortMode(SortMode.AMOUNT_ASC);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStockItemClicked(StockItem stockItem) {
        Intent detailIntent = new Intent(getContext(), StockDetailActivity.class);
        detailIntent.putExtra(StockDetailActivity.EXTRA_STOCK_ID, stockItem.getStockId());
        startActivity(detailIntent); //todo startActivityForResult?
    }

    private void setSortMode(int sortMode) {
        prefs.setInt(R.string.pref_stock_sort_by, sortMode);
        getActivity().invalidateOptionsMenu();
        loadStock();
    }

    private void loadStock() {
        stockList.clear();
        List<StockItem> stock = dbHandler.getAllStock();
        switch (prefs.getInt(R.string.pref_stock_sort_by, SORT_BY_DEFAULT)) {
            case SortMode.NAME:
                stockList.addAll(Utils.mergeSort(stock, StockItem.comparatorAlpha()));
                break;
            case SortMode.LOCATION:
                stockList.addAll(Utils.mergeSort(stock, StockItem.comparatorLocation()));
                break;
            case SortMode.AMOUNT_DESC:
                stockList.addAll(Utils.mergeSort(stock, StockItem.comparatorAmount(false)));
                break;
            case SortMode.AMOUNT_ASC:
                stockList.addAll(Utils.mergeSort(stock, StockItem.comparatorAmount(true)));
                break;
        }
        stockAdapter.notifyDataSetChanged();
    }
}
