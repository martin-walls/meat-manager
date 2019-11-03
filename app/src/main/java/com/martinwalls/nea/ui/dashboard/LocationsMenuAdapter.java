package com.martinwalls.nea.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;

import java.util.List;

//todo finish dashboard locations menu
public class LocationsMenuAdapter extends RecyclerView.Adapter<LocationsMenuAdapter.ViewHolder> {

    private LocationsMenuAdapterListener listener;

    private List<String> locationsList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View view) {
            super(view);
        }
    }

    LocationsMenuAdapter(List<String> locationsList, LocationsMenuAdapterListener listener) {
        this.locationsList = locationsList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_location, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //TODO Add your logic for binding the view
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public interface LocationsMenuAdapterListener {

    }
}