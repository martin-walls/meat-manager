package com.martinwalls.nea.ui.exchange;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Currency;
import com.martinwalls.nea.util.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder>
        implements Filterable {

    @SuppressWarnings("FieldCanBeLocal")
    private final int VIEW_STANDARD = 0;
    private final int VIEW_SECTION = 1;
    private final int VIEW_SECTION_NO_FAV = 2;
    private final int VIEW_SECTION_NO_RESULTS = 3;

    private List<Currency> currencyList;
    private List<Currency> currencyListFiltered;
    private List<Currency> favCurrencyList = new ArrayList<>();
    private CurrencyAdapterListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView divider;
        private TextView sectionTitle;

        private TextView currencyCode;
        private TextView currencyName;
        private ImageButton favouritesStar;

        ViewHolder(View view, int viewType) {
            super(view);
            if (viewType == VIEW_SECTION) {
                divider = view.findViewById(R.id.divider);
                sectionTitle = view.findViewById(R.id.section_title);
            }

            currencyCode = view.findViewById(R.id.currency_code);
            currencyName = view.findViewById(R.id.currency_name);
            favouritesStar = view.findViewById(R.id.favourite_star);

            favouritesStar.setOnClickListener(v -> {
                if (getLayoutPosition() < favCurrencyList.size()) {
                    onStarClicked(getLayoutPosition(), true);
                } else {
                    onStarClicked(getLayoutPosition() - favCurrencyList.size(), false);
                }
            });
        }
    }

    CurrencyAdapter(List<Currency> currencyList, CurrencyAdapterListener listener) {
        this.currencyList = currencyList;
        this.currencyListFiltered = currencyList;

        for (Currency currency : currencyList) {
            if (currency.isFavourite()) {
                favCurrencyList.add(currency);
            }
        }

        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_SECTION:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_currencies_section, parent, false);
                break;
            case VIEW_SECTION_NO_FAV:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_currencies_section_no_fav, parent, false);
                break;
            case VIEW_SECTION_NO_RESULTS:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_currencies_no_results, parent, false);
                break;
            case VIEW_STANDARD:
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_currencies, parent, false);
                break;
        }
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_SECTION) {
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
            currency = currencyListFiltered.get(position - favCurrencyList.size());
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
            return VIEW_SECTION_NO_FAV;
        } else if (position == favCurrencyList.size() - 1
                && currencyListFiltered.size() == 0) {
            return VIEW_SECTION_NO_RESULTS;
        } else if (position == 0 || position == favCurrencyList.size()) {
            return VIEW_SECTION;
        } else {
            return VIEW_STANDARD;
        }
    }

    @Override
    public int getItemCount() {
        return currencyListFiltered.size() + favCurrencyList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence filterText) {
                List<Currency> filteredList = new ArrayList<>();
                if (filterText.length() == 0) {
                    filteredList = currencyList;
                } else {
                    for (Currency currency : currencyList) {
                        if (currency.getCode().toLowerCase()
                                .contains(filterText.toString().toLowerCase())
                                || currency.getName().toLowerCase()
                                .contains(filterText.toString().toLowerCase())) {
                            filteredList.add(currency);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence filterText, FilterResults results) {
                //noinspection unchecked
                currencyListFiltered = (List<Currency>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * Returns the number of favourite currencies.
     */
    int getFavCurrencyCount() {
        return favCurrencyList.size();
    }

    /**
     * Handles clicks on the favourite button for the currency at {@code position}.
     * Toggles the favourite status of the currency and displays it in the
     * according list(s).
     *
     * <p>Calls {@link CurrencyAdapterListener#onCurrencyFavStateChange} to notify
     * the activity of the state change.
     */
    private void onStarClicked(int position, boolean isFavList) {
        if (isFavList) {
            favCurrencyList.get(position).toggleFavourite();
            listener.onCurrencyFavStateChange(favCurrencyList.get(position));
            favCurrencyList.remove(position);
        } else {
            Currency currency = currencyListFiltered.get(position);
            if (currency.toggleFavourite()) {
                favCurrencyList.add(currency);
            } else {
                favCurrencyList.remove(currency);
            }
            listener.onCurrencyFavStateChange(currency);
        }
        List<Currency> tempFavCurrencyList = new ArrayList<>(favCurrencyList);
        favCurrencyList.clear();
        favCurrencyList.addAll(
                SortUtils.mergeSort(tempFavCurrencyList, Currency.comparatorCode()));
        notifyDataSetChanged();
    }

    /**
     * Interface to update the favourite state of a currency when the user
     * favourites / unfavourites it.
     */
    public interface CurrencyAdapterListener {
        /**
         * Called when a currency changes its favourite state. Should be
         * implemented to store the new value.
         */
        void onCurrencyFavStateChange(Currency currency);
    }
}
