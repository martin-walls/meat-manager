package com.martinwalls.nea.stock;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.nea.BaseAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.db.models.Location;

import java.util.List;

public class LocationsAdapter extends BaseAdapter<LocationsAdapter.ViewHolder> {

    private List<Location> locationList;

    private Location recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    private Activity parentActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationName, locationType;

        ViewHolder(View view) {
            super(view);
            locationName = view.findViewById(R.id.info_primary);
            locationType = view.findViewById(R.id.info_secondary);
        }
    }

    LocationsAdapter(List<Location> locationList, Activity parentActivity) {
        this.locationList = locationList;
        this.parentActivity = parentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_secondary_info, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.locationName.setText(location.getLocationName());
        holder.locationType.setText(location.getLocationType().name());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    @Override
    public void deleteItem(int position) {
        DBHandler dbHandler = new DBHandler(parentActivity);
        boolean safeToDelete = dbHandler.isLocationSafeToDelete(locationList.get(position).getLocationId());
        if (safeToDelete) {
            recentlyDeletedItem = locationList.get(position);
            recentlyDeletedItemPosition = position;
            locationList.remove(position);
            notifyItemRemoved(position);
            showUndoSnackbar();
        } else {
            Toast.makeText(parentActivity, parentActivity.getString(R.string.db_error_delete_location, locationList.get(position).getLocationName()), Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
        }
    }

    private void showUndoSnackbar() {
        View view = parentActivity.findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo, v -> undoDelete());
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_MANUAL) {
                    DBHandler dbHandler = new DBHandler(parentActivity);
                    dbHandler.deleteLocation(recentlyDeletedItem.getLocationId());
                }

            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        locationList.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
    }
}
