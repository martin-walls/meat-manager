package com.martinwalls.nea.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.ProductQuantity;

import java.text.DecimalFormat;
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

    // when both buttons are hidden, no listener required
    public ProductsAddedAdapter(List<ProductQuantity> productList) {
        this.productList = productList;
        showEditBtn = false;
        showDeleteBtn = false;
    }

    public ProductsAddedAdapter(List<ProductQuantity> productList, ProductsAddedAdapterListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ProductQuantity productQuantity = productList.get(position);
        viewHolder.name.setText(productQuantity.getProduct().getProductName());
        viewHolder.mass.setText(viewHolder.mass.getContext().getString(R.string.amount_kg,
                new DecimalFormat("#.0###").format(productQuantity.getQuantityMass())));
        if (productQuantity.getQuantityBoxes() < 0) {
            viewHolder.numBoxes.setVisibility(View.GONE);
        } else {
            viewHolder.numBoxes.setText(viewHolder.numBoxes.getContext().getResources()
                    .getQuantityString(R.plurals.amount_boxes,
                            productQuantity.getQuantityBoxes(), productQuantity.getQuantityBoxes()));
        }
        if (!showDeleteBtn) {
            viewHolder.deleteBtn.setVisibility(View.GONE);
        }
        if (!showEditBtn) {
            viewHolder.editBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface ProductsAddedAdapterListener {
        default void onProductAddedDelete(int position) {}
        default void onProductAddedEdit(int position) {}
    }
}
