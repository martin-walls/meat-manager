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
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.contract.AddContractAction;
import com.martinwalls.meatmanager.util.undo.contract.EditContractAction;

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

    private MutableLiveData<List<SearchItem>> searchItemList;

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

    public boolean saveContract(Contract newContract) {
        if (!isEditMode()) {
            int newRowId = dbHandler.addContract(newContract);
            newContract.setContractId(newRowId);
            UndoStack.getInstance().push(new AddContractAction(newContract));
            return newRowId != -1;
        } else {
            newContract.setContractId(contract.getValue().getContractId());
            boolean success = dbHandler.updateContract(newContract);
            UndoStack.getInstance().push(new EditContractAction(contract.getValue(), newContract));
            return success;
        }
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






    public boolean addProduct(Product product) {
        return true;
    }



    public void setSelectedProductId(int id) {
        this.selectedProductId = id;
    }

    public Product getSelectedProduct() {
        return dbHandler.getProduct(selectedProductId);
    }




    public void setSelectedDestId(int id) {
        this.selectedDestId = id;
    }

    public Location getSelectedDest() {
        return dbHandler.getLocation(selectedDestId);
    }


    public LiveData<List<ProductQuantity>> getProductsAdded() {
        if (productsAdded == null) productsAdded = new MutableLiveData<>();
        if (productsAdded.getValue() == null) productsAdded.setValue(new ArrayList<>());
        return productsAdded;
    }


    public void addProductAdded(ProductQuantity product) {
        if (productsAdded == null) productsAdded = new MutableLiveData<>();
        productsAdded.getValue().add(product);
    }

    public void removeProductAdded(int pos) {
        if (productsAdded.getValue() == null || productsAdded.getValue().size() == 0) return;
        productsAdded.getValue().remove(pos);
        productsAdded.postValue(productsAdded.getValue()); // refresh LiveData so UI updates
    }





    public LiveData<List<SearchItem>> getSearchItemList() {
        if (searchItemList == null) searchItemList = new MutableLiveData<>();
        return searchItemList;
    }

    public void loadSearchItems(String searchItemType) {
        List<SearchItem> newSearchItems = new ArrayList<>();
        switch (searchItemType) {
            case "product":
                for (Product product : SortUtils.mergeSort(
                        dbHandler.getAllProducts(), Product.comparatorAlpha())) {
                    newSearchItems.add(
                            new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case "destination":
                for (Location location : SortUtils.mergeSort(
                        dbHandler.getAllLocations(Location.LocationType.Destination),
                        Location.comparatorAlpha())) {
                    newSearchItems.add(new SearchItem(
                            location.getLocationName(), location.getLocationId()));
                }
                break;
        }
        searchItemList.setValue(newSearchItems);
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
