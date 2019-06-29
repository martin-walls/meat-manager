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
import com.martinwalls.nea.*;
import com.martinwalls.nea.ui_components.CustomRecyclerView;

import java.util.List;

public class ExchangeFragment extends Fragment {

    private TextView primaryCurrencyText;
    private TextView secondaryCurrencyText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.exchange_title);
        View fragmentView = inflater.inflate(R.layout.fragment_exchange, container, false);

        primaryCurrencyText = fragmentView.findViewById(R.id.currency_primary);
        secondaryCurrencyText = fragmentView.findViewById(R.id.currency_secondary);

        NumberPicker currencyPickerLeft = fragmentView.findViewById(R.id.currency_picker_left);

        final String[] currencies = SampleData.getSampleCurrencies();

        currencyPickerLeft.setMinValue(0);
        currencyPickerLeft.setMaxValue(currencies.length - 1);
        currencyPickerLeft.setDisplayedValues(currencies);
        currencyPickerLeft.setWrapSelectorWheel(false);
        currencyPickerLeft.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerLeft.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setPrimaryCurrency(currencies[newVal]);
            }
        });
        primaryCurrencyText.setText(currencies[currencyPickerLeft.getValue()]);

        NumberPicker currencyPickerRight = fragmentView.findViewById(R.id.currency_picker_right);

        currencyPickerRight.setMinValue(0);
        currencyPickerRight.setMaxValue(currencies.length - 1);
        currencyPickerRight.setDisplayedValues(currencies);
        currencyPickerRight.setValue(1);
        currencyPickerRight.setWrapSelectorWheel(false);
        currencyPickerRight.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        currencyPickerRight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setSecondaryCurrency(currencies[newVal]);
            }
        });
        setSecondaryCurrency(currencies[currencyPickerRight.getValue()]);

        List<Conversion> conversionList = SampleData.getSampleConversions();
        ExchangeHistoryAdapter exchangeHistoryAdapter = new ExchangeHistoryAdapter(conversionList);

        TextView emptyView = fragmentView.findViewById(R.id.no_exchange_history);
        CustomRecyclerView conversionHistoryView = fragmentView.findViewById(R.id.exchange_history);
        conversionHistoryView.setAdapter(exchangeHistoryAdapter);
        conversionHistoryView.setEmptyView(emptyView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        conversionHistoryView.setLayoutManager(layoutManager);

        return fragmentView;
    }

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
}
