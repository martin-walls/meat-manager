package com.martinwalls.meatmanager.ui.contracts.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Order;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.List;

public class ContractsViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private final MutableLiveData<List<Contract>> contractsList;

    public ContractsViewModel(Application application) {
        super(application);

        dbHandler = new DBHandler(application);

        contractsList = new MutableLiveData<>();
    }

    public LiveData<List<Contract>> getContractListObservable() {
        loadContracts();
        return contractsList;
    }

    public void loadContracts() {
        contractsList.setValue(SortUtils.mergeSort(
                dbHandler.getAllContracts(), Contract.comparatorDate()));
    }
}
