package com.martinwalls.nea.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.models.Location;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private List<Location> locationList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationName;
        private TextView locationType;

        ViewHolder(View view) {
            super(view);
            locationName = view.findViewById(R.id.location_name);
            locationName = view.findViewById(R.id.location_type);
        }
    }

    LocationsAdapter(List<Location> locationList) {
        this.locationList = locationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
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
}
