package com.martinwalls.nea;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExchangeHistoryAdapter extends RecyclerView.Adapter<ExchangeHistoryAdapter.ViewHolder> {

    private List<Conversion> conversionList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView primaryCurrencyText;
        private TextView secondaryCurrencyText;

        public ViewHolder(View view) {
            super(view);
            primaryCurrencyText = view.findViewById(R.id.currency_primary);
            secondaryCurrencyText = view.findViewById(R.id.currency_secondary);
        }
    }

    public ExchangeHistoryAdapter(List<Conversion> conversionList) {
        this.conversionList = conversionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exchange_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Conversion conversion = conversionList.get(position);
        viewHolder.primaryCurrencyText.setText(conversion.getPrimaryString());
        viewHolder.secondaryCurrencyText.setText(conversion.getSecondaryString());
    }

    @Override
    public int getItemCount() {
        return conversionList.size();
    }
}
