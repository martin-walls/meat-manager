package com.martinwalls.meatmanager.ui.locations;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.meatmanager.ui.BaseAdapter;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;

import java.util.List;

public class LocationsAdapter extends BaseAdapter<LocationsAdapter.ViewHolder> {

    private List<Location> locationList;

    private Location recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    private Activity parentActivity;
    private LocationsAdapterListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationName, locationType;

        ViewHolder(View view) {
            super(view);
            locationName = view.findViewById(R.id.info_primary);
            locationType = view.findViewById(R.id.info_secondary);

            RelativeLayout itemRootLayout = view.findViewById(R.id.item_root_layout);
            itemRootLayout.setOnClickListener(v ->
                    listener.onLocationClicked(locationList.get(getAdapterPosition())));
        }
    }

    LocationsAdapter(List<Location> locationList, LocationsAdapterListener listener,
                     Activity parentActivity) {
        this.locationList = locationList;
        this.listener = listener;
        this.parentActivity = parentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_secondary_info_selectable, parent, false);
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

    /**
     * Deletes the {@link Location} at the specified position if it is safe
     * to do so. Shows a {@link Snackbar} so the user can undo the action.
     */
    @Override
    public void deleteItem(int position) {
        DBHandler dbHandler = new DBHandler(parentActivity);
        boolean safeToDelete = dbHandler.isLocationSafeToDelete(
                locationList.get(position).getLocationId());
        if (safeToDelete) {
            recentlyDeletedItem = locationList.get(position);
            recentlyDeletedItemPosition = position;
            locationList.remove(position);
            notifyItemRemoved(position);
            showUndoSnackbar();
        } else {
            Toast.makeText(parentActivity,
                    parentActivity.getString(R.string.db_error_delete_location,
                            locationList.get(position).getLocationName()),
                    Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
        }
    }

    /**
     * Shows a {@link Snackbar} allowing the user to undo deleting a location.
     * Deletes the location from the database when the Snackbar times out or
     * is dismissed.
     */
    private void showUndoSnackbar() {
        View view = parentActivity.findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(view,
                R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);
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

    /**
     * Adds the recently delete location back to its previous position in the
     * list.
     */
    private void undoDelete() {
        locationList.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
    }

    /**
     * Interface to handle clicks on location items.
     */
    public interface LocationsAdapterListener {
        /**
         * This is called when a location in the list is clicked.
         */
        void onLocationClicked(Location location);
    }
}
