package com.martinwalls.meatmanager.ui.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.ViewHolder>
        implements Filterable {

    private List<StockItem> stockItemList;
    private List<StockItem> stockItemListFiltered;
    private StockItemAdapterListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemLocation;
        private TextView itemMass;
        private TextView itemNumBoxes;

        ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.name);
            itemLocation = view.findViewById(R.id.location);
            itemMass = view.findViewById(R.id.mass);
            itemNumBoxes = view.findViewById(R.id.num_boxes);

            LinearLayout itemLayout = view.findViewById(R.id.stock_item_layout);
            itemLayout.setOnClickListener(v ->
                    listener.onStockItemClicked(
                            stockItemListFiltered.get(getAdapterPosition())));
        }
    }

    StockItemAdapter(StockItemAdapterListener listener) {
        this.listener = listener;
    }

    StockItemAdapter(List<StockItem> stockItemList, StockItemAdapterListener listener) {
        this.stockItemList = stockItemList;
        this.stockItemListFiltered = stockItemList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (stockItemListFiltered == null) {
            return;
        }
        StockItem item = stockItemListFiltered.get(position);
        holder.itemName.setText(item.getProduct().getProductName());
        holder.itemLocation.setText(holder.itemLocation.getContext()
                .getString(R.string.stock_location_tag,
                        item.getLocationName(), item.getSupplierName()));
        MassUnit massUnit = MassUnit.getMassUnit(holder.itemMass.getContext());
        holder.itemMass.setText(holder.itemMass.getContext()
                .getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                        Utils.getMassDisplayValue(
                                holder.itemMass.getContext(), item.getMass(), 3)));
        // hide num boxes text if not specified
        if (item.getNumBoxes() >= 0) {
            holder.itemNumBoxes.setText(holder.itemNumBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            item.getNumBoxes(), item.getNumBoxes()));
            holder.itemNumBoxes.setVisibility(View.VISIBLE);
        } else {
            holder.itemNumBoxes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (stockItemListFiltered != null) {
            return stockItemListFiltered.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence filterText) {
                List<StockItem> filteredList = new ArrayList<>();
                if (filterText.length() == 0) {
                    filteredList = stockItemList;
                } else {
                    for (StockItem stockItem : stockItemList) {
                        if (stockItem.getProduct().getProductName().toLowerCase()
                                .contains(filterText.toString().toLowerCase())) {
                            filteredList.add(stockItem);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence filterText, FilterResults results) {
                //noinspection unchecked
                stockItemListFiltered = (List<StockItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setStockList(List<StockItem> stockList) {
        this.stockItemList = stockList;
        this.stockItemListFiltered = stockList;
        notifyDataSetChanged();
    }

    /**
     * Interface to handle clicks on stock items.
     */
    public interface StockItemAdapterListener {
        /**
         * This is called when a stock item is clicked.
         */
        void onStockItemClicked(StockItem stockItem);
    }
}
