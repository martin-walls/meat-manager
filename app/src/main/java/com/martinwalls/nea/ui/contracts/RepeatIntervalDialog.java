package com.martinwalls.nea.ui.contracts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.models.Interval;
import com.martinwalls.nea.util.SimpleTextWatcher;

/**
 * Dialog that allows the user to choose a repeat interval for a contract.
 *
 * @see EditContractActivity
 */
public class RepeatIntervalDialog extends DialogFragment {

    static final String EXTRA_SELECTED = "selected";
    static final String EXTRA_CUSTOM_INTERVAL = "customInterval";

    static final int OPTION_WEEK = 1;
    static final int OPTION_TWO_WEEK = 2;
    static final int OPTION_MONTH = 3;
    static final int OPTION_CUSTOM = 4;

    private RepeatIntervalDialogListener listener;

    private LinearLayout radioGroup;
    private LinearLayout customInterval;

    private RadioButton radioWeek;
    private RadioButton radioTwoWeek;
    private RadioButton radioMonth;
    private RadioButton radioCustom;
    private RadioButton radioCustomSelected;

    private Spinner timePeriodSpn;
    private ArrayAdapter<CharSequence> timePeriodAdapter;
    private EditText editTextCustomValue;

    /**
     * Flag to store whether the user entered value is "1" or not, for use
     * when displaying strings that need to display the correct pluralised form.
     */
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
                selectedCustomInterval =
                        (Interval) args.getSerializable(EXTRA_CUSTOM_INTERVAL);
            }
        }

        radioGroup = dialogView.findViewById(R.id.radio_group);
        customInterval = dialogView.findViewById(R.id.custom_interval);

        initRadioBtns(dialogView);
        setRadioBtnSelected(selectedOption, selectedCustomInterval);

        initTimePeriodSpinner(dialogView);
        initCustomValueInput(dialogView);
        initDoneBtn(dialogView);

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

    /**
     * Initialises the radio buttons with on click listeners.
     */
    private void initRadioBtns(View dialogView) {
        radioWeek = dialogView.findViewById(R.id.radio_week);
        radioWeek.setOnClickListener(v -> onRadioBtnClicked(OPTION_WEEK));

        radioTwoWeek = dialogView.findViewById(R.id.radio_two_week);
        radioTwoWeek.setOnClickListener(v -> onRadioBtnClicked(OPTION_TWO_WEEK));

        radioMonth = dialogView.findViewById(R.id.radio_month);
        radioMonth.setOnClickListener(v -> onRadioBtnClicked(OPTION_MONTH));

        radioCustom = dialogView.findViewById(R.id.radio_custom);
        radioCustom.setOnClickListener(v -> onRadioBtnClicked(OPTION_CUSTOM));

        // this option is always the currently selected custom interval if
        // visible, so when clicked, the interval is the same
        radioCustomSelected = dialogView.findViewById(R.id.radio_custom_selected);
        radioCustomSelected.setOnClickListener(v -> dismiss());
    }

    /**
     * Selects the radio button corresponding to the stored interval.
     */
    private void setRadioBtnSelected(int selectedOption, Interval selectedCustomInterval) {
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
                        radioCustomSelected.setText(
                                getString(R.string.contracts_repeat_interval_display_one,
                                        selectedCustomInterval.getUnit()
                                                .name().toLowerCase()));
                    } else {
                        radioCustomSelected.setText(
                                getString(R.string.contracts_repeat_interval_display_multiple,
                                        selectedCustomInterval.getValue(),
                                        selectedCustomInterval.getUnit()
                                                .name().toLowerCase()));
                    }
                }
                break;
        }
    }

    /**
     * Called when the user clicks one of the radio buttons with id {@code id}.
     */
    private void onRadioBtnClicked(int id) {
        if (id == OPTION_CUSTOM) {
            radioGroup.setVisibility(View.GONE);
            customInterval.setVisibility(View.VISIBLE);
        } else {
            listener.onRadioBtnClicked(id);
            dismiss();
        }
    }

    /**
     * Initialises the spinner that shows the time periods the user can
     * choose from, corresponding to the values of {@link Interval.TimeUnit}.
     */
    private void initTimePeriodSpinner(View dialogView) {
        timePeriodSpn = dialogView.findViewById(R.id.spn_time_period);
        timePeriodAdapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        timePeriodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timePeriodAdapter.addAll(getResources().getStringArray(R.array.time_periods));
        timePeriodAdapter.notifyDataSetChanged();
        timePeriodSpn.setAdapter(timePeriodAdapter);
    }

    /**
     * Initialises the input field for the user to choose a custom length of
     * time for the repeat interval.
     */
    private void initCustomValueInput(View dialogView) {
        editTextCustomValue = dialogView.findViewById(R.id.edit_text_number);
        editTextCustomValue.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isOne && s.toString().equals("1")) { // show not plurals
                    timePeriodAdapter.clear();
                    timePeriodAdapter.addAll(
                            getResources().getStringArray(R.array.time_periods));
                    timePeriodAdapter.notifyDataSetChanged();
                    isOne = true;
                } else if (isOne && !s.toString().equals("1")) { // show plurals
                    timePeriodAdapter.clear();
                    timePeriodAdapter.addAll(
                            getResources().getStringArray(R.array.time_periods_plural));
                    timePeriodAdapter.notifyDataSetChanged();
                    isOne = false;
                }
            }
        });
    }

    /**
     * Initialises the done button that shows when a custom interval is
     * being entered.
     */
    private void initDoneBtn(View dialogView) {
        Button doneBtn = dialogView.findViewById(R.id.btn_done);
        doneBtn.setOnClickListener(v -> {
            int spinnerPos = timePeriodSpn.getSelectedItemPosition();
            if (TextUtils.isEmpty(editTextCustomValue.getText())) {
                return;
            }
            int value = Integer.valueOf(editTextCustomValue.getText().toString());

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
    }

    /**
     * Interface for when the user selects a repeat interval from the dialog.
     * This must be implemented by the calling Activity/Fragment.
     */
    public interface RepeatIntervalDialogListener {
        /**
         * This is called when the radio button with specified {@code id} is
         * clicked in the {@link RepeatIntervalDialog}. This should be
         * implemented to store the corresponding repeat interval.
         */
        void onRadioBtnClicked(int id);

        /**
         * This is called when the user enters a custom interval in the
         * {@link RepeatIntervalDialog}. This should be implemented to store
         * this repeat interval.
         */
        void onCustomIntervalSelected(Interval interval);
    }
}
