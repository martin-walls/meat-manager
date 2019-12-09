package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EditContractViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    private final int editType;
    private int contractId = -1;

    public EditContractViewModelFactory(@NonNull Application application, int editType) {
        this.application = application;
        this.editType = editType;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (editType == EditContractActivity.EDIT_TYPE_EDIT) {
            return (T) new EditContractViewModel(application, editType, contractId);
        } else {
            return (T) new EditContractViewModel(application, editType,
                    EditContractViewModel.CONTRACT_NONE);
        }
    }
}
