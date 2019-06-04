package com.martinwalls.nea;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    //todo filterable

    private List<String> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;

        public ViewHolder(View view) {
            super(view);
            itemName = (TextView) view;
        }
    }

    public ItemAdapter(List<String> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = new TextView(parent.getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((TextView) itemView).setTextAppearance(R.style.SelectDialogItemText_Item);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = itemList.get(position);
        holder.itemName.setText(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
