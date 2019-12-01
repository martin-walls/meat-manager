package com.martinwalls.meatmanager.util.undo.location;

import android.content.Context;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.util.undo.UndoableAction;

public class DeleteLocationAction extends UndoableAction {

    private Location location;

    public DeleteLocationAction(Location location) {
        this.location = location;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        int newId = dbHandler.addLocation(location);
        location.setLocationId(newId);
        return true;
    }

    @Override
    public void showUndoMessage(Context context) {
        showUndoSnackbar(context, context.getString(
                R.string.undo_delete_location_success, location.getLocationName()));
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.deleteLocation(location.getLocationId());
    }

    @Override
    public void showRedoMessage(Context context) {
        showRedoSnackbar(context, context.getString(
                R.string.redo_delete_location_success, location.getLocationName()));
    }
}
