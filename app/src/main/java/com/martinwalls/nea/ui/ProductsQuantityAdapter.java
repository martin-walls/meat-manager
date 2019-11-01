package com.martinwalls.nea.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.util.MassUnit;
import com.martinwalls.nea.util.Utils;

import java.util.List;

public class ProductsQuantityAdapter extends RecyclerView.Adapter<ProductsQuantityAdapter.ViewHolder> {

    private List<ProductQuantity> productList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, mass, numBoxes;

        ViewHolder(View view) {
            super(view);
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
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductQuantity productQuantity = productList.get(position);
        holder.productName.setText(productQuantity.getProduct().getProductName());
        MassUnit massUnit = MassUnit.getMassUnit(holder.mass.getContext());
        holder.mass.setText(holder.mass.getContext()
                .getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                        Utils.getMassDisplayValue(holder.mass.getContext(), productQuantity.getQuantityMass(), 3)));
        if (productQuantity.getQuantityBoxes() >= 0) {
            holder.numBoxes.setText(holder.numBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            productQuantity.getQuantityBoxes(), productQuantity.getQuantityBoxes()));
        } else {
            holder.numBoxes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
