package com.martinwalls.nea;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChooseCurrenciesActivity extends AppCompatActivity
        implements CurrencyAdapter.CurrencyAdapterListener {

    private CurrencyAdapter favCurrencyAdapter;
    private CurrencyAdapter allCurrencyAdapter;
    private List<Currency> allCurrencyList = new ArrayList<>();
    private List<Currency> favCurrencyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_currencies);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (String currencyString : SampleData.getSampleCurrencies()) {
            allCurrencyList.add(new Currency(currencyString, currencyString.equals("GBP") || currencyString.equals("HKD")));
        }

        for (Currency currency : allCurrencyList) {
            if (currency.isFavourite()) {
                favCurrencyList.add(currency);
            }
        }

        TextView favouritesEmptyView = findViewById(R.id.favourite_currencies_empty);
        CustomRecyclerView favouritesList = findViewById(R.id.favourite_currencies_list);
        favCurrencyAdapter = new CurrencyAdapter(favCurrencyList, this, true);
        favouritesList.setEmptyView(favouritesEmptyView);
        favouritesList.setAdapter(favCurrencyAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        favouritesList.setLayoutManager(layoutManager);

        CustomRecyclerView allCurrenciesList = findViewById(R.id.all_currencies_list);
        allCurrencyAdapter = new CurrencyAdapter(allCurrencyList, this, false);
        allCurrenciesList.setAdapter(allCurrencyAdapter);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this);
        allCurrenciesList.setLayoutManager(layoutManager1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //todo rest of menu
        //noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCurrencyStarClicked(int position, boolean isFavList) {
        if (isFavList) {
            favCurrencyList.get(position).toggleFavourite();
            favCurrencyList.remove(position);
        } else {
            Currency currency = allCurrencyList.get(position);
            if (currency.toggleFavourite()) {
                favCurrencyList.add(currency);
            } else {
                favCurrencyList.remove(currency);
            }
        }
        favCurrencyAdapter.notifyDataSetChanged();
        allCurrencyAdapter.notifyDataSetChanged();
    }
}
