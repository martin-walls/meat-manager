package com.martinwalls.nea.ui.exchange;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.api.ApiIntentService;
import com.martinwalls.nea.data.db.ExchangeDBHandler;
import com.martinwalls.nea.data.models.Conversion;
import com.martinwalls.nea.data.models.Currency;
import com.martinwalls.nea.ui.MainActivity;
import com.martinwalls.nea.ui.misc.CustomRecyclerView;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.SimpleTextWatcher;
import com.martinwalls.nea.util.SortUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExchangeFragment extends Fragment {

    private static final int REQUEST_REFRESH_ON_DONE = 1;

    private ExchangeDBHandler dbHandler;
    private EasyPreferences prefs;

    private LinearLayout ratesLayout;
    private TextView emptyView;

    private TextView primaryCurrencyText;
    private TextView secondaryCurrencyText;
    private TextView secondaryCurrencyValueText;
    private EditText primaryCurrencyValueInput;

    private NumberPicker currencyPickerLeft;
    private NumberPicker currencyPickerRight;

    private String primaryCurrency;
    private double primaryCurrencyValue = 1;
    private String secondaryCurrency;
    private double secondaryCurrencyValue = 0;

    private HashMap<String, Double> rates = new HashMap<>();

    private ExchangeHistoryAdapter exchangeHistoryAdapter;
    private List<Conversion> conversionList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.exchange_title);
        View fragmentView = inflater.inflate(R.layout.fragment_exchange, container, false);

        dbHandler = new ExchangeDBHandler(getContext());
        prefs = EasyPreferences.createForDefaultPreferences(getContext());

        ratesLayout = fragmentView.findViewById(R.id.rates_layout);
        emptyView = fragmentView.findViewById(R.id.empty);
        ratesLayout.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);

        checkConnectionAndFetchRates();

        primaryCurrencyText = fragmentView.findViewById(R.id.currency_primary);
        secondaryCurrencyText = fragmentView.findViewById(R.id.currency_secondary);
        secondaryCurrencyValueText = fragmentView.findViewById(R.id.currency_secondary_value);

        currencyPickerLeft = fragmentView.findViewById(R.id.currency_picker_left);
        currencyPickerRight = fragmentView.findViewById(R.id.currency_picker_right);

//        initCurrencyPickers();

        /*
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getString(R.string.pref_exchange_file_key), Context.MODE_PRIVATE);
        String lastCurrencyLeft = sharedPref.getString(getString(R.string.pref_exchange_currency_primary),
                currencyPickerLeft.getDisplayedValues()[0]);
        String lastCurrencyRight = sharedPref.getString(getString(R.string.pref_exchange_currency_secondary),
                currencyPickerRight.getDisplayedValues()[1]);

        currencyPickerLeft.setValue(currenciesList.indexOf(lastCurrencyLeft));
        currencyPickerRight.setValue(currenciesList.indexOf(lastCurrencyRight));
        */

        ImageButton swapBtn = fragmentView.findViewById(R.id.swap_currencies);
        swapBtn.setOnClickListener(v -> swapCurrencies());

//        List<Conversion> conversionList = SortUtils.mergeSort(dbHandler.getAllConversions(),
//                (conv1, conv2) -> (int) (conv2.getTimestamp() - conv1.getTimestamp()));
        exchangeHistoryAdapter = new ExchangeHistoryAdapter(conversionList);
        loadConversionHistory();

        TextView emptyView = fragmentView.findViewById(R.id.no_exchange_history);
        CustomRecyclerView conversionHistoryView = fragmentView.findViewById(R.id.exchange_history);
        conversionHistoryView.setAdapter(exchangeHistoryAdapter);
        conversionHistoryView.setEmptyView(emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        conversionHistoryView.setLayoutManager(layoutManager);

        primaryCurrencyValueInput = fragmentView.findViewById(R.id.currency_primary_value);
        primaryCurrencyValueInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    primaryCurrencyValue = Double.parseDouble(s.toString());
                    updateRates();
                }
            }
        });
        primaryCurrencyValueInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addConversionToHistory();
                // returning false means keyboard will be closed
                return false;
            }
            return false;
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_exchange, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favourites:
                Intent favouritesIntent = new Intent(getContext(), ChooseCurrenciesActivity.class);
                startActivityForResult(favouritesIntent, REQUEST_REFRESH_ON_DONE);
                return true;
            case R.id.action_refresh:
                checkConnectionAndFetchRates();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REFRESH_ON_DONE) {
            initCurrencyPickers();
        }
    }

    /**
     * Checks whether the device has an active Internet connection.
     */
    private boolean checkInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo(); //deprecated

