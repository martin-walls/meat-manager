package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.data.models.ProductQuantity;

public class EditContractFragmentViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private final int contractId;

    private final boolean isNewContract;

    private MutableLiveData<Contract> contract;
    private MutableLiveData<ProductQuantity> selectedProductQuantityValues;

    // new contract
    public EditContractFragmentViewModel(Application application) {
        super(application);

        isNewContract = true;
        contractId = -1;

        dbHandler = new DBHandler(application);

        initLiveData();
    }

    // edit existing contract
    public EditContractFragmentViewModel(Application application, int contractId) {
        super(application);

        isNewContract = false;
        this.contractId = contractId;

        dbHandler = new DBHandler(application);

        initLiveData();
    }

    private void initLiveData() {
        contract = new MutableLiveData<>();
        contract.setValue(new Contract());

        selectedProductQuantityValues = new MutableLiveData<>();
        selectedProductQuantityValues.setValue(new ProductQuantity());
    }

    public LiveData<Contract> getContractObservable() {
        return contract;
    }

    public void loadContract() {
        if (isNewContract) return;
        contract.setValue(dbHandler.getContract(contractId));
    }

    public LiveData<ProductQuantity> getSelectedProductQuantityObservable() {
        return selectedProductQuantityValues;
    }

    public void setSelectedProduct(Product product) {
        selectedProductQuantityValues.getValue().setProduct(product);
        selectedProductQuantityValues.setValue(selectedProductQuantityValues.getValue());
    }

    public void setSelectedMass(double mass) {
        selectedProductQuantityValues.getValue().setQuantityMass(mass);
        selectedProductQuantityValues.setValue(selectedProductQuantityValues.getValue()); //todo find better way to do this
    }

    public void setSelectedNumBoxes(int numBoxes) {
        selectedProductQuantityValues.getValue().setQuantityBoxes(numBoxes);
        selectedProductQuantityValues.setValue(selectedProductQuantityValues.getValue());
    }

    public void commitProductQuantityFields() {
        contract.getValue().getProductList().add(selectedProductQuantityValues.getValue());
        selectedProductQuantityValues.setValue(new ProductQuantity());
    }
}
