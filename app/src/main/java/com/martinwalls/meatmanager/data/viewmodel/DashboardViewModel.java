package com.martinwalls.meatmanager.data.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private final MutableLiveData<List<StockItem>> dbStockList;
    private final MediatorLiveData<List<StockItem>> stockListObservable;

    private int sortMode = SortUtils.SORT_AMOUNT_DESC;
    private int filterLocationId = -1;

    public DashboardViewModel(Application application) {
        super(application);

        dbHandler = new DBHandler(application);

        dbStockList = new MutableLiveData<>();

        stockListObservable = new MediatorLiveData<>();

        stockListObservable.addSource(dbStockList, stockItems ->
                stockListObservable.setValue(sortedStockList(stockItems, sortMode)));
    }

    public void sortStock(int sortMode) {
        this.sortMode = sortMode;

        List<StockItem> stockItems = stockListObservable.getValue();
        if (stockItems == null) {
            return;
        }

        stockListObservable.setValue(sortedStockList(stockItems, sortMode));
    }

    private List<StockItem> sortedStockList(List<StockItem> stockItems, int sortMode) {
        switch (sortMode) {
            case SortUtils.SORT_NAME:
                return SortUtils.mergeSort(stockItems, StockItem.comparatorAlpha());
            case SortUtils.SORT_AMOUNT_DESC:
                return SortUtils.mergeSort(stockItems, StockItem.comparatorAmount(false));
            case SortUtils.SORT_AMOUNT_ASC:
                return SortUtils.mergeSort(stockItems, StockItem.comparatorAmount(true));
        }
        return stockItems;
    }

    public void loadStock() {
        dbStockList.setValue(dbHandler.getAllStockByProduct());
    }

    public LiveData<List<StockItem>> getStockListObservable() {
        loadStock();
        return stockListObservable;
    }
}
