package com.martinwalls.nea.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.models.ProductQuantity;

import java.text.DecimalFormat;
import java.util.List;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.ViewHolder> {

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

    OrderProductsAdapter(List<ProductQuantity> productList) {
        this.productList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ProductQuantity productQuantity = productList.get(position);
        viewHolder.productName.setText(productQuantity.getProduct().getProductName());
        viewHolder.mass.setText(
                viewHolder.mass.getContext().getString(R.string.amount_kg,
                        new DecimalFormat("#.0###").format(productQuantity.getQuantityMass())));
        if (productQuantity.getQuantityBoxes() >= 0) {
            viewHolder.numBoxes.setText(viewHolder.numBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            productQuantity.getQuantityBoxes(), productQuantity.getQuantityBoxes()));
        } else {
            viewHolder.numBoxes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
