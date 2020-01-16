package com.martinwalls.meatmanager.ui.contracts.edit;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.databinding.ItemSearchResultBinding;

import java.util.ArrayList;
import java.util.List;

public class DestinationListAdapter extends RecyclerView.Adapter<DestinationListAdapter.ViewHolder>
        implements Filterable {

    private final DestinationListAdapterListener listener;

    private List<Location> destinationList;
    private List<Location> destinationListFiltered;

    private int selectedDestinationId = -1;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ViewHolder(ItemSearchResultBinding binding) {
            super(binding.getRoot());
            name = binding.name;

            name.setOnClickListener(v ->
                    listener.onDestinationClicked(destinationListFiltered.get(getAdapterPosition())));
        }
    }

    public DestinationListAdapter(DestinationListAdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchResultBinding binding = ItemSearchResultBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(destinationListFiltered.get(position).getLocationName());
        if (selectedDestinationId != -1) {
            if (selectedDestinationId == destinationListFiltered.get(position).getLocationId()) {
                holder.name.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.name.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (destinationListFiltered == null) return 0;
        return destinationListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Location> filteredList = new ArrayList<>();
                if (constraint.length() == 0) {
                    filteredList = destinationList;
                } else {
                    for (Location destination : destinationList) {
                        if (destination.getLocationName().toLowerCase()
                                .contains(constraint.toString().toLowerCase())) {
                            filteredList.add(destination);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                destinationListFiltered = (List<Location>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setDestinationList(List<Location> destinationList) {
        this.destinationList = destinationList;
        this.destinationListFiltered = destinationList;
        notifyDataSetChanged();
    }

    public void setSelectedDestinationId(int destinationId) {
        this.selectedDestinationId = destinationId;
    }

    public int getPositionOfItemWithId(int id) {
        for (int i = 0; i < destinationListFiltered.size(); i++) {
            if (destinationListFiltered.get(i).getLocationId() == id) {
                return i;
            }
        }
        return -1;
    }

    public interface DestinationListAdapterListener {
        void onDestinationClicked(Location destination);
    }
}
