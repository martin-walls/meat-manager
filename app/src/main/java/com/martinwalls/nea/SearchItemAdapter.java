package com.martinwalls.nea;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.ViewHolder>
        implements Filterable {

    private List<String> itemList;
    private List<String> itemListFiltered;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;

        public ViewHolder(View view) {
            super(view);
            itemName = (TextView) view;
        }
    }

    public SearchItemAdapter(List<String> itemList) {
        this.itemList = itemList;
        this.itemListFiltered = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = itemListFiltered.get(position);
        holder.itemName.setText(item);
    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                if (charSequence.length() == 0) {
                    itemListFiltered = itemList;
                } else {
                    List<String> filteredList = new ArrayList<>();
                    for (String item : itemList) {
                        if (item.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    itemListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                itemListFiltered = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
