package com.martinwalls.meatmanager.ui.dashboard;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Location;

import java.util.List;

public class LocationsMenuAdapter
        extends RecyclerView.Adapter<LocationsMenuAdapter.ViewHolder> {

    private LocationsMenuAdapterListener listener;

    private List<LocationFilter> locationsList;

    private int selectedItemPos = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationName;

        ViewHolder(View view) {
            super(view);
            locationName = view.findViewById(R.id.name);

            locationName.setOnClickListener(
                    v -> {
                        setSelectedItem(getAdapterPosition());
                        listener.onLocationItemClicked(
                                locationsList.get(getAdapterPosition()));
                    });
        }
    }

    LocationsMenuAdapter(LocationsMenuAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_location, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationFilter location = locationsList.get(position);
        if (location.filterAll()) {
            holder.locationName.setText(R.string.dashboard_filter_locations_all);
        } else {
            holder.locationName.setText(location.getName());
        }
        if (position == selectedItemPos) {
            holder.locationName.setSelected(true);
        } else {
            holder.locationName.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public void setLocationsList(List<LocationFilter> locationsList) {
        this.locationsList = locationsList;
        this.locationsList.add(0, new LocationFilter(true));
        notifyDataSetChanged();
    }

    /**
     * Sets the menu item at position {@code newSelectedItemPos} to be
     * selected, and deselects the previously selected item.
     */
    private void setSelectedItem(int newSelectedItemPos) {
        int prevSelectedItemPos = selectedItemPos;
        if (newSelectedItemPos < 0) {
            selectedItemPos = 0;
        } else if (newSelectedItemPos >= locationsList.size()) {
            selectedItemPos = locationsList.size() - 1;
        } else {
            selectedItemPos = newSelectedItemPos;
        }
        notifyItemChanged(prevSelectedItemPos);
        notifyItemChanged(newSelectedItemPos);
    }

    /**
     * Interface to handle clicks on menu items.
     */
    public interface LocationsMenuAdapterListener {
        /**
         * This is called when a menu item is clicked. This should be
         * implemented to filter stock by location.
         */
        void onLocationItemClicked(LocationFilter location);
    }
}