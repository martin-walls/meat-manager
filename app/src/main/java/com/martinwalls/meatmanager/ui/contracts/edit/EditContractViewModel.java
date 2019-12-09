package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.data.models.ProductQuantity;
import com.martinwalls.meatmanager.data.models.SearchItem;
import com.martinwalls.meatmanager.ui.SearchItemViewModel;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class EditContractViewModel extends AndroidViewModel {

    public static final int CONTRACT_NONE = -1;

    private final DBHandler dbHandler;

    private final int editType;
    private final int contractId;

    private MutableLiveData<Contract> contract;

    private MutableLiveData<List<ProductQuantity>> productsAdded;

    private int selectedProductId;
    private int selectedDestId;

    private MutableLiveData<Interval> selectedRepeatInterval;

    public EditContractViewModel(Application application, int editType, int contractId) {
        super(application);

        this.editType = editType;
        this.contractId = contractId;

        dbHandler = new DBHandler(application);
    }

    public boolean isEditMode() {
        return editType == EditContractActivity.EDIT_TYPE_EDIT;
    }

    public LiveData<Contract> getContract() {
        if (contract == null) {
            contract = new MutableLiveData<>();
            loadContract();
        }
        return contract;
    }

    public void loadContract() {
        contract.setValue(dbHandler.getContract(contractId));
    }

    public LiveData<Interval> getSelectedRepeatInterval() {
        if (selectedRepeatInterval == null) {
            selectedRepeatInterval = new MutableLiveData<>();
        }
        return selectedRepeatInterval;
    }

    public void setSelectedRepeatInterval(Interval interval) {
        selectedRepeatInterval.setValue(interval);
    }













//    public LiveData<Location> getLocation() {
//
//    }
//
//    public LiveData<List<Product>> getAllProducts() {
//
//    }
//
//    public LiveData<List<Location>> getAllDestinations() {
//
//    }
//
//    public boolean addNewProduct(Product product) {
//
//    }
//
//    public LiveData<Product> getProduct(int productId) {
//        // ??
//    }
//
//    public boolean updateContract() {
//
//    }
}
