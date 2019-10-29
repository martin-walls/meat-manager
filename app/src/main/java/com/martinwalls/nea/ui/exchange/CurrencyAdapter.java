package com.martinwalls.nea.ui.exchange;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Currency;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    @SuppressWarnings("FieldCanBeLocal")
    private final int STANDARD_VIEW = 0;
    private final int SECTION_VIEW = 1;
    private final int SECTION_VIEW_NO_FAV = 2;

    private List<Currency> currencyList;
    private List<Currency> favCurrencyList;
    private CurrencyAdapterListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView divider;
        private TextView sectionTitle;

        private TextView currencyCode;
        private TextView currencyName;
        private ImageButton favouritesStar;

        ViewHolder(View view, int viewType) {
            super(view);
            if (viewType == SECTION_VIEW) {
                divider = view.findViewById(R.id.divider);
                sectionTitle = view.findViewById(R.id.section_title);
            }

            currencyCode = view.findViewById(R.id.currency_code);
            currencyName = view.findViewById(R.id.currency_name);
            favouritesStar = view.findViewById(R.id.favourite_star);

            favouritesStar.setOnClickListener(v -> {
                if (getLayoutPosition() < favCurrencyList.size()) {
                    listener.onCurrencyStarClicked(getLayoutPosition(), true);
                } else {
                    listener.onCurrencyStarClicked(getLayoutPosition() - favCurrencyList.size(), false);
                }
            });
        }
    }

    CurrencyAdapter(List<Currency> currencyList, List<Currency> favCurrencyList,
                           CurrencyAdapterListener listener) {
        this.currencyList = currencyList;
        this.favCurrencyList = favCurrencyList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SECTION_VIEW) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_currencies_section, parent, false);
        } else if (viewType == SECTION_VIEW_NO_FAV) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_currencies_section_no_fav, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_currencies, parent, false);
        }
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == SECTION_VIEW) {
            if (position == 0) {
                holder.divider.setVisibility(View.GONE);
                holder.sectionTitle.setText(R.string.exchange_favourites);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
                holder.sectionTitle.setText(R.string.exchange_all_currencies);
            }
        }

        Currency currency;
        if (position < favCurrencyList.size()) {
            currency = favCurrencyList.get(position);
        } else {
            currency = currencyList.get(position - favCurrencyList.size());
        }
        holder.currencyCode.setText(currency.getCode());
        holder.currencyName.setText(currency.getName());
        holder.favouritesStar.setImageDrawable(currency.isFavourite()
                ? holder.favouritesStar.getContext().getDrawable(R.drawable.ic_star_filled)
                : holder.favouritesStar.getContext().getDrawable(R.drawable.ic_star_outline));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && favCurrencyList.size() == 0) {
            return SECTION_VIEW_NO_FAV;
        } else if (position == 0 || position == favCurrencyList.size()) {
            return SECTION_VIEW;
        } else {
            return STANDARD_VIEW;
        }
    }

    @Override
    public int getItemCount() {
//        return currencyList.size();
        return currencyList.size() + favCurrencyList.size();
    }

    public interface CurrencyAdapterListener {
        void onCurrencyStarClicked(int position, boolean isFavList);
    }
}
