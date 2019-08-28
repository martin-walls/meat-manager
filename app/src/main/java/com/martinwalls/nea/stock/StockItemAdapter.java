package com.martinwalls.nea.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.models.StockItem;

import java.util.List;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.ViewHolder> {

    private List<StockItem> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemMass;
        private TextView itemNumBoxes;

        ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.name);
            itemMass = view.findViewById(R.id.mass);
            itemNumBoxes = view.findViewById(R.id.boxes);
        }
    }

    StockItemAdapter(List<StockItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StockItem item = itemList.get(position);
        holder.itemName.setText(item.getProduct().getProductName());
        //todo shared preferences for kg / lbs
        holder.itemMass.setText(holder.itemMass.getContext().getString(R.string.amount_kg, item.getMass()));
        // hide num boxes text if not specified
        if (item.getNumBoxes() >= 0) {
            holder.itemNumBoxes.setText(holder.itemNumBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes, item.getNumBoxes(), item.getNumBoxes()));
        } else {
            holder.itemNumBoxes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
