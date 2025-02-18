package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;

/**
 * Dialog that allows the user to choose a repeat interval for a contract.
 *
 * @see EditContractActivity
 */
public class RepeatIntervalDialog extends DialogFragment {

    public static final String EXTRA_SELECTED_INTERVAL = "selectedInterval";

    public static final int OPTION_WEEK = 1;
    public static final int OPTION_TWO_WEEK = 2;
    public static final int OPTION_MONTH = 3;
    public static final int OPTION_CUSTOM = 4;

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

    @Deprecated
    public RepeatIntervalDialog() {}

    public RepeatIntervalDialog(RepeatIntervalDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_repeat_interval, null);

        Bundle args = getArguments();
        Interval selectedInterval = null;
        if (args != null) {
            selectedInterval = (Interval) args.getSerializable(EXTRA_SELECTED_INTERVAL);
        }

        radioGroup = dialogView.findViewById(R.id.radio_group);
        customInterval = dialogView.findViewById(R.id.custom_interval);

        initRadioBtns(dialogView);
        setRadioBtnSelected(selectedInterval);

        initTimePeriodSpinner(dialogView);
        initCustomValueInput(dialogView);
        initDoneBtn(dialogView);

        builder.setView(dialogView);
        return builder.create();
    }

    /**
     * Initialises the radio buttons with on click listeners.
     */
    private void initRadioBtns(View dialogView) {
        radioWeek = dialogView.findViewById(R.id.radio_week);
        radioWeek.setOnClickListener(v -> { onRadioBtnClicked(OPTION_WEEK); });

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
     * Selects the radio button corresponding to the stored interval. If null, no
     * radio button is selected.
     */
    private void setRadioBtnSelected(@Nullable Interval selectedInterval) {
        if (selectedInterval != null) {
            if (selectedInterval.hasValues(1, Interval.TimeUnit.WEEK)) {
                radioWeek.setChecked(true);
            } else if (selectedInterval.hasValues(2, Interval.TimeUnit.WEEK)) {
                radioTwoWeek.setChecked(true);
            } else if (selectedInterval.hasValues(1, Interval.TimeUnit.MONTH)) {
                radioMonth.setChecked(true);
            } else {
                setCustomRadioBtnSelected(selectedInterval);
            }
        }
    }

    /**
     * Shows a radio button for a custom interval that the user has entered,
     * and sets it to selected.
     */
    private void setCustomRadioBtnSelected(@NonNull Interval selectedCustomInterval) {
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
            switch (id) {
                case OPTION_WEEK:
                    listener.onRepeatIntervalSelected(new Interval(1, Interval.TimeUnit.WEEK));
                    break;
                case OPTION_TWO_WEEK:
                    listener.onRepeatIntervalSelected(new Interval(2, Interval.TimeUnit.WEEK));
                    break;
                case OPTION_MONTH:
                    listener.onRepeatIntervalSelected(new Interval(1, Interval.TimeUnit.MONTH));
            }
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

        // plus and minus buttons
        ImageButton btnMinus = dialogView.findViewById(R.id.btn_minus);
        btnMinus.setOnClickListener(v -> decrementCustomValue());

        ImageButton btnPlus = dialogView.findViewById(R.id.btn_plus);
        btnPlus.setOnClickListener(v -> incrementCustomValue());
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
            int value = Integer.parseInt(editTextCustomValue.getText().toString());

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
            listener.onRepeatIntervalSelected(interval);
            dismiss();
        });
    }

    /**
     * Sets the custom value input field to the specified value.
     */
    private void setCustomValueInputValue(int value) {
        editTextCustomValue.setText(String.valueOf(value));
    }

    /**
     * Increments the value in the custom value input field.
     */
    private void incrementCustomValue() {
        if (TextUtils.isEmpty(editTextCustomValue.getText())) {
            setCustomValueInputValue(1);
        } else {
            int currentValue = Integer.parseInt(editTextCustomValue.getText().toString());
            setCustomValueInputValue(currentValue + 1);
        }
    }

    /**
     * Decrements the value in the custom value input field, if it is greater
     * than 0.
     */
    private void decrementCustomValue() {
        if (!TextUtils.isEmpty(editTextCustomValue.getText())) {
            int currentValue = Integer.parseInt(editTextCustomValue.getText().toString());
            // don't allow the value to go below 1
            if (currentValue > 1) {
                setCustomValueInputValue(currentValue - 1);
            }
        }
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
        @Deprecated
        default void onRadioBtnClicked(int id) {};

        /**
         * This is called when the user enters a custom interval in the
         * {@link RepeatIntervalDialog}. This should be implemented to store
         * this repeat interval.
         */
        @Deprecated
        default void onCustomIntervalSelected(Interval interval) {};

        default void onRepeatIntervalSelected(Interval interval) {};
    }
}
