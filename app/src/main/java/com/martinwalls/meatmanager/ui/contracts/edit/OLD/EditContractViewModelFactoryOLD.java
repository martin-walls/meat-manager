package com.martinwalls.meatmanager.ui.contracts.edit.OLD;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.martinwalls.meatmanager.ui.contracts.edit.EditContractActivity;

public class EditContractViewModelFactoryOLD implements ViewModelProvider.Factory {

    private final Application application;

    private final int editType;
    private int contractId = -1;

    public EditContractViewModelFactoryOLD(@NonNull Application application, int editType) {
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
            return (T) new EditContractViewModelOLD(application, editType, contractId);
        } else {
            return (T) new EditContractViewModelOLD(application, editType,
                    EditContractViewModelOLD.CONTRACT_NONE);
        }
    }
}
