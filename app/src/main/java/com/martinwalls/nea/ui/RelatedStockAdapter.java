package com.martinwalls.nea.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.StockItem;
import com.martinwalls.nea.util.MassUnit;
import com.martinwalls.nea.util.Utils;

import java.util.List;

public class RelatedStockAdapter extends RecyclerView.Adapter<RelatedStockAdapter.ViewHolder> {
    private List<StockItem> stockItemList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView location;
        private TextView mass;
        private TextView numBoxes;

        ViewHolder(View view) {
            super(view);

            location = view.findViewById(R.id.location);
            mass = view.findViewById(R.id.mass);
            numBoxes = view.findViewById(R.id.num_boxes);
        }
    }

    public RelatedStockAdapter(List<StockItem> StockItemList) {
        this.stockItemList = StockItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_in_location, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StockItem stockItem = stockItemList.get(position);
        holder.location.setText(holder.location.getContext()
                .getString(R.string.stock_location_tag,
                        stockItem.getLocationName(), stockItem.getSupplierName())
                + stockItem.getProduct().getProductName());
        MassUnit massUnit = MassUnit.getMassUnit(holder.mass.getContext());
        holder.mass.setText(holder.mass.getContext()
                .getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                        Utils.getMassDisplayValue(holder.mass.getContext(),
                                stockItem.getMass(), 3)));
        if (stockItem.getNumBoxes() < 0) {
            holder.numBoxes.setVisibility(View.GONE);
        } else {
            holder.numBoxes.setVisibility(View.VISIBLE);
            holder.numBoxes.setText(holder.numBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            stockItem.getNumBoxes(),
                            stockItem.getNumBoxes()));
        }
    }

    @Override
    public int getItemCount() {
        return stockItemList.size();
    }
}