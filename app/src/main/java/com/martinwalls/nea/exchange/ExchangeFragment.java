package com.martinwalls.nea.exchange;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.MainActivity;
import com.martinwalls.nea.R;
import com.martinwalls.nea.SampleData;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.models.Conversion;

import java.util.HashMap;
import java.util.List;

public class ExchangeFragment extends Fragment {

    private TextView primaryCurrencyText;
    private TextView secondaryCurrencyText;
    private TextView primaryCurrencyValue;
    private TextView secondaryCurrencyValue;

    private NumberPicker currencyPickerLeft;
    private NumberPicker currencyPickerRight;

    private String primaryCurrency;
    private String secondaryCurrency;

    private HashMap<String, Double> rates = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.exchange_title);
        View fragmentView = inflater.inflate(R.layout.fragment_exchange, container, false);

        primaryCurrencyText = fragmentView.findViewById(R.id.currency_primary);
        secondaryCurrencyText = fragmentView.findViewById(R.id.currency_secondary);
        primaryCurrencyValue = fragmentView.findViewById(R.id.currency_primary_value);
        secondaryCurrencyValue = fragmentView.findViewById(R.id.currency_secondary_value);

        final String[] currencies = SampleData.getSampleCurrencies();

        currencyPickerLeft = fragmentView.findViewById(R.id.currency_picker_left);

        currencyPickerLeft.setMinValue(0);
        currencyPickerLeft.setMaxValue(currencies.length - 1);
        currencyPickerLeft.setDisplayedValues(currencies);
        currencyPickerLeft.setWrapSelectorWheel(false);
        currencyPickerLeft.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerLeft.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setPrimaryCurrency(currencies[newVal]));
        setPrimaryCurrency(currencies[currencyPickerLeft.getValue()]);

        currencyPickerRight = fragmentView.findViewById(R.id.currency_picker_right);

        currencyPickerRight.setMinValue(0);
        currencyPickerRight.setMaxValue(currencies.length - 1);
        currencyPickerRight.setDisplayedValues(currencies);
        currencyPickerRight.setValue(1);
        currencyPickerRight.setWrapSelectorWheel(false);
        currencyPickerRight.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerRight.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setSecondaryCurrency(currencies[newVal]));
        setSecondaryCurrency(currencies[currencyPickerRight.getValue()]);

        ImageButton swapBtn = fragmentView.findViewById(R.id.swap_currencies);
        swapBtn.setOnClickListener(v -> swapCurrencies());

        List<Conversion> conversionList = SampleData.getSampleConversions();
        ExchangeHistoryAdapter exchangeHistoryAdapter = new ExchangeHistoryAdapter(conversionList);

        TextView emptyView = fragmentView.findViewById(R.id.no_exchange_history);
        CustomRecyclerView conversionHistoryView = fragmentView.findViewById(R.id.exchange_history);
        conversionHistoryView.setAdapter(exchangeHistoryAdapter);
        conversionHistoryView.setEmptyView(emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        conversionHistoryView.setLayoutManager(layoutManager);

        fetchRatesFromApi();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchRatesFromApi();
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
        secondaryCurrencyValue.setText(getString(R.string.exchange_rate, getRate(secondaryCurrency, primaryCurrency)));
    }

    private void swapCurrencies() {
        String primaryTemp = primaryCurrency;
        setPrimaryCurrency(secondaryCurrency);
        setSecondaryCurrency(primaryTemp);

        int leftValue = currencyPickerLeft.getValue();
        currencyPickerLeft.setValue(currencyPickerRight.getValue());
        currencyPickerRight.setValue(leftValue);
        //todo set number picker value
    }

    private double getRate(String currency, String base) {
        if (rates != null && rates.keySet().contains(currency) && rates.keySet().contains(base)) {
            return rates.get(currency) / rates.get(base);
        }
        return 0;
    }
}
