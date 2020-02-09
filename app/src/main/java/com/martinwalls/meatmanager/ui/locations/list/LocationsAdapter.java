package com.martinwalls.meatmanager.ui.locations.list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.meatmanager.ui.common.adapter.BaseAdapter;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.location.DeleteLocationAction;

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

    @Deprecated
    LocationsAdapter(List<Location> locationList, LocationsAdapterListener listener,
                     Activity parentActivity) {
        this.locationList = locationList;
        this.listener = listener;
        this.parentActivity = parentActivity;
    }

    LocationsAdapter(LocationsAdapterListener listener, Activity parentActivity) {
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
        if (locationList == null) return 0;
        return locationList.size();
    }

    /**
     * Deletes the {@link Location} at the specified position if it is safe
     * to do so. Shows a {@link Snackbar} so the user can undo the action.
     */
    @Override
    public void deleteItem(int position) {
        listener.deleteLocation(locationList.get(position));
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    /**
     * Interface to handle clicks on location items.
     */
    public interface LocationsAdapterListener {
        /**
         * This is called when a location in the list is clicked.
         */
        void onLocationClicked(Location location);

        boolean deleteLocation(Location location); //todo
    }
}
