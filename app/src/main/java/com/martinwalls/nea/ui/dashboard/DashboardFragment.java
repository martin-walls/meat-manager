package com.martinwalls.nea.ui.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Location;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.data.models.StockItem;
import com.martinwalls.nea.ui.misc.RecyclerViewMargin;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.SortUtils;
import com.martinwalls.nea.util.Utils;

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

    private RecyclerView locationsRecyclerView;
    private LocationsMenuAdapter locationsAdapter;

    private Location filterLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.dashboard_title);
        View fragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHandler = new DBHandler(getContext());
        prefs = EasyPreferences.createForDefaultPreferences(getContext());

        locationsRecyclerView = fragmentView.findViewById(R.id.recycler_view_locations);
        locationsAdapter = new LocationsMenuAdapter(
                dbHandler.getAllLocations(Location.LocationType.Storage), this);
        locationsRecyclerView.setAdapter(locationsAdapter);

        RecyclerViewMargin margins = new RecyclerViewMargin(
                Utils.convertDpToPixelSize(16, getContext()), RecyclerViewMargin.HORIZONTAL);
        locationsRecyclerView.addItemDecoration(margins);

        if (filterLocation == null || !TextUtils.isEmpty(filterLocation.getLocationName())) {
            filterLocation = new Location();
        }

        locationsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

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
     * Gets all stock from the database, sorts it and updates the bar chart
     * to show the updated data.
     */
    private void loadData() {
        List<BarChartEntry> entries = new ArrayList<>();

        List<StockItem> stockList;
        if (filterLocation == null || TextUtils.isEmpty(filterLocation.getLocationName())) {
            stockList = SortUtils.mergeSort(
                    dbHandler.getAllStock(), StockItem.comparatorAlpha());
        } else {
            stockList = SortUtils.mergeSort(
                    dbHandler.getAllStockFromLocation(filterLocation.getLocationId()),
                    StockItem.comparatorAlpha());
        }

        for (int i = 1; i < stockList.size(); i++) {
            StockItem lastStockItem = stockList.get(i - 1);
            StockItem thisStockItem = stockList.get(i);
            if (lastStockItem.getProduct().getProductId()
                    == thisStockItem.getProduct().getProductId()) {
                lastStockItem.setMass(lastStockItem.getMass() + thisStockItem.getMass());
                lastStockItem.setNumBoxes(
                        lastStockItem.getNumBoxes() + thisStockItem.getNumBoxes());
                stockList.remove(i);
                i--;
            }
        }

        if (stockList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            graphLayout.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            graphLayout.setVisibility(View.VISIBLE);
        }


        switch (prefs.getInt(R.string.pref_dashboard_sort_by, SORT_BY_DEFAULT)) {
            case SortUtils.SORT_NAME:
                stockList = SortUtils.mergeSort(stockList, StockItem.comparatorAlpha());
                break;
            case SortUtils.SORT_AMOUNT_DESC:
                stockList = SortUtils.mergeSort(stockList, StockItem.comparatorAmount(false));
                break;
            case SortUtils.SORT_AMOUNT_ASC:
                stockList = SortUtils.mergeSort(stockList, StockItem.comparatorAmount(true));
                break;
        }

        List<ProductQuantity> productsRequiredList = dbHandler.getAllProductsRequired();

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

        graphView.setData(entries);
    }
}
