package com.martinwalls.meatmanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.data.models.RelatedStock;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.Utils;

import java.util.List;

public class RelatedStockAdapter extends RecyclerView.Adapter<RelatedStockAdapter.ViewHolder> {

    private final int VIEW_NORMAL = 0;
    private final int VIEW_NONE = 1;

    private RelatedStockListener listener;
    private List<RelatedStock> relatedStockList;

    private int expandedItemPos = -1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView title;
        private LinearLayout childrenLayout;

        ViewHolder(View view, int viewType, Context context) {
            super(view);

            this.context = context;

            title = view.findViewById(R.id.current_stock_title);
            if (viewType == VIEW_NONE) {
                return;
            }
            childrenLayout = view.findViewById(R.id.current_stock_children);
            childrenLayout.setVisibility(View.GONE);

            // find out the max number of children that will be needed
            int maxChildren = 0;
            for (int i = 0; i < relatedStockList.size(); i++) {
                int children = relatedStockList.get(i).getStockItemList().size();
                if (children > maxChildren) {
                    maxChildren = children;
                }
            }

            // inflate enough child views so there will be enough for each item
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < maxChildren; i++) {
                View childView =
                        inflater.inflate(R.layout.item_stock_in_location, null);
                childView.setOnClickListener(childClickListener);
                childrenLayout.addView(childView);
            }

            title.setOnClickListener(titleClickListener);
        }

        /**
         * Listener to handle clicks on the item title. Expands or collapses
         * the child views depending on the current state.
         */
        private View.OnClickListener titleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prevExpandedItemPos = expandedItemPos;
                if (expandedItemPos != getAdapterPosition()) {
                    expandedItemPos = getAdapterPosition();
                    notifyItemChanged(prevExpandedItemPos);
                    notifyItemChanged(expandedItemPos);
                } else {
                    expandedItemPos = -1;
                    notifyItemChanged(prevExpandedItemPos);
                }
            }
        };

        /**
         * Listener to handle clicks on child items. Calls
         * {@link RelatedStockListener#onStockItemClicked(int)} to allow the
         * Activity to handle the click action.
         */
        private View.OnClickListener childClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView stockId = v.findViewById(R.id.stock_id);
                listener.onStockItemClicked(Integer.valueOf(stockId.getText().toString()));
            }
        };
    }

    public RelatedStockAdapter(List<RelatedStock> relatedStockList,
                               RelatedStockListener listener) {
        this.relatedStockList = relatedStockList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_NONE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_current_stock_none, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_current_stock_in_location, parent, false);
        }
        return new ViewHolder(itemView, viewType, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RelatedStock relatedStock = relatedStockList.get(position);

        MassUnit massUnit = MassUnit.getMassUnit(holder.context);
        if (getItemViewType(position) == VIEW_NONE) {
            holder.title.setText(holder.context.getString(
                    R.string.related_stock_item_none,
                    relatedStock.getProduct().getProductName()));
            return;
        }

        // set item title with name of product, total mass and number of locations
        holder.title.setText(holder.context.getResources().getQuantityString(
                massUnit == MassUnit.KG
                        ? R.plurals.related_stock_item_kg
                        : R.plurals.related_stock_item_lbs,
                relatedStock.getStockItemList().size(),
                relatedStock.getProduct().getProductName(),
                Utils.getMassDisplayValue(
                        holder.context, relatedStock.getTotalMass(), 3),
                relatedStock.getStockItemList().size()));

        if (position == expandedItemPos) {
            holder.childrenLayout.setVisibility(View.VISIBLE);
        } else {
            holder.childrenLayout.setVisibility(View.GONE);
        }

        int numChildViews = holder.childrenLayout.getChildCount();
        int numChildrenToShow = relatedStock.getStockItemList().size();

        for (int i = 0; i < numChildViews; i++) {
            View childView = holder.childrenLayout.getChildAt(i);
            // if this view is being used
            if (i < numChildrenToShow) {
                childView.setVisibility(View.VISIBLE);

                StockItem stockItem = relatedStock.getStockItemList().get(i);

                // fill fields of child view
                TextView location = childView.findViewById(R.id.location);
                location.setText(holder.context.getString(
                        R.string.stock_location_tag,
                        stockItem.getLocationName(), stockItem.getSupplierName()));

                TextView mass = childView.findViewById(R.id.mass);
                mass.setText(holder.context.getString(
                        massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                        Utils.getMassDisplayValue(holder.context, stockItem.getMass(), 3)));

                TextView numBoxes = childView.findViewById(R.id.num_boxes);
                if (stockItem.getNumBoxes() < 0) {
                    numBoxes.setVisibility(View.GONE);
                } else {
                    numBoxes.setVisibility(View.VISIBLE);
                    numBoxes.setText(holder.context.getResources().getQuantityString(
                            R.plurals.amount_boxes,
                            stockItem.getNumBoxes(), stockItem.getNumBoxes()));
                }

                // set stock id so this can be retrieved later, this is hidden
                TextView stockId = childView.findViewById(R.id.stock_id);
                stockId.setText(String.valueOf(stockItem.getStockId()));
            } else {
                // hide views not being used
                childView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (relatedStockList.get(position).getTotalMass() > 0) {
            return VIEW_NORMAL;
        } else {
            return VIEW_NONE;
        }
    }

    @Override
    public int getItemCount() {
        return relatedStockList.size();
    }

    /**
     * Interface to handle clicks on stock items.
     */
    public interface RelatedStockListener {
        /**
         * This is called when one of the children of a product item is clicked,
         * showing how much of that product is stored in a particular location.
         * This should be implemented to show more details about the stock item
         * with the specified ID.
         */
        void onStockItemClicked(int stockId);
    }
}