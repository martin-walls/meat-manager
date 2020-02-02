package com.martinwalls.meatmanager.ui.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martinwalls.meatmanager.R;

public class ConfirmCancelDialog extends DialogFragment {

    private ConfirmCancelListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_cancel, null);

        // set button listeners
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(v -> onYesBtnClick());
        btnNo.setOnClickListener(v -> onNoBtnClick());

        builder.setView(dialogView);

        return builder.create();
    }

    public void setListener(ConfirmCancelListener listener) {
        this.listener = listener;
    }

    private void onYesBtnClick() {
        if (listener != null) listener.onConfirmCancelYesAction();
        dismiss();
    }

    private void onNoBtnClick() {
        if (listener != null) listener.onConfirmCancelNoAction();
        dismiss();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            listener = (ConfirmCancelListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement " + ConfirmCancelListener.class.getSimpleName());
//        }
//    }

    /**
     * Interface to handle clicks on dialog buttons.
     */
    public interface ConfirmCancelListener {
        /**
         * This is called when the user confirms the cancel action from the
         * dialog. Typically this should be implemented to close the calling
         * activity / fragment to go back to the previous page.
         */
        void onConfirmCancelYesAction();
        // doesn't need to be implemented if NO btn should just cancel dialog

        /**
         * This is called when the user stops the cancel action from the dialog.
         * Typically this should be implemented to resume what the user was
         * doing before. {@link ConfirmCancelDialog} handles closing the
         * dialog so this doesn't need to be implemented.
         *
         * <p>If this should just close the dialog without any additional
         * actions, this doesn't need to be implemented.
         */
        default void onConfirmCancelNoAction() {}
    }
}
