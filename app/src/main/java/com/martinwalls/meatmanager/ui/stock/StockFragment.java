package com.martinwalls.meatmanager.ui.stock;

import android.app.SearchManager;
import android.content.Context;
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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.ui.locations.LocationsActivity;
import com.martinwalls.meatmanager.ui.meatTypes.EditMeatTypesActivity;
import com.martinwalls.meatmanager.ui.misc.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.misc.RecyclerViewDivider;
import com.martinwalls.meatmanager.ui.products.EditProductsActivity;
import com.martinwalls.meatmanager.util.EasyPreferences;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.undo.UndoStack;

public class StockFragment extends Fragment
        implements StockItemAdapter.StockItemAdapterListener {

    private final int REQUEST_REFRESH_ON_DONE = 1;

    private final int SORT_BY_DEFAULT = SortUtils.SORT_NAME;

    private EasyPreferences prefs;
    private StockViewModel viewModel;
    private StockItemAdapter stockAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.stock_title);

        View fragmentView = inflater.inflate(R.layout.fragment_stock, container, false);

        prefs = EasyPreferences.getInstance(getContext());

        // initialise RecyclerView
        initStockList(fragmentView);

        viewModel = ViewModelProviders.of(this).get(StockViewModel.class);

        viewModel.getStockListObservable().observe(getViewLifecycleOwner(),
                stockItems -> stockAdapter.setStockList(stockItems));

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startNewStockActivity());

        return fragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            viewModel.loadStock();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs == null) {
            prefs = EasyPreferences.getInstance(getContext());
        }
        viewModel.loadStock();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_stock, menu);

        //setup search bar
        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

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
            // undo / redo
            case R.id.action_undo:
                UndoStack.getInstance().undo(getContext());
                viewModel.loadStock();
                return true;
            case R.id.action_redo:
                UndoStack.getInstance().redo(getContext());
                viewModel.loadStock();
                return true;
            // products, locations, meat types pages
            case R.id.action_edit_products:
                startEditProductsActivity();
                return true;
            case R.id.action_edit_locations:
                startLocationsActivity();
                return true;
            case R.id.action_edit_meat_types:
                startEditMeatTypesActivity();
                return true;
            // sort options
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
     * Initialises the RecyclerView to show stock.
     */
    private void initStockList(View fragmentView) {
        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        stockAdapter = new StockItemAdapter(this);
        recyclerView.setAdapter(stockAdapter);

        // item dividers
        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Stores which property to sort stock by, then reloads the data.
     */
    private void setSortMode(int sortMode) {
        prefs.setInt(R.string.pref_stock_sort_by, sortMode);
        getActivity().invalidateOptionsMenu();
        viewModel.sortStock(sortMode);
    }

    /**
     * Starts {@link EditStockActivity} to allow the user to add a new stock item.
     */
    private void startNewStockActivity() {
        Intent newStockIntent = new Intent(getContext(), EditStockActivity.class);
        startActivityForResult(newStockIntent, REQUEST_REFRESH_ON_DONE);
    }

    /**
     * Starts {@link EditProductsActivity} to allow the user to view and edit
     * products.
     */
    private void startEditProductsActivity() {
        Intent productsIntent = new Intent(getContext(), EditProductsActivity.class);
        startActivity(productsIntent);
    }

    /**
     * Starts {@link LocationsActivity} to allow the user to view and edit locations.
     */
    private void startLocationsActivity() {
        Intent locationsIntent = new Intent(getContext(), LocationsActivity.class);
        startActivity(locationsIntent);
    }

    /**
     * Starts {@link EditMeatTypesActivity} to allow the user to view and edit
     * meat types.
     */
    private void startEditMeatTypesActivity() {
        Intent meatTypesIntent = new Intent(getContext(), EditMeatTypesActivity.class);
        startActivity(meatTypesIntent);
    }
}
