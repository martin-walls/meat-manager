package com.martinwalls.nea;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ConfirmCancelDialog extends DialogFragment {

    private ConfirmCancelListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_confirm_cancel, null);

        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(v -> {
            listener.onConfirmCancelYesAction();
            getDialog().dismiss();
        });
        btnNo.setOnClickListener(v -> {
            listener.onConfirmCancelNoAction();
            getDialog().dismiss();
        });

        builder.setView(dialogView);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmCancelListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement " + ConfirmCancelListener.class.getSimpleName());
        }
    }

    public interface ConfirmCancelListener {
        void onConfirmCancelYesAction();
        // doesn't need to be implemented if NO btn should just cancel dialog
        default void onConfirmCancelNoAction() {}
    }
}
