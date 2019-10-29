package com.martinwalls.nea.ui.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.StockItem;

import java.text.DecimalFormat;
import java.util.List;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.ViewHolder> {

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
//        holder.itemLocation.setText(item.getLocationName());
        holder.itemLocation.setText(holder.itemLocation.getContext()
                .getString(R.string.stock_location_tag, item.getLocationName(), item.getSupplierName()));
        //todo shared preferences for kg / lbs
        holder.itemMass.setText(holder.itemMass.getContext().getString(R.string.amount_kg,
                new DecimalFormat("#.0###").format(item.getMass())));
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

    public interface StockItemAdapterListener {
        void onStockItemClicked(StockItem stockItem);
    }
}
