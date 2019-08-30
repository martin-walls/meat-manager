package com.martinwalls.nea.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.models.ProductQuantity;

import java.util.List;

import static android.view.View.GONE;

public class ProductsAddedAdapter extends RecyclerView.Adapter<ProductsAddedAdapter.ViewHolder> {

    private List<ProductQuantity> productList;
    ProductsAddedAdapterListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, mass, numBoxes;
        private ImageButton deleteBtn;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.product_name);
            mass = view.findViewById(R.id.mass);
            numBoxes = view.findViewById(R.id.num_boxes);
            deleteBtn = view.findViewById(R.id.btn_delete);

            deleteBtn.setOnClickListener(v -> listener.deleteProductAdded(getAdapterPosition()));
        }
    }

    ProductsAddedAdapter(List<ProductQuantity> productList, ProductsAddedAdapterListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_added, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ProductQuantity productQuantity = productList.get(position);
        viewHolder.name.setText(productQuantity.getProduct().getProductName());
        viewHolder.mass.setText(
                viewHolder.mass.getContext().getString(R.string.amount_kg, productQuantity.getQuantityMass()));
        if (productQuantity.getQuantityBoxes() < 0) {
            viewHolder.numBoxes.setVisibility(GONE);
        } else {
            viewHolder.numBoxes.setText(viewHolder.numBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            productQuantity.getQuantityBoxes(), productQuantity.getQuantityBoxes()));
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface ProductsAddedAdapterListener {
        void deleteProductAdded(int position);
    }
}
