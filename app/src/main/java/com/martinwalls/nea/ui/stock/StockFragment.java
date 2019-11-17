package com.martinwalls.nea.ui.stock;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.StockItem;
import com.martinwalls.nea.ui.locations.LocationsActivity;
import com.martinwalls.nea.ui.meatTypes.EditMeatTypesActivity;
import com.martinwalls.nea.ui.misc.CustomRecyclerView;
import com.martinwalls.nea.ui.misc.RecyclerViewDivider;
import com.martinwalls.nea.ui.products.EditProductsActivity;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.SortUtils;
import com.martinwalls.nea.util.undo.UndoStack;

import java.util.ArrayList;
import java.util.List;

public class StockFragment extends Fragment
        implements StockItemAdapter.StockItemAdapterListener {

    private final int REQUEST_REFRESH_ON_DONE = 1;

    private final int SORT_BY_DEFAULT = SortUtils.SORT_NAME;

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

        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent newStockIntent = new Intent(getContext(), EditStockActivity.class);
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

        //setup search bar
        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                stockAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                stockAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        switch (prefs.getInt(R.string.pref_stock_sort_by, SORT_BY_DEFAULT)) {
            case SortUtils.SORT_NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_name).setChecked(true);
                break;
            case SortUtils.SORT_LOCATION:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_location).setChecked(true);
                break;
            case SortUtils.SORT_AMOUNT_DESC:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_amount_desc).setChecked(true);
                break;
            case SortUtils.SORT_AMOUNT_ASC:
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
                setSortMode(SortUtils.SORT_NAME);
                return true;
            case R.id.action_sort_by_location:
                setSortMode(SortUtils.SORT_LOCATION);
                return true;
            case R.id.action_sort_by_amount_desc:
                setSortMode(SortUtils.SORT_AMOUNT_DESC);
                return true;
            case R.id.action_sort_by_amount_asc:
                setSortMode(SortUtils.SORT_AMOUNT_ASC);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStockItemClicked(StockItem stockItem) {
        Intent detailIntent = new Intent(getContext(), StockDetailActivity.class);
        detailIntent.putExtra(StockDetailActivity.EXTRA_STOCK_ID, stockItem.getStockId());
        startActivity(detailIntent);
    }

    /**
     * Stores which property to sort stock by, then reloads the data.
     */
    private void setSortMode(int sortMode) {
        prefs.setInt(R.string.pref_stock_sort_by, sortMode);
        getActivity().invalidateOptionsMenu();
        loadStock();
    }

    /**
     * Gets all stock items from the database, sorts them and reloads the
     * layout to show the updated data.
     */
    private void loadStock() {
        stockList.clear();
        List<StockItem> stock = dbHandler.getAllStock();
        switch (prefs.getInt(R.string.pref_stock_sort_by, SORT_BY_DEFAULT)) {
            case SortUtils.SORT_NAME:
                stockList.addAll(SortUtils.mergeSort(stock, StockItem.comparatorAlpha()));
                break;
            case SortUtils.SORT_LOCATION:
                stockList.addAll(SortUtils.mergeSort(stock, StockItem.comparatorLocation()));
                break;
            case SortUtils.SORT_AMOUNT_DESC:
                stockList.addAll(SortUtils.mergeSort(
                        stock, StockItem.comparatorAmount(false)));
                break;
            case SortUtils.SORT_AMOUNT_ASC:
                stockList.addAll(SortUtils.mergeSort(
                        stock, StockItem.comparatorAmount(true)));
                break;
        }
        stockAdapter.notifyDataSetChanged();
    }
}
