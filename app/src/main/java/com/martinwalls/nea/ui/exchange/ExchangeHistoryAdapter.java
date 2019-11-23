package com.martinwalls.nea.ui.exchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Conversion;

import java.util.List;

public class ExchangeHistoryAdapter
        extends RecyclerView.Adapter<ExchangeHistoryAdapter.ViewHolder> {

    private List<Conversion> conversionList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView primaryCurrencyText;
        private TextView secondaryCurrencyText;
        private TextView dayDividerText;

        public ViewHolder(View view, Context context) {
            super(view);
            this.context = context;
            primaryCurrencyText = view.findViewById(R.id.currency_primary);
            secondaryCurrencyText = view.findViewById(R.id.currency_secondary);
            dayDividerText = view.findViewById(R.id.day_divider);
        }
    }

    ExchangeHistoryAdapter(List<Conversion> conversionList) {
        this.conversionList = conversionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exchange_history, parent, false);
        return new ViewHolder(itemView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversion conversion = conversionList.get(position);

        holder.primaryCurrencyText.setText(holder.context
                .getString(R.string.exchange_rate_history,
                        conversion.getPrimaryValue(),
                        conversion.getPrimaryCurrency().getCode()));
        holder.secondaryCurrencyText.setText(holder.context
                .getString(R.string.exchange_rate_history,
                        conversion.getSecondaryValue(),
                        conversion.getSecondaryCurrency().getCode()));

        if (position == 0) {
            holder.dayDividerText.setVisibility(View.VISIBLE);
        } else {
            Conversion lastConversion = conversionList.get(position - 1);
            if (lastConversion.getDaysAgo() == conversion.getDaysAgo()) {
                holder.dayDividerText.setVisibility(View.GONE);
            } else {
                holder.dayDividerText.setVisibility(View.VISIBLE);
            }
        }

        if (holder.dayDividerText.getVisibility() == View.VISIBLE) {
            if (conversion.getDaysAgo() == 0) {
                holder.dayDividerText.setText(holder.context
                        .getString(R.string.exchange_history_today));
            } else {
                holder.dayDividerText.setText(
                        holder.context.getResources()
                                .getQuantityString(R.plurals.exchange_history_days_ago,
                                        conversion.getDaysAgo(), conversion.getDaysAgo()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return conversionList.size();
    }
}
