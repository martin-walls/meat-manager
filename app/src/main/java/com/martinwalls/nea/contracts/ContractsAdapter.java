package com.martinwalls.nea.contracts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.models.Contract;

import java.util.List;

public class ContractsAdapter extends RecyclerView.Adapter<ContractsAdapter.ViewHolder> {
    private List<Contract> contractList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View view) {
            super(view);
        }
    }

    ContractsAdapter(List<Contract> contractList) {
        this.contractList = contractList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contract, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //TODO Add your logic for binding the view
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }
}