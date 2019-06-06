package com.martinwalls.nea;

import android.content.Context;
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

    private Context context;

    private List<String> itemList;
    private List<String> itemListFiltered;
    private SearchItemAdapterListener listener;
    private boolean showAddView;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;

        public ViewHolder(View view) {
            super(view);
            itemName = (TextView) view;

            itemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemSelected(((TextView) v).getText().toString());
                }
            });
        }
    }

    public SearchItemAdapter(List<String> itemList, SearchItemAdapterListener listener, Context context) {
        this.itemList = itemList;
        this.itemListFiltered = itemList;
        this.listener = listener;
        this.context = context;
        showAddView = true;
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
        if (position == 0 && !showAddView) {
            holder.itemName.setTextAppearance(R.style.SelectDialogItemText_Item_Emphasis);
        } else {
            holder.itemName.setTextAppearance(R.style.SelectDialogItemText_Item_Normal);
        }
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
                //noinspection unchecked
                itemListFiltered = (ArrayList<String>) results.values;
                if (itemListFiltered.size() > 0) {
                    showAddView = !itemListFiltered.get(0).toLowerCase().contentEquals(charSequence.toString().toLowerCase());
                } else {
                    showAddView = false;
                }
                listener.showAddNewView(showAddView);
                notifyDataSetChanged();
            }
        };
    }

    public interface SearchItemAdapterListener {
        void onItemSelected(String item);
        void showAddNewView(boolean showView);
    }
}
