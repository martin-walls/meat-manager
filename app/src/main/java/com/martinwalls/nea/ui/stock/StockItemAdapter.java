package com.martinwalls.nea.ui.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.StockItem;
import com.martinwalls.nea.util.MassUnit;
import com.martinwalls.nea.util.Utils;

import java.util.List;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.ViewHolder> {

    // todo use Utils.roundToDp method instead?
    private final String DECIMAL_FORMAT_PATTERN = "#.0##";

    private List<StockItem> itemList;
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
            itemLayout.setOnClickListener(v -> listener.onStockItemClicked(itemList.get(getAdapterPosition())));
        }
    }

    StockItemAdapter(List<StockItem> itemList, StockItemAdapterListener listener) {
        this.itemList = itemList;
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
        StockItem item = itemList.get(position);
        holder.itemName.setText(item.getProduct().getProductName());
        holder.itemLocation.setText(holder.itemLocation.getContext()
                .getString(R.string.stock_location_tag, item.getLocationName(), item.getSupplierName()));
        MassUnit massUnit = MassUnit.getMassUnit(holder.itemMass.getContext());
        holder.itemMass.setText(holder.itemMass.getContext()
                .getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                        Utils.getMassDisplayValue(holder.itemMass.getContext(), item.getMass(), 3)));
        // hide num boxes text if not specified
        if (item.getNumBoxes() >= 0) {
            holder.itemNumBoxes.setText(holder.itemNumBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes, item.getNumBoxes(), item.getNumBoxes()));
            holder.itemNumBoxes.setVisibility(View.VISIBLE);
        } else {
            holder.itemNumBoxes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
