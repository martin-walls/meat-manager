package com.martinwalls.meatmanager.ui.locations.list;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.location.DeleteLocationAction;

import java.util.List;

public class LocationsViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private final MutableLiveData<List<Location>> locations;

    public LocationsViewModel(Application application) {
        super(application);

        dbHandler = new DBHandler(application);

        locations = new MutableLiveData<>();
    }

    public LiveData<List<Location>> getLocationsObservable() {
        loadLocations();
        return locations;
    }

    public void loadLocations() {
        locations.setValue(SortUtils.mergeSort(dbHandler.getAllLocations(), Location.comparatorAlpha()));
    }

    public boolean isLocationSafeToDelete(int locationId) {
        return dbHandler.isLocationSafeToDelete(locationId);
    }

    public boolean deleteLocation(int locationId) {
        if (!isLocationSafeToDelete(locationId)) return false;
        Location deletedLocation = dbHandler.getLocation(locationId);
        boolean success = dbHandler.deleteLocation(locationId);
        // push action to undo stack if location was deleted
        if (success) UndoStack.getInstance().push(new DeleteLocationAction(deletedLocation));
        return success;
    }
}
