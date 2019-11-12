package com.martinwalls.nea.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.util.MassUnit;
import com.martinwalls.nea.util.Utils;

import java.util.List;

public class ProductsAddedAdapter extends RecyclerView.Adapter<ProductsAddedAdapter.ViewHolder> {

    private List<ProductQuantity> productList;
    private ProductsAddedAdapterListener listener;

    private boolean showEditBtn = true;
    private boolean showDeleteBtn = true;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, mass, numBoxes;
        private ImageButton deleteBtn, editBtn;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.product_name);
            mass = view.findViewById(R.id.mass);
            numBoxes = view.findViewById(R.id.num_boxes);
            deleteBtn = view.findViewById(R.id.btn_delete);
            editBtn = view.findViewById(R.id.btn_edit);

            deleteBtn.setOnClickListener(v -> listener.onProductAddedDelete(getAdapterPosition()));
            editBtn.setOnClickListener(v -> listener.onProductAddedEdit(getAdapterPosition()));
        }
    }

    /**
     * Constructor for when both buttons are hidden, so no listener is required.
     */
    public ProductsAddedAdapter(List<ProductQuantity> productList) {
        this.productList = productList;
        showEditBtn = false;
        showDeleteBtn = false;
    }

    public ProductsAddedAdapter(List<ProductQuantity> productList, ProductsAddedAdapterListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    /**
     * Constructor to set which buttons should be showing.
     */
    public ProductsAddedAdapter(List<ProductQuantity> productList, ProductsAddedAdapterListener listener,
                                boolean showEditBtn, boolean showDeleteBtn) {
        this.productList = productList;
        this.listener = listener;
        this.showEditBtn = showEditBtn;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_options, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductQuantity productQuantity = productList.get(position);
        holder.name.setText(productQuantity.getProduct().getProductName());
        MassUnit massUnit = MassUnit.getMassUnit(holder.mass.getContext());
        holder.mass.setText(holder.mass.getContext()
                .getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                        Utils.getMassDisplayValue(holder.mass.getContext(), productQuantity.getQuantityMass(), 3)));
        if (productQuantity.getQuantityBoxes() < 0) {
            holder.numBoxes.setVisibility(View.GONE);
        } else {
            holder.numBoxes.setText(holder.numBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            productQuantity.getQuantityBoxes(), productQuantity.getQuantityBoxes()));
        }
        if (!showDeleteBtn) {
            holder.deleteBtn.setVisibility(View.GONE);
        }
        if (!showEditBtn) {
            holder.editBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Interface to handle clicks on the delete and edit buttons.
     */
    public interface ProductsAddedAdapterListener {
        /**
         * Called when the delete button for the item at this position is clicked.
         * This doesn't need to be implemented if the button is hidden.
         */
        default void onProductAddedDelete(int position) {}

        /**
         * Called when the edit button for the item at this position is clicked.
         * This doesn't need to be implemented if the button is hidden.
         */
        default void onProductAddedEdit(int position) {}
    }
}
