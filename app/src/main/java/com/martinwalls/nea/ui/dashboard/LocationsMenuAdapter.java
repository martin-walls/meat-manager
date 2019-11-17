package com.martinwalls.nea.ui.dashboard;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Location;

import java.util.List;

public class LocationsMenuAdapter
        extends RecyclerView.Adapter<LocationsMenuAdapter.ViewHolder> {

    private LocationsMenuAdapterListener listener;

    private List<Location> locationsList;

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

    LocationsMenuAdapter(List<Location> locationsList, LocationsMenuAdapterListener listener) {
        this.locationsList = locationsList;
        this.listener = listener;

        this.locationsList.add(0, new Location());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_location, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Location location = locationsList.get(position);
        if (!TextUtils.isEmpty(location.getLocationName())) {
            holder.locationName.setText(location.getLocationName());
        } else {
            holder.locationName.setText(R.string.dashboard_filter_locations_all);
        }
//        if (position == 0) {
//            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.locationName.getLayoutParams();
//            layoutParams.setMarginStart(Utils.convertDpToPixelSize(24, holder.locationName.getContext()));
//        } else if (position == locationsList.size() - 1) {
//            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.locationName.getLayoutParams();
//            layoutParams.setMarginEnd(Utils.convertDpToPixelSize(24, holder.locationName.getContext()));
//        }

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
        void onLocationItemClicked(Location location);
    }
}