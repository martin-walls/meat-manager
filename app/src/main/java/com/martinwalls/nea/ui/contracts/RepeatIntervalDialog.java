package com.martinwalls.nea.ui.contracts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Interval;

public class RepeatIntervalDialog extends DialogFragment {

    static final String EXTRA_SELECTED = "selected";
    static final String EXTRA_TIME_VALUE = "timeValue";
    static final String EXTRA_TIME_UNIT = "timeUnit";

    static final int OPTION_WEEK = 1;
    static final int OPTION_TWO_WEEK = 2;
    static final int OPTION_MONTH = 3;
    static final int OPTION_CUSTOM = 4;

    private RepeatIntervalDialogListener listener;

    private LinearLayout radioGroup;
    private LinearLayout customInterval;

    private boolean isOne = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_repeat_interval, null);

        Bundle args = getArguments();
        int selectedOption = -1;
        Interval selectedCustomInterval = new Interval();
        if (args != null) {
            selectedOption = args.getInt(EXTRA_SELECTED);
            if (selectedOption == OPTION_CUSTOM) {
                selectedCustomInterval.setValue(args.getInt(EXTRA_TIME_VALUE));
                selectedCustomInterval.setUnit(Interval.TimeUnit.parseTimeUnit(args.getString(EXTRA_TIME_UNIT)));
            }
        }

        radioGroup = dialogView.findViewById(R.id.radio_group);
        customInterval = dialogView.findViewById(R.id.custom_interval);

        RadioButton radioWeek = dialogView.findViewById(R.id.radio_week);
        radioWeek.setOnClickListener(v -> onRadioBtnClicked(OPTION_WEEK));
        RadioButton radioTwoWeek = dialogView.findViewById(R.id.radio_two_week);
        radioTwoWeek.setOnClickListener(v -> onRadioBtnClicked(OPTION_TWO_WEEK));
        RadioButton radioMonth = dialogView.findViewById(R.id.radio_month);
        radioMonth.setOnClickListener(v -> onRadioBtnClicked(OPTION_MONTH));
        RadioButton radioCustom = dialogView.findViewById(R.id.radio_custom);
        radioCustom.setOnClickListener(v -> onRadioBtnClicked(OPTION_CUSTOM));

        RadioButton radioCustomSelected = dialogView.findViewById(R.id.radio_custom_selected);
        radioCustomSelected.setOnClickListener(v -> dismiss());

        switch (selectedOption) {
            case OPTION_WEEK:
                radioWeek.setChecked(true);
                break;
            case OPTION_TWO_WEEK:
                radioTwoWeek.setChecked(true);
                break;
            case OPTION_MONTH:
                radioMonth.setChecked(true);
                break;
            case OPTION_CUSTOM:
                if (selectedCustomInterval.getUnit() != null) {
                    radioCustomSelected.setVisibility(View.VISIBLE);
                    radioCustomSelected.setChecked(true);
                    if (selectedCustomInterval.getValue() == 1) {
                        radioCustomSelected.setText(getString(R.string.contracts_repeat_interval_display_one,
                                selectedCustomInterval.getUnit().name().toLowerCase()));
                    } else {
                        radioCustomSelected.setText(getString(R.string.contracts_repeat_interval_display_multiple,
                                selectedCustomInterval.getValue(),
                                selectedCustomInterval.getUnit().name().toLowerCase()));
                    }
                }
                break;
        }

        Spinner timePeriodSpn = dialogView.findViewById(R.id.spn_time_period);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
//                ArrayAdapter.createFromResource(getContext(),
//                R.array.time_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(getResources().getStringArray(R.array.time_periods));
        adapter.notifyDataSetChanged();
        timePeriodSpn.setAdapter(adapter);

        EditText editTextNumber = dialogView.findViewById(R.id.edit_text_number);
        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isOne && s.toString().equals("1")) {
                    adapter.clear();
                    adapter.addAll(getResources().getStringArray(R.array.time_periods));
                    adapter.notifyDataSetChanged();
                    isOne = true;
                } else if (isOne && !s.toString().equals("1")) {
                    adapter.clear();
                    adapter.addAll(getResources().getStringArray(R.array.time_periods_plural));
                    adapter.notifyDataSetChanged();
                    isOne = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button doneBtn = dialogView.findViewById(R.id.btn_done);
        doneBtn.setOnClickListener(v -> {
            int spinnerPos = timePeriodSpn.getSelectedItemPosition();
            if (TextUtils.isEmpty(editTextNumber.getText())) {
                return;
            }
            int value = Integer.valueOf(editTextNumber.getText().toString());

            Interval interval = new Interval();
            interval.setValue(value);
            switch (spinnerPos) {
                case 0:
                    interval.setUnit(Interval.TimeUnit.WEEK);
                    break;
                case 1:
                    interval.setUnit(Interval.TimeUnit.MONTH);
                    break;
            }
            listener.onCustomIntervalSelected(interval);
            dismiss();
        });

        builder.setView(dialogView);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RepeatIntervalDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement " + RepeatIntervalDialogListener.class.getSimpleName());
        }
    }

    private void onRadioBtnClicked(int id) {
        if (id == OPTION_CUSTOM) {
            radioGroup.setVisibility(View.GONE);
            customInterval.setVisibility(View.VISIBLE);
        } else {
            listener.onRadioBtnClicked(id);
            dismiss();
        }
    }

    public interface RepeatIntervalDialogListener {
        void onRadioBtnClicked(int id);
        void onCustomIntervalSelected(Interval interval);
    }
}
