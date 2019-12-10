package com.martinwalls.meatmanager.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.ProductQuantity;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.Utils;

import java.util.List;

public class ProductsQuantityAdapter
        extends RecyclerView.Adapter<ProductsQuantityAdapter.ViewHolder> {

    private List<ProductQuantity> productList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView productName;
        private TextView mass;
        private TextView numBoxes;

        ViewHolder(View view, Context context) {
            super(view);
            this.context = context;
            productName = view.findViewById(R.id.product_name);
            mass = view.findViewById(R.id.mass);
            numBoxes = view.findViewById(R.id.num_boxes);
        }
    }

    public ProductsQuantityAdapter(List<ProductQuantity> productList) {
        this.productList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(itemView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductQuantity productQuantity = productList.get(position);
        holder.productName.setText(productQuantity.getProduct().getProductName());
        MassUnit massUnit = MassUnit.getMassUnit(holder.context);
        holder.mass.setText(holder.context
                .getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                        Utils.getMassDisplayValue(holder.context,
                                productQuantity.getQuantityMass(), 3)));
        if (productQuantity.getQuantityBoxes() >= 0) {
            holder.numBoxes.setText(holder.context.getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            productQuantity.getQuantityBoxes(),
                            productQuantity.getQuantityBoxes()));
        } else {
            holder.numBoxes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
