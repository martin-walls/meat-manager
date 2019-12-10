package com.martinwalls.meatmanager.ui.exchange;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.ExchangeDBHandler;
import com.martinwalls.meatmanager.data.models.Currency;
import com.martinwalls.meatmanager.ui.common.recyclerview.CustomRecyclerView;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.List;


public class ChooseCurrenciesActivity extends AppCompatActivity
        implements CurrencyAdapter.CurrencyAdapterListener {

    private ExchangeDBHandler dbHandler;

    private CurrencyAdapter currencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_currencies);

        getSupportActionBar().setTitle(R.string.exchange_choose_currencies);

        dbHandler = new ExchangeDBHandler(this);

        List<Currency> currencyList = loadCurrencies();
        initCurrenciesList(currencyList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose_currencies, menu);

        //setup search bar
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currencyAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                currencyAdapter.getFilter().filter(query);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_done:
                if (currencyAdapter.getFavCurrencyCount() >= 2) {
                    finish();
                } else {
                    Toast.makeText(this, R.string.exchange_choose_currencies_not_enough,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCurrencyFavStateChange(Currency currency) {
        dbHandler.setCurrencyFavourite(currency.getCode(), currency.isFavourite());
    }

    /**
     * Gets all currencies from the database, sorted by their currency code
     * alphabetically.
     */
    private List<Currency> loadCurrencies() {
        return SortUtils.mergeSort(
                dbHandler.getCurrencies(),
                Currency.comparatorCode());
    }

    /**
     * Initialises the view to show the list of currencies given.
     */
    private void initCurrenciesList(List<Currency> currencyList) {
        currencyAdapter = new CurrencyAdapter(currencyList, this);

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(currencyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
