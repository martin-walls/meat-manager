package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.data.models.ProductQuantity;

public class EditContractFragmentViewModel extends AndroidViewModel {

    public static enum FieldStatus {
        INITIAL,
        VALID,
        INVALID_BLANK,
        INVALID_DATA,
        INVALID_ZERO;
    }

    private final DBHandler dbHandler;

    private final int contractId;

    private final boolean isNewContract;

    private MutableLiveData<Contract> contract;
    private MutableLiveData<Product> selectedProduct;

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

        selectedProduct = new MutableLiveData<>();

        // new contracts initially have a default reminder of 1 day
        if (isNewContract) {
            contract.getValue().setReminder(1);
        }
    }

    private void refreshContract() {
        contract.setValue(contract.getValue()); //todo find better way to do this
    }

    public boolean isNewContract() {
        return isNewContract;
    }

    public LiveData<Contract> getContractObservable() {
        return contract;
    }

    public void loadContract() {
        if (isNewContract) return;
        contract.setValue(dbHandler.getContract(contractId));
    }

    public LiveData<Product> getSelectedProductObservable() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product product) {
        selectedProduct.setValue(product);
    }

    public void commitSelectedProduct(double quantityMass, int numBoxes) {
        if (selectedProduct.getValue() == null) return;
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setProduct(selectedProduct.getValue());
        productQuantity.setQuantityMass(quantityMass);
        productQuantity.setQuantityBoxes(numBoxes);
        contract.getValue().getProductList().add(productQuantity);
        selectedProduct.setValue(null);
        refreshContract();
    }

    public void setDestination(Location destination) {
        contract.getValue().setDest(destination);
        refreshContract();
    }

    public void setRepeatInterval(Interval repeatInterval) {
        contract.getValue().setRepeatInterval(repeatInterval);
        refreshContract();
    }

    public void setReminder(int reminder) {
        contract.getValue().setReminder(reminder);
        refreshContract();
    }

    /**
     * Adds the specified amount to the reminder. The change can be positive or
     * negative to increase/decrease the reminder respectively. Doesn't let the
     * reminder go below 0.
     */
    public void updateReminderBy(int change) {
        int newValue = contract.getValue().getReminder() + change;
        if (newValue < 0) newValue = 0;
        contract.getValue().setReminder(newValue);
        refreshContract();
    }
}
