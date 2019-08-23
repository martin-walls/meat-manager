package com.martinwalls.nea.stock;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;

import java.util.List;

public class MeatTypesAdapter extends RecyclerView.Adapter<MeatTypesAdapter.ViewHolder> {

    private List<String> meatTypesList;

    private String recentlyDeletedItem;
    private int recentlyDeletedItemPosition;

    private Activity parentActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
        }
    }

    MeatTypesAdapter(List<String> meatTypesList, Activity parentActivity) {
        this.meatTypesList = meatTypesList;
        this.parentActivity = parentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meat_type, parent, false);
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

    public void deleteItem(int position) {
        recentlyDeletedItem = meatTypesList.get(position);
        recentlyDeletedItemPosition = position;
        meatTypesList.remove(position);
        notifyItemRemoved(position);
        DBHandler dbHandler = new DBHandler(parentActivity);
        dbHandler.deleteMeatType(recentlyDeletedItem);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = parentActivity.findViewById(R.id.root_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_item_deleted, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        meatTypesList.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
        DBHandler dbHandler = new DBHandler(parentActivity);
        dbHandler.addMeatType(recentlyDeletedItem);
    }
}
