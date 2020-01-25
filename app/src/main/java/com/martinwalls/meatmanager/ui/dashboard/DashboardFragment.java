package com.martinwalls.meatmanager.ui.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.data.models.ProductQuantity;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.ui.common.recyclerview.RecyclerViewMargin;
import com.martinwalls.meatmanager.util.EasyPreferences;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment
        implements LocationsMenuAdapter.LocationsMenuAdapterListener {

    private final int SORT_BY_DEFAULT = SortUtils.SORT_AMOUNT_DESC;

    private DBHandler dbHandler;
    private EasyPreferences prefs;

    private BarChartView graphView;
    private TextView emptyView;
    private ScrollView graphLayout;

    private Location filterLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.dashboard_title);
        View fragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHandler = new DBHandler(getContext());
        prefs = EasyPreferences.getInstance(getContext());

        if (filterLocation == null || !TextUtils.isEmpty(filterLocation.getLocationName())) {
            filterLocation = new Location();
        }

        initLocationFilterMenu(fragmentView);

        graphView = fragmentView.findViewById(R.id.graph);
        emptyView = fragmentView.findViewById(R.id.empty);
        graphLayout = fragmentView.findViewById(R.id.graph_layout);

        loadData();

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_dashboard, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        switch (prefs.getInt(R.string.pref_dashboard_sort_by, SORT_BY_DEFAULT)) {
            case SortUtils.SORT_NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_name).setChecked(true);
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
            case R.id.action_sort_by_name:
                setSortMode(SortUtils.SORT_NAME);
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
    public void onLocationItemClicked(Location location) {
        filterLocation = location;
        loadData();
    }

    /**
     * Sets which property to sort stock by, then reloads the data.
     */
    private void setSortMode(int sortMode) {
        prefs.setInt(R.string.pref_dashboard_sort_by, sortMode);
        getActivity().invalidateOptionsMenu();
        loadData();
    }

    /**
     * Initialises menu to allow the user to filter by location.
     */
    private void initLocationFilterMenu(View fragmentView) {
        RecyclerView locationsRecyclerView =
                fragmentView.findViewById(R.id.recycler_view_locations);
        LocationsMenuAdapter locationsAdapter = new LocationsMenuAdapter(
                dbHandler.getAllStorageLocationsWithStock(), this);
        locationsRecyclerView.setAdapter(locationsAdapter);

        RecyclerViewMargin margins = new RecyclerViewMargin(
                Utils.convertDpToPixelSize(16, getContext()), RecyclerViewMargin.HORIZONTAL);
        locationsRecyclerView.addItemDecoration(margins);

        locationsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    /**
     * Gets all stock from the database, sorts it and updates the bar chart
     * to show the updated data.
     */
    private void loadData() {
        // get data from db and sort it
        List<StockItem> stockList;
        if (filterLocation == null || TextUtils.isEmpty(filterLocation.getLocationName())) {
            stockList = SortUtils.mergeSort(
                    dbHandler.getAllStockByProduct(), StockItem.comparatorId());
        } else {
            stockList = SortUtils.mergeSort(
                    dbHandler.getAllStockFromLocationByProduct(filterLocation.getLocationId()),
                    StockItem.comparatorId());
        }

        // handle case for no data
        showEmptyView(stockList.size() == 0);

        // sort data by chosen sort mode
        switch (prefs.getInt(R.string.pref_dashboard_sort_by, SORT_BY_DEFAULT)) {
            case SortUtils.SORT_NAME:
                stockList = SortUtils.mergeSort(stockList, StockItem.comparatorProductAlpha());
                break;
            case SortUtils.SORT_AMOUNT_DESC:
                stockList = SortUtils.mergeSort(stockList, StockItem.comparatorAmount(false));
                break;
            case SortUtils.SORT_AMOUNT_ASC:
                stockList = SortUtils.mergeSort(stockList, StockItem.comparatorAmount(true));
                break;
        }

        List<ProductQuantity> productsRequiredList = dbHandler.getAllProductsRequired();

        // get data to pass to the chart view
        List<BarChartEntry> entries = getChartData(stockList, productsRequiredList);
        graphView.setData(entries);
    }

    /**
     * Shows/hides the empty view.
     */
    private void showEmptyView(boolean showEmptyView) {
        if (showEmptyView) {
            emptyView.setVisibility(View.VISIBLE);
            graphLayout.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            graphLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Gets a list of {@link BarChartEntry} objects to pass to the chart view
     * from a list of stock items and required products.
     */
    private List<BarChartEntry> getChartData(List<StockItem> stockList,
                                             List<ProductQuantity> productsRequiredList) {
        List<BarChartEntry> entries = new ArrayList<>();

        for (StockItem stockItem : stockList) {
            int productId = stockItem.getProduct().getProductId();
            float amountRequired = 0;
            for (ProductQuantity productRequired : productsRequiredList) {
                if (productRequired.getProduct().getProductId() == productId) {
                    amountRequired += productRequired.getQuantityMass();
                }
            }
            entries.add(new BarChartEntry(stockItem.getProduct().getProductName(),
                    (float) stockItem.getMass(), amountRequired));
        }

        // only show bars where stock is required but not held if filtered by ALL
        if (filterLocation == null || TextUtils.isEmpty(filterLocation.getLocationName())) {
            List<ProductQuantity> noStockProducts = new ArrayList<>();
            for (ProductQuantity productRequired : productsRequiredList) {
                int productId = productRequired.getProduct().getProductId();
                boolean hasStock = false;
                for (StockItem stockItem : stockList) {
                    if (stockItem.getProduct().getProductId() == productId) {
                        hasStock = true;
                        break;
                    }
                }
                // if no stock held
                if (!hasStock) {
                    // check if same product already required elsewhere
                    boolean isNewEntry = true;
                    for (ProductQuantity productQuantity : noStockProducts) {
                        if (productQuantity.getProduct().getProductId() == productId) {
                            productQuantity.setQuantityMass(
                                    productQuantity.getQuantityMass()
                                            + productRequired.getQuantityMass());
                            isNewEntry = false;
                            break;
                        }
                    }
                    if (isNewEntry) {
                        noStockProducts.add(new ProductQuantity(
                                productRequired.getProduct(),
                                productRequired.getQuantityMass()));
                    }
                }
            }

            // add bar for each required product with no stock
            for (ProductQuantity productRequired : noStockProducts) {
                entries.add(new BarChartEntry(
                        productRequired.getProduct().getProductName(),
                        0, (float) productRequired.getQuantityMass()));
            }
        }

        return entries;
    }
}
