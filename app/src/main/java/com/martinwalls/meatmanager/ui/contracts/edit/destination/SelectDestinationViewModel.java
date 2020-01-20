package com.martinwalls.meatmanager.ui.contracts.edit.destination;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.List;

public class SelectDestinationViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private final MutableLiveData<List<Location>> destinationList;

    public SelectDestinationViewModel(Application application) {
        super(application);

        dbHandler = new DBHandler(application);

        destinationList = new MutableLiveData<>();
    }

    public LiveData<List<Location>> getDestinationListObservable() {
        loadDestinations();
        return destinationList;
    }

    public void loadDestinations() {
        destinationList.setValue(
                SortUtils.mergeSort(
                        dbHandler.getAllLocations(Location.LocationType.Destination),
                        Location.comparatorAlpha()));
    }
}
