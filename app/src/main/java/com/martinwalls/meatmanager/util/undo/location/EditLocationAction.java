package com.martinwalls.meatmanager.util.undo.location;

import android.content.Context;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.util.undo.UndoableAction;

public class EditLocationAction extends UndoableAction {

    private Location locationBefore;
    private Location locationAfter;

    public EditLocationAction(Location locationBefore, Location locationAfter) {
        this.locationBefore = locationBefore;
        this.locationAfter = locationAfter;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateLocation(locationBefore);
    }

    @Override
    public void showUndoMessage(Context context) {
        showUndoSnackbar(context, context.getString(
                R.string.undo_edit_location_success, locationBefore.getLocationName()));
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateLocation(locationAfter);
    }

    @Override
    public void showRedoMessage(Context context) {
        showRedoSnackbar(context, context.getString(
                R.string.redo_edit_location_success, locationBefore.getLocationName()));
    }
}
