package com.martinwalls.meatmanager.ui.contracts.edit;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>
        implements Filterable {

    private final ProductListAdapterListener listener;

    private List<Product> productList;
    private List<Product> productListFiltered;

    private Product selectedProduct;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ViewHolder(View view) {
            super(view);

            name = (TextView) view;
            name.setOnClickListener(v ->
                    listener.onProductClicked(productListFiltered.get(getAdapterPosition())));
        }
    }

    public ProductListAdapter(ProductListAdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(productListFiltered.get(position).getProductName());
        if (selectedProduct != null) {
            if (selectedProduct.getProductId() == productListFiltered.get(position).getProductId()) {
                holder.name.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.name.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (productListFiltered == null) return 0;
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Product> filteredList = new ArrayList<>();
                if (constraint.length() == 0) {
//                    productListFiltered = productList;
                    filteredList = productList;
                } else {
                    for (Product product : productList) {
                        if (product.getProductName().toLowerCase()
                                .contains(constraint.toString().toLowerCase())) {
                            filteredList.add(product);
                        }
                    }
//                    productListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
//                filterResults.values = productListFiltered;//todo maybe can just use filteredList?
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                productListFiltered = (List<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        this.productListFiltered = productList;
        notifyDataSetChanged();
    }

    public void setSelectedProduct(Product product) {
        this.selectedProduct = product;
    }

    public interface ProductListAdapterListener {
        void onProductClicked(Product product);
    }
}
