package com.martinwalls.meatmanager.ui;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Abstract implementation of {@link RecyclerView.Adapter} that allows an item
 * at a certain position to be deleted.
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    /**
     * Deletes the item at the given position. This should be implemented to
     * remove the item from the list and update the layout accordingly, as well
     * as deleting the item from where it is stored, such as the database, if
     * appropriate.
     */
    public abstract void deleteItem(int position);
}
