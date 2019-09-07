package com.martinwalls.nea.contracts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martinwalls.nea.R;

public class RepeatIntervalDialog extends DialogFragment {

    public static final String EXTRA_SELECTED = "selected";

    public static final int OPTION_WEEK = 1;
    public static final int OPTION_TWO_WEEK = 2;
    public static final int OPTION_MONTH = 3;
    public static final int OPTION_CUSTOM = 4;

    private RadioBtnListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_repeat_interval, null);

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

        builder.setView(dialogView);

        return builder.create();
    }

    private void onRadioBtnClicked(int id) {
        listener.onRadioBtnClicked(id);
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RadioBtnListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement " + RadioBtnListener.class.getSimpleName());
        }
    }

    public interface RadioBtnListener {
        void onRadioBtnClicked(int id);
    }
}