//        return network != null && network.isConnectedOrConnecting();

        return cm.getActiveNetwork() != null;
    }

    /**
     * Runs {@link ApiIntentService} to fetch the latest exchange rates from
     * the API.
     */
    private void fetchRatesFromApi() {
        PendingIntent pendingResult = getActivity().createPendingResult(
                MainActivity.REQUEST_EXCHANGE_API_SERVICE, new Intent(), 0);
        Intent intent = new Intent(getContext().getApplicationContext(), ApiIntentService.class);
        intent.putExtra(ApiIntentService.EXTRA_PENDING_RESULT, pendingResult);
        getActivity().startService(intent);
    }

    /**
     * Fetches latest exchange rates from the API, making sure there is an
     * active Internet connection beforehand.
     */
    private void checkConnectionAndFetchRates() {
        if (checkInternetConnection()) {
            fetchRatesFromApi();
            emptyView.setText(R.string.exchange_loading);
        } else {
            ratesLayout.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.exchange_no_connection);
        }
    }

    /**
     * Called when {@link ApiIntentService} returns the exchange rates. Updates
     * the layout to show the updated rates.
     */
    public void onRatesFetched(Intent data) {
        rates = (HashMap<String, Double>) data.getSerializableExtra(ApiIntentService.EXTRA_RESULT);
        updateRates();

        initCurrencyPickers();

        ratesLayout.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

//        long timestamp = Long.parseLong(CacheHelper.retrieve(getContext(), "last_cache_timestamp"));
//        Toast.makeText(getContext(), "fetched", Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets all favourited currencies from the database, as these are the ones
     * that should be shown on the exchange screen.
     */
    private String[] fetchCurrenciesToShow() {
        List<Currency> currencies = SortUtils.mergeSort(dbHandler.getFavCurrencies(),
                (currency1, currency2) -> currency1.getCode().compareTo(currency2.getCode()));

        String[] currencyCodes = new String[currencies.size()];
        for (int i = 0; i < currencies.size(); i++) {
            currencyCodes[i] = currencies.get(i).getCode();
        }
        return currencyCodes;
    }

    /**
     * Initialises the currency pickers to show all the favourited currencies
     * in the database.
     */
    private void initCurrencyPickers() {
        String[] currencies = fetchCurrenciesToShow();
        if (currencies.length == 0) {
            return;
        }

        // load last selected currencies
        String primaryCurrencyValue = prefs.getString(R.string.pref_exchange_currency_primary, "");
        String secondaryCurrencyValue = prefs.getString(R.string.pref_exchange_currency_secondary, "");

        currencyPickerLeft.setMinValue(0);
        currencyPickerLeft.setMaxValue(currencies.length - 1);
        currencyPickerLeft.setDisplayedValues(currencies);
        currencyPickerLeft.setWrapSelectorWheel(false);
        currencyPickerLeft.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerLeft.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setPrimaryCurrency(currencies[newVal]));

        currencyPickerRight.setMinValue(0);
        currencyPickerRight.setMaxValue(currencies.length - 1);
        currencyPickerRight.setDisplayedValues(currencies);
        currencyPickerRight.setWrapSelectorWheel(false);
        currencyPickerRight.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerRight.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setSecondaryCurrency(currencies[newVal]));

        // select last selected currencies
        List<String> currencyList = Arrays.asList(currencies);
        if (currencyList.contains(primaryCurrencyValue)) {
            currencyPickerLeft.setValue(currencyList.indexOf(primaryCurrencyValue));
        }
        if (currencyList.contains(secondaryCurrencyValue)) {
            currencyPickerRight.setValue(currencyList.indexOf(secondaryCurrencyValue));
        }

        setPrimaryCurrency(currencies[currencyPickerLeft.getValue()]);
        setSecondaryCurrency(currencies[currencyPickerRight.getValue()]);
    }

    /**
     * Sets the primary (left) currency to the specified currency.
     */
    private void setPrimaryCurrency(String currency) {
        primaryCurrency = currency;
        primaryCurrencyText.setText(currency);
        prefs.setString(R.string.pref_exchange_currency_primary, currency);
        updateRates();
    }

    /**
     * Sets the secondary (right) currency to the specified currency.
     */
    private void setSecondaryCurrency(String currency) {
        secondaryCurrency = currency;
        secondaryCurrencyText.setText(currency);
        prefs.setString(R.string.pref_exchange_currency_secondary, currency);
        updateRates();
    }

    /**
     * Calculates the exchange rate for {@code currency} relative to the
     * currency {@code base}.
     */
    private double getRate(String currency, String base) {
        if (rates != null && rates.keySet().contains(currency) && rates.keySet().contains(base)) {
            return (rates.get(currency) / rates.get(base)) * primaryCurrencyValue;
        }
        return 0;
    }

    /**
     * Updates the displayed rates.
     */
    private void updateRates() {
        secondaryCurrencyValue = getRate(secondaryCurrency, primaryCurrency);
        secondaryCurrencyValueText.setText(getString(R.string.exchange_rate, secondaryCurrencyValue));
    }

    /**
     * Swaps the primary and secondary currencies.
     */
    private void swapCurrencies() {
        String primaryTemp = primaryCurrency;
        setPrimaryCurrency(secondaryCurrency);
        setSecondaryCurrency(primaryTemp);

        int leftValue = currencyPickerLeft.getValue();
        currencyPickerLeft.setValue(currencyPickerRight.getValue());
        currencyPickerRight.setValue(leftValue);
    }

    /**
     * Adds a new conversion to the history.
     */
    private void addConversionToHistory() {
        Conversion newConversion = new Conversion();
        newConversion.setTimestamp(System.currentTimeMillis() / 1000);
        newConversion.setPrimaryCurrency(dbHandler.getCurrency(primaryCurrency));
        newConversion.setPrimaryValue(primaryCurrencyValue);
        newConversion.setSecondaryCurrency(dbHandler.getCurrency(secondaryCurrency));
        newConversion.setSecondaryValue(secondaryCurrencyValue);
        dbHandler.addConversion(newConversion);
        loadConversionHistory();
    }

    /**
     * Gets the conversion history from the database.
     */
    private void loadConversionHistory() {
        conversionList.clear();
        conversionList.addAll(SortUtils.mergeSort(dbHandler.getAllConversions(), Conversion.comparatorTime()));
        exchangeHistoryAdapter.notifyDataSetChanged();
    }
}
