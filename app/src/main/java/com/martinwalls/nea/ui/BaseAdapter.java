package com.martinwalls.nea.ui;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Abstract implementation of {@link RecyclerView.Adapter} that allows an item
 * at a certain position to be deleted.
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * Deletes the item at the given position.
     */
    public abstract void deleteItem(int position);
}
