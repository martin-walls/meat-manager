package com.martinwalls.nea;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChooseCurrenciesActivity extends AppCompatActivity {

    private CurrencyAdapter currencyAdapterFav;
    private CurrencyAdapter currencyAdapterAll;
    private List<Currency> currencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_currencies);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //todo recycler views with item_choose_currencies
        //todo star/unstar change drawable

        currencyList = new ArrayList<>();
        for (String currencyString : SampleData.getSampleCurrencies()) {
            currencyList.add(new Currency(currencyString, currencyString.equals("GBP") || currencyString.equals("HKD")));
        }

        TextView favouritesEmptyView = findViewById(R.id.favourite_currencies_empty);
        CustomRecyclerView favouritesList = findViewById(R.id.favourite_currencies_list);
        currencyAdapterFav = new CurrencyAdapter(currencyList, true);
        favouritesList.setEmptyView(favouritesEmptyView);
        favouritesList.setAdapter(currencyAdapterFav);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        favouritesList.setLayoutManager(layoutManager);

        CustomRecyclerView allCurrenciesList = findViewById(R.id.all_currencies_list);
        currencyAdapterAll = new CurrencyAdapter(currencyList, false);
        allCurrenciesList.setAdapter(currencyAdapterAll);
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
}
