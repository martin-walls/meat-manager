package com.martinwalls.meatmanager.ui.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martinwalls.meatmanager.R;

public class ConfirmDeleteDialog extends DialogFragment {

    public static final String EXTRA_NAME = "name";

    private ConfirmDeleteListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete, null);

        setDialogMessage(dialogView);

        // set button listeners
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnDelete.setOnClickListener(v -> {
            listener.onConfirmDelete();
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            listener.onConfirmDeleteCancel();
            dismiss();
        });

        builder.setView(dialogView);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmDeleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement " + ConfirmDeleteListener.class.getSimpleName());
        }
    }

    /**
     * Checks if a name has been passed as an argument to the dialog, sets the
     * dialog's message based on this. Shows a default message if no name has
     * been passed.
     */
    private void setDialogMessage(View dialogView) {
        // check if name has been passed to the dialog
        Bundle args = getArguments();
        String name = "";
        if (args != null) {
            name = args.getString(EXTRA_NAME);
        }

        TextView message = dialogView.findViewById(R.id.message);
        // show default delete message if no name given
        if (TextUtils.isEmpty(name)) {
            message.setText(R.string.dialog_confirm_delete_msg_default);
        } else {
            message.setText(getString(R.string.dialog_confirm_delete_msg, name));
        }
    }

    /**
     * Interface to handle clicks on dialog buttons.
     */
    public interface ConfirmDeleteListener {
        /**
         * This is called when the user confirms the delete action from the
         * dialog. This should be implemented to perform the delete action.
         */
        void onConfirmDelete();

        /**
         * This is called when the user cancels the delete action from the
         * dialog. Typically this should be resume what the user was previously
         * doing.
         *
         * <p>If this should just close the dialog without any additional
         * actions, this doesn't need to be implemented.
         */
        default void onConfirmDeleteCancel() {}
    }
}
