package com.martinwalls.nea.ui.meat_types;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.nea.ui.BaseAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;

import java.util.List;

public class MeatTypesAdapter extends BaseAdapter<MeatTypesAdapter.ViewHolder> {

    private List<String> meatTypesList;

    private String recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    private Activity parentActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.info_primary);
        }
    }

    MeatTypesAdapter(List<String> meatTypesList, Activity parentActivity) {
        this.meatTypesList = meatTypesList;
        this.parentActivity = parentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_single_info, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String meatType = meatTypesList.get(position);
        holder.name.setText(meatType);
    }

    @Override
    public int getItemCount() {
        return meatTypesList.size();
    }

    /**
     * Deletes the meat type at the specified position. Shows a {@link Snackbar}
     * so the user can undo the action.
     */
    @Override
    public void deleteItem(int position) {
        recentlyDeletedItem = meatTypesList.get(position);
        recentlyDeletedItemPosition = position;
        DBHandler dbHandler = new DBHandler(parentActivity);
        boolean success = dbHandler.deleteMeatType(recentlyDeletedItem);
        if (success) {
            meatTypesList.remove(position);
            notifyItemRemoved(position);
            showUndoSnackbar();
        } else {
            Toast.makeText(parentActivity,
                    parentActivity.getString(R.string.db_error_delete_meat_type,
                            recentlyDeletedItem.toLowerCase()),
                    Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
        }
    }

    /**
     * Shows a {@link Snackbar} allowing the user to undo deleting a meat type.
     * Deletes the meat type from the database when the Snackbar times out or
     * is dismissed.
     */
    private void showUndoSnackbar() {
        View view = parentActivity.findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(view,
                R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo, v -> undoDelete());
        snackbar.show();
    }

    /**
     * Adds the recently deleted meat type back to its previous position in the
     * list.
     */
    private void undoDelete() {
        meatTypesList.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
        DBHandler dbHandler = new DBHandler(parentActivity);
        dbHandler.addMeatType(recentlyDeletedItem);
    }
}
