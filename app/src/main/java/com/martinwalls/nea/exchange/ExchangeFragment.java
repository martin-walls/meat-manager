package com.martinwalls.nea.exchange;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.*;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.db.ExchangeDbHandler;
import com.martinwalls.nea.models.Conversion;
import com.martinwalls.nea.models.Currency;

import java.util.HashMap;
import java.util.List;

public class ExchangeFragment extends Fragment {

    private ExchangeDbHandler dbHandler;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.exchange_title);
        View fragmentView = inflater.inflate(R.layout.fragment_exchange, container, false);

        dbHandler = new ExchangeDbHandler(getContext());

        primaryCurrencyText = fragmentView.findViewById(R.id.currency_primary);
        secondaryCurrencyText = fragmentView.findViewById(R.id.currency_secondary);
        secondaryCurrencyValueText = fragmentView.findViewById(R.id.currency_secondary_value);

        currencyPickerLeft = fragmentView.findViewById(R.id.currency_picker_left);
        currencyPickerRight = fragmentView.findViewById(R.id.currency_picker_right);

        initCurrencyPickers();
        currencyPickerRight.setValue(1);

        //todo store last currencies in shared prefs, select those when screen opened again

        ImageButton swapBtn = fragmentView.findViewById(R.id.swap_currencies);
        swapBtn.setOnClickListener(v -> swapCurrencies());

        List<Conversion> conversionList = Utils.mergeSort(dbHandler.getAllConversions(),
                (conv1, conv2) -> (int) (conv2.getTimestamp() - conv1.getTimestamp()));
        ExchangeHistoryAdapter exchangeHistoryAdapter = new ExchangeHistoryAdapter(conversionList);

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

        fetchRatesFromApi();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initCurrencyPickers();
        fetchRatesFromApi();
    }

    private void initCurrencyPickers() {
        String[] currencies = fetchCurrenciesToShow();
        currencyPickerLeft.setMinValue(0);
        currencyPickerLeft.setMaxValue(currencies.length - 1);
        currencyPickerLeft.setDisplayedValues(currencies);
        currencyPickerLeft.setWrapSelectorWheel(false);
        currencyPickerLeft.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerLeft.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setPrimaryCurrency(currencies[newVal]));
        setPrimaryCurrency(currencies[currencyPickerLeft.getValue()]);

        currencyPickerRight.setMinValue(0);
        currencyPickerRight.setMaxValue(currencies.length - 1);
        currencyPickerRight.setDisplayedValues(currencies);
        currencyPickerRight.setWrapSelectorWheel(false);
        currencyPickerRight.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerRight.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setSecondaryCurrency(currencies[newVal]));
        setSecondaryCurrency(currencies[currencyPickerRight.getValue()]);
    }

    private String[] fetchCurrenciesToShow() {
        List<Currency> currencies = Utils.mergeSort(dbHandler.getFavCurrencies(),
                (currency1, currency2) -> currency1.getCode().compareTo(currency2.getCode()));

        String[] currencyCodes = new String[currencies.size()];
        for (int i = 0; i < currencies.size(); i++) {
            currencyCodes[i] = currencies.get(i).getCode();
        }
        return currencyCodes;
    }

    private void fetchRatesFromApi() {
        PendingIntent pendingResult = getActivity().createPendingResult(
                MainActivity.REQUEST_EXCHANGE_API_SERVICE, new Intent(), 0);
        Intent intent = new Intent(getContext().getApplicationContext(), ApiIntentService.class);
        intent.putExtra(ApiIntentService.EXTRA_PENDING_RESULT, pendingResult);
        getActivity().startService(intent);
    }

    public void onRatesFetched(Intent data) {
        //noinspection unchecked
        rates = (HashMap<String, Double>) data.getSerializableExtra(ApiIntentService.EXTRA_RESULT);
        updateRates();

//        long timestamp = Long.parseLong(CacheHelper.retrieve(getContext(), "last_cache_timestamp"));
//        Toast.makeText(getContext(), "fetched", Toast.LENGTH_SHORT).show();
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
                startActivity(favouritesIntent);
                return true;
            case R.id.action_refresh:
                fetchRatesFromApi();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPrimaryCurrency(String currency) {
        primaryCurrency = currency;
        primaryCurrencyText.setText(currency);
        updateRates();
    }

    private void setSecondaryCurrency(String currency) {
        secondaryCurrency = currency;
        secondaryCurrencyText.setText(currency);
        updateRates();
    }

    private void updateRates() {
        secondaryCurrencyValue = getRate(secondaryCurrency, primaryCurrency);
        secondaryCurrencyValueText.setText(getString(R.string.exchange_rate, secondaryCurrencyValue));
    }

    private void swapCurrencies() {
        String primaryTemp = primaryCurrency;
        setPrimaryCurrency(secondaryCurrency);
        setSecondaryCurrency(primaryTemp);

        int leftValue = currencyPickerLeft.getValue();
        currencyPickerLeft.setValue(currencyPickerRight.getValue());
        currencyPickerRight.setValue(leftValue);
    }

    private double getRate(String currency, String base) {
        if (rates != null && rates.keySet().contains(currency) && rates.keySet().contains(base)) {
            return (rates.get(currency) / rates.get(base)) * primaryCurrencyValue;
        }
        return 0;
    }

    private void addConversionToHistory() {
        Conversion newConversion = new Conversion();
        newConversion.setTimestamp(System.currentTimeMillis() / 1000);
        newConversion.setPrimaryCurrency(dbHandler.getCurrency(primaryCurrency));
        newConversion.setPrimaryValue(primaryCurrencyValue);
        newConversion.setSecondaryCurrency(dbHandler.getCurrency(secondaryCurrency));
        newConversion.setSecondaryValue(secondaryCurrencyValue);
        dbHandler.addConversion(newConversion);
    }
}
