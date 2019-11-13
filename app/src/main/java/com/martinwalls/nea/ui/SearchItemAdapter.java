package com.martinwalls.nea.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.ViewHolder>
        implements Filterable {

    private List<SearchItem> itemList;
    private List<SearchItem> itemListFiltered;
    private String searchItemType;
    private SearchItemAdapterListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;

        ViewHolder(View view) {
            super(view);
            itemName = (TextView) view;

            itemName.setOnClickListener(
                    v -> listener.onSearchItemSelected(itemListFiltered.get(getAdapterPosition()), searchItemType));
        }
    }

    public SearchItemAdapter(List<SearchItem> itemList, String searchItemType, SearchItemAdapterListener listener) {
        this.itemList = itemList;
        this.itemListFiltered = itemList;
        this.searchItemType = searchItemType;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = itemListFiltered.get(position).getName();
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
                    List<SearchItem> filteredList = new ArrayList<>();
                    for (SearchItem item : itemList) {
                        if (item.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
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
                //noinspection unchecked
                itemListFiltered = (ArrayList<SearchItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setSearchItemType(String searchItemType) {
        this.searchItemType = searchItemType;
    }

    /**
     * Interface to handle clicks on search items.
     */
    public interface SearchItemAdapterListener {
        /**
         * Called when a search item is clicked. This should be implemented
         * to close the search view and store the selected search item.
         */
        void onSearchItemSelected(SearchItem item, String searchItemType);
    }
}
