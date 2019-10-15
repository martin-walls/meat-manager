package com.martinwalls.nea;

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

public class ConfirmDeleteDialog extends DialogFragment {

    public static final String EXTRA_NAME = "name";

    private ConfirmDeleteListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete, null);

        Bundle args = getArguments();
        String name = "";
        if (args != null) {
            name = args.getString(EXTRA_NAME);
        }

        TextView message = dialogView.findViewById(R.id.message);
        if (TextUtils.isEmpty(name)) {
            message.setText(R.string.dialog_confirm_delete_msg_default);
        } else {
            message.setText(getString(R.string.dialog_confirm_delete_msg, name));
        }

        Button btnDelete = dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnDelete.setOnClickListener(v -> {
            listener.onConfirmDelete();
            dismiss();
        });
        btnCancel.setOnClickListener(v -> dismiss());

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

    public interface ConfirmDeleteListener {
        void onConfirmDelete();
    }
}
