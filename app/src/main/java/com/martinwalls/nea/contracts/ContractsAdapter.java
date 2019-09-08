package com.martinwalls.nea.contracts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.ProductsQuantityAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.models.Contract;
import com.martinwalls.nea.models.Interval;

import java.util.List;

public class ContractsAdapter extends RecyclerView.Adapter<ContractsAdapter.ViewHolder> {

    private List<Contract> contractList;
    private ContractsAdapterListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contractDest, contractRepeat;
        private RecyclerView recyclerView;

        ViewHolder(View view) {
            super(view);
            contractDest = view.findViewById(R.id.contract_dest);
            contractRepeat = view.findViewById(R.id.contract_repeat);
            recyclerView = view.findViewById(R.id.recycler_view);

            LinearLayout contractLayout = view.findViewById(R.id.contract_layout);
            contractLayout.setOnClickListener(v -> listener.onContractClicked(contractList.get(getAdapterPosition())));
        }
    }

    ContractsAdapter(List<Contract> contractList, ContractsAdapterListener listener) {
        this.contractList = contractList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contract, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Contract contract = contractList.get(position);
        viewHolder.contractDest.setText(contract.getDestName());
        String repeatStr;
        Interval repeatInterval = contract.getRepeatInterval();
        int repeatOn = contract.getRepeatOn();
        String repeatOnStr;
        if (repeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnStr = viewHolder.contractRepeat.getContext().getResources()
                    .getStringArray(R.array.weekdays)[repeatOn];
        } else {
            repeatOnStr = "day " + (repeatOn + 1);
        }
        if (repeatInterval.getValue() == 1) {
            repeatStr = viewHolder.contractRepeat.getContext().getResources().getString(
                    R.string.contracts_repeat_display_one,
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        } else {
            repeatStr = viewHolder.contractRepeat.getContext().getResources().getString(
                    R.string.contracts_repeat_display_multiple,
                    repeatInterval.getValue(), repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        }
        viewHolder.contractRepeat.setText(repeatStr);
        ProductsQuantityAdapter adapter = new ProductsQuantityAdapter(contract.getProductList());
        viewHolder.recyclerView.setAdapter(adapter);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder.recyclerView.getContext()));
        // allow click events to pass to parent layout
        viewHolder.recyclerView.suppressLayout(true);
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    public interface ContractsAdapterListener {
        void onContractClicked(Contract contract);
    }
}