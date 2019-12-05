package com.martinwalls.meatmanager.ui.orders.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Order;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.List;

public class OrdersViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private final MutableLiveData<List<Order>> ordersList;

    private boolean getCompletedOrders = false;

    public OrdersViewModel(Application application) {
        super(application);

        dbHandler = new DBHandler(application);

        ordersList = new MutableLiveData<>();
    }

    public LiveData<List<Order>> getOrderListObservable() {
        loadOrders();
        return ordersList;
    }

    public void loadOrders() {
        ordersList.setValue(SortUtils.mergeSort(
                dbHandler.getAllOrders(getCompletedOrders), Order.comparatorDate()));
    }

    public void getCompletedOrders(boolean completed) {
        getCompletedOrders = completed;
        loadOrders();
    }
}
