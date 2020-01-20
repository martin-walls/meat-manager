package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.ValidationState;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.data.models.ProductQuantity;

public class EditContractViewModel extends AndroidViewModel {

    public class State {
/*        public class FieldStatus {
            private String name;
            private ValidationState state;

            public FieldStatus(@NonNull String name, @NonNull ValidationState state) {
                this.name = name;
                this.state = state;
            }

            public String getName() {
                return name;
            }

            public ValidationState getState() {
                return state;
            }

            public void setState(ValidationState state) {
                this.state = state;
            }
        }*/

        private ValidationState productState;
        private ValidationState productListState;
        private ValidationState destinationState;
        private ValidationState repeatIntervalState;
        private ValidationState repeatOnState;
        private ValidationState reminderState;

        private State() {
            productState = ValidationState.INITIAL;
            productListState = ValidationState.INITIAL;
            destinationState = ValidationState.INITIAL;
            repeatIntervalState = ValidationState.INITIAL;
            repeatOnState = ValidationState.INITIAL;
            reminderState = ValidationState.INITIAL;
        }

        public boolean isTotalStateValid() {
            if (productState != ValidationState.VALID
                    && productListState != ValidationState.VALID) return false;
            if (destinationState != ValidationState.VALID) return false;
            if (repeatIntervalState != ValidationState.VALID) return false;
            if (repeatOnState != ValidationState.VALID) return false;
            if (reminderState != ValidationState.VALID) return false;
            return true;
        }

        private void setProductState(ValidationState productState) {
            this.productState = productState;
        }

        public ValidationState getProductState() {
            return productState;
        }

        private void setProductListState(ValidationState productListState) {
            this.productListState = productListState;
        }

        public ValidationState getProductListState() {
            return productListState;
        }

        public ValidationState getDestinationState() {
            return destinationState;
        }

        private void setDestinationState(ValidationState destinationState) {
            this.destinationState = destinationState;
        }

        public ValidationState getRepeatIntervalState() {
            return repeatIntervalState;
        }

        private void setRepeatIntervalState(ValidationState repeatIntervalState) {
            this.repeatIntervalState = repeatIntervalState;
        }

        public ValidationState getRepeatOnState() {
            return repeatOnState;
        }

        private void setRepeatOnState(ValidationState repeatOnState) {
            this.repeatOnState = repeatOnState;
        }

        public ValidationState getReminderState() {
            return reminderState;
        }

        private void setReminderState(ValidationState reminderState) {
            this.reminderState = reminderState;
        }
    }

    private final DBHandler dbHandler;

    private final int contractId;

    private final boolean isNewContract;

    private MutableLiveData<Contract> contract;
    private MutableLiveData<Product> selectedProduct;

    private State state;

    // new contract
    public EditContractViewModel(Application application) {
        super(application);

        isNewContract = true;
        contractId = -1;

        dbHandler = new DBHandler(application);

        initLiveData();
    }

    // edit existing contract
    public EditContractViewModel(Application application, int contractId) {
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

        state = new State();

        // new contracts initially have a default reminder of 1 day
        if (isNewContract) {
            contract.getValue().setReminder(1);
            state.setReminderState(ValidationState.VALID);
            contract.getValue().setRepeatOn(1);
            state.setRepeatOnState(ValidationState.VALID);
        }
    }

    public State getState() {
        return state;
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
        state.setProductState(ValidationState.VALID);
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
        state.setProductState(ValidationState.INITIAL);
        state.setProductListState(ValidationState.INITIAL);
    }

    public void setDestination(Location destination) {
        contract.getValue().setDest(destination);
        refreshContract();
        state.setDestinationState(ValidationState.VALID);
    }

    public void setRepeatInterval(Interval repeatInterval) {
        contract.getValue().setRepeatInterval(repeatInterval);

        refreshContract();
        state.setRepeatIntervalState(ValidationState.VALID);
    }

    public void setRepeatOn(int repeatOn) {
        contract.getValue().setRepeatOn(repeatOn);
        state.setRepeatIntervalState(ValidationState.VALID);
    }

    public void setReminder(int reminder) {
        contract.getValue().setReminder(reminder);
        refreshContract();
        state.setRepeatIntervalState(ValidationState.VALID);
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
        state.setRepeatIntervalState(ValidationState.VALID);
    }

    public boolean commitContract() {
        int newRowId = dbHandler.addContract(contract.getValue());
        return newRowId != -1;
    }
}
