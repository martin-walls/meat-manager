package com.martinwalls.meatmanager.ui.products;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.ui.common.adapter.BaseAdapter;

import java.util.List;

public class ProductsAdapter extends BaseAdapter<ProductsAdapter.ViewHolder> {

    private List<Product> productList;

    private Product recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    private Activity parentActivity; // needed to show snackbar message

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, meatType;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.info_primary);
            meatType = view.findViewById(R.id.info_secondary);
        }
    }

    ProductsAdapter(List<Product> productList, Activity parentActivity) {
        this.productList = productList;
        this.parentActivity = parentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_secondary_info, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Product product = productList.get(position);
        viewHolder.name.setText(product.getProductName());
        viewHolder.meatType.setText(product.getMeatType());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Removes the product at the specified position if it is safe to do so.
     * Shows a {@link Snackbar} to allow the user to undo the action.
     */
    @Override
    public void deleteItem(int position) {
        DBHandler dbHandler = new DBHandler(parentActivity);
        boolean safeToDelete = dbHandler.isProductSafeToDelete(
                productList.get(position).getProductId());
        if (safeToDelete) {
            recentlyDeletedItem = productList.get(position);
            recentlyDeletedItemPosition = position;
            productList.remove(position);
            notifyItemRemoved(position);
            showUndoSnackbar();
        } else {
            Toast.makeText(parentActivity,
                    parentActivity.getString(R.string.db_error_delete_product,
                            productList.get(position).getProductName()),
                    Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
        }
    }

    /**
     * Shows a {@link Snackbar} allowing the user to undo deleting a product.
     * Deletes the product from the database when the snackbar times out or
     * is dismissed.
     */
    private void showUndoSnackbar() {
        View view = parentActivity.findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(view,
                R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo, v -> undoDelete());
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_MANUAL) {
                    DBHandler dbHandler = new DBHandler(parentActivity);
                    dbHandler.deleteProduct(recentlyDeletedItem.getProductId());
                }

            }
        });
        snackbar.show();
    }

    /**
     * Adds the recently deleted product back to its previous position in the
     * list.
     */
    private void undoDelete() {
        productList.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
    }
}
