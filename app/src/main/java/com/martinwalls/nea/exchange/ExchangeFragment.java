package com.martinwalls.nea.exchange;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private HashMap<String, Double> rates;

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

        NumberPicker currencyPickerLeft = fragmentView.findViewById(R.id.currency_picker_left);

        final String[] currencies = SampleData.getSampleCurrencies();

        currencyPickerLeft.setMinValue(0);
        currencyPickerLeft.setMaxValue(currencies.length - 1);
        currencyPickerLeft.setDisplayedValues(currencies);
        currencyPickerLeft.setWrapSelectorWheel(false);
        currencyPickerLeft.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerLeft.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setPrimaryCurrency(currencies[newVal]));
        primaryCurrencyText.setText(currencies[currencyPickerLeft.getValue()]);

        NumberPicker currencyPickerRight = fragmentView.findViewById(R.id.currency_picker_right);

        currencyPickerRight.setMinValue(0);
        currencyPickerRight.setMaxValue(currencies.length - 1);
        currencyPickerRight.setDisplayedValues(currencies);
        currencyPickerRight.setValue(1);
        currencyPickerRight.setWrapSelectorWheel(false);
        currencyPickerRight.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerRight.setOnValueChangedListener(
                (picker, oldVal, newVal) -> setSecondaryCurrency(currencies[newVal]));
        setSecondaryCurrency(currencies[currencyPickerRight.getValue()]);

        List<Conversion> conversionList = SampleData.getSampleConversions();
        ExchangeHistoryAdapter exchangeHistoryAdapter = new ExchangeHistoryAdapter(conversionList);

        TextView emptyView = fragmentView.findViewById(R.id.no_exchange_history);
        CustomRecyclerView conversionHistoryView = fragmentView.findViewById(R.id.exchange_history);
        conversionHistoryView.setAdapter(exchangeHistoryAdapter);
        conversionHistoryView.setEmptyView(emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        conversionHistoryView.setLayoutManager(layoutManager);


        ((ExchangeApiInterface) getActivity()).startExchangeApiService();

        return fragmentView;
    }

    public void onRatesFetched(Intent data) {
        rates = (HashMap<String, Double>) data.getSerializableExtra(ApiIntentService.EXTRA_RESULT);
        secondaryCurrencyValue.setText(getString(R.string.exchange_rate, rates.get("USD")));
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            rates = (HashMap<String, Double>) data.getSerializableExtra(ApiIntentService.EXTRA_RESULT);
//
////            for (Map.Entry<String, Double> entry : rates.entrySet()) {
////                Log.e("RATES", entry.getKey() + ": " + entry.getValue());
////            }
//        }
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_exchange, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favourites) {
            Intent favouritesIntent = new Intent(getContext(), ChooseCurrenciesActivity.class);
            startActivity(favouritesIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPrimaryCurrency(String currency) {
        primaryCurrencyText.setText(currency);
        //todo update rate values
    }

    private void setSecondaryCurrency(String currency) {
        secondaryCurrencyText.setText(currency);
        //todo update rate values
    }

    public interface ExchangeApiInterface {
        void startExchangeApiService();
    }
}
