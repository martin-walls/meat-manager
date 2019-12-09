package com.martinwalls.meatmanager.ui.contracts.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ContractDetailViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private int extra;

    public ContractDetailViewModelFactory(@NonNull Application application, int extra) {
        this.application = application;
        this.extra = extra;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ContractDetailViewModel(application, extra);
    }
}
