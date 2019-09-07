package com.martinwalls.nea.contracts;

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

import java.time.Period;

public class RepeatIntervalDialog extends DialogFragment {

    public static final String EXTRA_SELECTED = "selected";

    public static final int OPTION_WEEK = 1;
    public static final int OPTION_TWO_WEEK = 2;
    public static final int OPTION_MONTH = 3;
    public static final int OPTION_CUSTOM = 4;

    private RepeatIntervalDialogListener listener;

    private LinearLayout radioGroup;
    private LinearLayout customInterval;

    private boolean isOne = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_repeat_interval, null);

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

        Bundle args = getArguments();
        int selectedOption = -1;
        if (args != null) {
            selectedOption = args.getInt(EXTRA_SELECTED);
        }
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
                radioCustom.setChecked(true);
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
            Period interval;
            switch (spinnerPos) {
                case 0:
                    interval = Period.ofDays(value);
                    break;
                case 1:
                    interval = Period.ofWeeks(value);
                    break;
                case 2:
                    interval = Period.ofMonths(value);
                    break;
                case 3:
                    interval = Period.ofYears(value);
                    break;
                default:
                    interval = Period.ZERO; // to make sure it's not null
                    break;
            }
            listener.onCustomIntervalSelected(interval);
            getDialog().dismiss();
        });

        builder.setView(dialogView);

        return builder.create();
    }

    private void onRadioBtnClicked(int id) {
        if (id == OPTION_CUSTOM) {
            radioGroup.setVisibility(View.GONE);
            customInterval.setVisibility(View.VISIBLE);
        } else {
            listener.onRadioBtnClicked(id);
            getDialog().dismiss();
        }
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

    public interface RepeatIntervalDialogListener {
        void onRadioBtnClicked(int id);
        void onCustomIntervalSelected(Period interval);
    }
}
