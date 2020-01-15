package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;

public class EditContractFragmentViewModelFactory implements ViewModelProvider.Factory {

    public static enum Mode {
        CREATE_NEW,
        EDIT_EXISTING
    }

    private final Application application;

    private final Mode mode;
    private int contractId = -1;

    public EditContractFragmentViewModelFactory(@NonNull Application application, Mode mode) {
        this.application = application;
        this.mode = mode;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (mode == Mode.CREATE_NEW) {
                    return modelClass.getConstructor(Application.class)
                            .newInstance(application);
            } else {
                return modelClass.getConstructor(Application.class, Integer.TYPE)
                        .newInstance(application, contractId);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
