package com.martinwalls.nea.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;

import java.util.List;

public class MeatTypesAdapter extends RecyclerView.Adapter<MeatTypesAdapter.ViewHolder> {

    private List<String> meatTypesList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        ViewHolder(View view) {
            super(view);
            name = (TextView) view;
        }
    }

    MeatTypesAdapter(List<String> meatTypesList) {
        this.meatTypesList = meatTypesList;
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
}
