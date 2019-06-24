package com.martinwalls.nea;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    private List<Currency> currencyList = new ArrayList<>();
    private CurrencyAdapterListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView currencyText;
        private ImageButton favouritesStar;

        ViewHolder(View view) {
            super(view);
            currencyText = view.findViewById(R.id.currency_text);
            favouritesStar = view.findViewById(R.id.favourite_star);

            favouritesStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCurrencyStarClicked(currencyList.get(getLayoutPosition()));
                }
            });
        }
    }

    public CurrencyAdapter(List<Currency> currencyList, boolean onlyFavourites, CurrencyAdapterListener listener) {
        this.listener = listener;
        if (onlyFavourites) {
            for (Currency currency : currencyList) {
                if (currency.isFavourite()) {
                    this.currencyList.add(currency);
                }
            }
        } else {
            this.currencyList = currencyList;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_currencies, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Currency currency = currencyList.get(position);
        holder.currencyText.setText(currency.getName());
        holder.favouritesStar.setImageDrawable(currency.isFavourite()
                ? holder.favouritesStar.getContext().getDrawable(R.drawable.ic_star_filled)
                : holder.favouritesStar.getContext().getDrawable(R.drawable.ic_star_outline));
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public interface CurrencyAdapterListener {
        void onCurrencyStarClicked(Currency currency);
    }
}
