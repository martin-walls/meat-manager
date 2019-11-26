package com.martinwalls.meatmanager.ui.contracts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.meatmanager.ui.ProductsQuantityAdapter;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Interval;

import java.util.List;

public class ContractsAdapter extends RecyclerView.Adapter<ContractsAdapter.ViewHolder> {

    private List<Contract> contractList;
    private ContractsAdapterListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contractDest;
        private TextView contractRepeat;
        private RecyclerView recyclerView;
        private TextView dateDivider;

        ViewHolder(View view) {
            super(view);
            contractDest = view.findViewById(R.id.contract_dest);
            contractRepeat = view.findViewById(R.id.contract_repeat);
            recyclerView = view.findViewById(R.id.recycler_view);
            dateDivider = view.findViewById(R.id.date_divider);

            LinearLayout contractLayout = view.findViewById(R.id.contract_info);
            contractLayout.setOnClickListener(v ->
                    listener.onContractClicked(contractList.get(getAdapterPosition())));
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contract contract = contractList.get(position);
        holder.contractDest.setText(contract.getDestName());
//        holder.contractDest.setText(contract.getDaysToNextRepeat() + "");
        String repeatStr;
        Interval repeatInterval = contract.getRepeatInterval();
        int repeatOn = contract.getRepeatOn();
        String repeatOnStr;
        if (repeatInterval.getUnit() == Interval.TimeUnit.WEEK) {
            repeatOnStr = holder.contractRepeat.getContext().getResources()
                    .getStringArray(R.array.weekdays)[repeatOn - 1];
        } else {
            repeatOnStr = "day " + repeatOn;
        }
        if (repeatInterval.getValue() == 1) {
            repeatStr = holder.contractRepeat.getContext().getResources().getString(
                    R.string.contracts_repeat_display_one,
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        } else {
            repeatStr = holder.contractRepeat.getContext().getResources().getString(
                    R.string.contracts_repeat_display_multiple,
                    repeatInterval.getValue(),
                    repeatInterval.getUnit().name().toLowerCase(), repeatOnStr);
        }
        holder.contractRepeat.setText(repeatStr);
        ProductsQuantityAdapter adapter =
                new ProductsQuantityAdapter(contract.getProductList());
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(
                new LinearLayoutManager(holder.recyclerView.getContext()));
        // allow click events to pass to parent layout
        holder.recyclerView.suppressLayout(true);

        int daysToNextRepeat = contract.getDaysToNextRepeat();
        if (position == 0
                || contractList.get(position - 1).getDaysToNextRepeat() != daysToNextRepeat) {
            switch (daysToNextRepeat) {
                case 0:
                    holder.dateDivider.setText(R.string.contracts_divider_today);
                    break;
                case 1:
                    holder.dateDivider.setText(R.string.contracts_divider_tomorrow);
                    break;
                default:
                    holder.dateDivider.setText(holder.dateDivider.getContext().getString(
                            R.string.contracts_divider_days_until,
                            contract.getDaysToNextRepeat()));
                    break;
            }
            holder.dateDivider.setVisibility(View.VISIBLE);
        } else {
            holder.dateDivider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contractList.size();
    }

    /**
     * Interface to handle clicks on contract items.
     */
    public interface ContractsAdapterListener {
        /**
         * This is called when a contract in the list is clicked.
         */
        void onContractClicked(Contract contract);
    }
}