package com.martinwalls.meatmanager.data.viewmodel;

import android.app.Application;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.data.models.ProductQuantity;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.ui.dashboard.BarChartEntry;
import com.martinwalls.meatmanager.ui.dashboard.LocationFilter;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    public static final int FILTER_ALL = -1;

    private final DBHandler dbHandler;

    private final MutableLiveData<List<BarChartEntry>> chartData;
    private final MediatorLiveData<List<BarChartEntry>> chartDataSorted;

    private int sortMode = SortUtils.SORT_AMOUNT_DESC;
    private int filterLocationId = FILTER_ALL;

    public DashboardViewModel(Application application) {
        super(application);

        dbHandler = new DBHandler(application);

        chartData = new MutableLiveData<>();

        chartDataSorted = new MediatorLiveData<>();
        chartDataSorted.addSource(chartData, barChartEntries ->
                chartDataSorted.setValue(sortedChartData(barChartEntries, sortMode)));
    }

    public LiveData<List<BarChartEntry>> getChartDataObservable() {
        loadChartData();
        return chartDataSorted;
    }

    public void loadChartData() {
        List<StockItem> stockItems;
        if (filterLocationId == FILTER_ALL) {
            stockItems = dbHandler.getAllStockByProduct();
        } else {
            stockItems = dbHandler.getAllStockFromLocationByProduct(filterLocationId);
        }
        List<ProductQuantity> productsRequired = dbHandler.getAllProductsRequired();

        chartData.setValue(formatChartData(stockItems, productsRequired));
    }

    public void sortChartData(int sortMode) {
        this.sortMode = sortMode;

        List<BarChartEntry> entries = chartDataSorted.getValue();
        if (entries == null) {
            return;
        }

        chartDataSorted.setValue(sortedChartData(entries, sortMode));
    }

    public void filterByLocation(int filterLocationId) {
        this.filterLocationId = filterLocationId;
        loadChartData();
    }

    public LiveData<List<LocationFilter>> getLocationsObservable() {

        MutableLiveData<List<Location>> locations = new MutableLiveData<>();

        locations.setValue(dbHandler.getAllStorageLocationsWithStock());

        return Transformations.map(locations, this::getLocationFilters);
    }

    private List<LocationFilter> getLocationFilters(List<Location> locations) {
        List<LocationFilter> filters = new ArrayList<>();

        for (Location location : locations) {
            filters.add(
                    new LocationFilter(location.getLocationId(), location.getLocationName()));
        }

        return filters;
    }

    private List<BarChartEntry> sortedChartData(List<BarChartEntry> entries, int sortMode) {
        switch (sortMode) {
            case SortUtils.SORT_NAME:
                return SortUtils.mergeSort(entries, BarChartEntry.comparatorName());
            case SortUtils.SORT_AMOUNT_DESC:
                return SortUtils.mergeSort(entries, BarChartEntry.comparatorAmount(false));
            case SortUtils.SORT_AMOUNT_ASC:
                return SortUtils.mergeSort(entries, BarChartEntry.comparatorAmount(true));
        }
        return entries;
    }

    private List<BarChartEntry> formatChartData(List<StockItem> stockItems,
                                                List<ProductQuantity> productsRequired) {
        List<BarChartEntry> entries = new ArrayList<>();

        for (StockItem stockItem : stockItems) {
            int productId = stockItem.getProduct().getProductId();
            float amountRequired = 0;
            for (ProductQuantity productRequired : productsRequired) {
                if (productRequired.getProduct().getProductId() == productId) {
                    amountRequired += productRequired.getQuantityMass();
                }
            }
            entries.add(new BarChartEntry(stockItem.getProduct().getProductName(),
                    (float) stockItem.getMass(), amountRequired));
        }

        // only show required products with no stock if not filtered by location
        if (filterLocationId == FILTER_ALL) {
            List<ProductQuantity> noStockProducts = new ArrayList<>();
            for (ProductQuantity productRequired : productsRequired) {
                int productId = productRequired.getProduct().getProductId();
                boolean hasStock = false;
                for (StockItem stockItem : stockItems) {
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
