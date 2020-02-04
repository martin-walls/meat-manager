package com.martinwalls.meatmanager.ui.locations.detail;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.location.DeleteLocationAction;

public class LocationDetailViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private MutableLiveData<Location> location;

    private final int locationId;

    public LocationDetailViewModel(Application application, int locationId) {
        super(application);

        this.locationId = locationId;

        dbHandler = new DBHandler(application);
    }

    public LiveData<Location> getLocation() {
        if (location == null) {
            location = new MutableLiveData<>();
            loadLocation();
        }
        return location;
    }

    public void loadLocation() {
        location.setValue(dbHandler.getLocation(locationId));
    }

    public int getLocationId() {
        return locationId;
    }

    public boolean isLocationSafeToDelete() {
        return dbHandler.isLocationSafeToDelete(locationId);
    }

    public boolean deleteLocation() {
        boolean success = dbHandler.deleteLocation(locationId);
        if (success) {
            UndoStack.getInstance().push(new DeleteLocationAction(location.getValue()));
        }
        return success;
    }
}
