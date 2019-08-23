package com.martinwalls.nea;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public abstract void deleteItem(int position);
}
