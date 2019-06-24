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

    private List<Currency> currencyList;
    private List<Currency> currencyListFiltered = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView currencyText;
        private ImageButton favouritesStar;

        ViewHolder(View view) {
            super(view);
            currencyText = view.findViewById(R.id.currency_text);
            favouritesStar = view.findViewById(R.id.favourite_star);
        }
    }

    public CurrencyAdapter(List<Currency> currencyList, boolean onlyFavourites) {
        this.currencyList = currencyList;
        if (onlyFavourites) {
            for (Currency currency : currencyList) {
                if (currency.isFavourite()) {
                    currencyListFiltered.add(currency);
                }
            }
        } else {
            currencyListFiltered = currencyList;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_currencies, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Currency currency = currencyListFiltered.get(position);
        holder.currencyText.setText(currency.getName());
        holder.favouritesStar.setImageDrawable(currency.isFavourite()
                ? holder.favouritesStar.getContext().getDrawable(R.drawable.ic_star_filled)
                : holder.favouritesStar.getContext().getDrawable(R.drawable.ic_star_outline));
    }

    @Override
    public int getItemCount() {
        return currencyListFiltered.size();
    }
}
