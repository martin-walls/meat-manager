package com.martinwalls.meatmanager.ui.locations.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LocationDetailViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private int locationId;

    public LocationDetailViewModelFactory(@NonNull Application application, int locationId) {
        this.application = application;
        this.locationId = locationId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LocationDetailViewModel(application, locationId);
    }
}
