package com.martinwalls.nea.ui.exchange;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.ExchangeDBHandler;
import com.martinwalls.nea.data.models.Currency;
import com.martinwalls.nea.ui.misc.CustomRecyclerView;
import com.martinwalls.nea.util.SortUtils;

import java.util.ArrayList;
import java.util.List;


//todo search currencies?
public class ChooseCurrenciesActivity extends AppCompatActivity
        implements CurrencyAdapter.CurrencyAdapterListener {

    private ExchangeDBHandler dbHandler;

    private CurrencyAdapter currencyAdapter;
    private List<Currency> allCurrencyList;
    private List<Currency> favCurrencyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_currencies);

        getSupportActionBar().setTitle(R.string.exchange_choose_currencies);

        dbHandler = new ExchangeDBHandler(this);

        allCurrencyList = SortUtils.mergeSort(dbHandler.getCurrencies(),
                (currency1, currency2) -> currency1.getCode().compareTo(currency2.getCode()));

        // fav currencies have to be found like this so there it is the same object for currencies in both lists
        for (Currency currency : allCurrencyList) {
            if (currency.isFavourite()) {
                favCurrencyList.add(currency);
            }
        }

        currencyAdapter = new CurrencyAdapter(allCurrencyList, favCurrencyList, this);

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(currencyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose_currencies, menu);

        //setup search bar
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
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

        //todo fix starring currencies when list is filtered, as position is returned but activity
        // doesn't have filtered list. Try having adapter handle fav list, just passing it the list of
        // all currencies, then set favs in database when done action selected

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (favCurrencyList.size() >= 2) {
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
    public void onCurrencyStarClicked(int position, boolean isFavList) {
        if (isFavList) {
            dbHandler.setCurrencyFavourite(favCurrencyList.get(position).getCode(), false);
            favCurrencyList.get(position).toggleFavourite();
            favCurrencyList.remove(position);
        } else {
            Currency currency = allCurrencyList.get(position);
            if (currency.toggleFavourite()) {
                favCurrencyList.add(currency);
                dbHandler.setCurrencyFavourite(currency.getCode(), true);
            } else {
                favCurrencyList.remove(currency);
                dbHandler.setCurrencyFavourite(currency.getCode(), false);
            }
        }
        List<Currency> tempFavCurrencyList = new ArrayList<>();
        tempFavCurrencyList.addAll(favCurrencyList);
        favCurrencyList.clear();
        favCurrencyList.addAll(SortUtils.mergeSort(tempFavCurrencyList,
                (currency1, currency2) -> currency1.getCode().compareTo(currency2.getCode())));
        currencyAdapter.notifyDataSetChanged();
    }
}
