package com.martinwalls.nea.ui.meat_types;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;

import java.util.List;

public class AddNewMeatTypeDialog extends DialogFragment {

    private AddNewMeatTypeListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_new_meat_type, null);
        TextInputLayout inputLayoutMeatType =
                dialogView.findViewById(R.id.input_layout_meat_type);
        TextInputEditText editTextMeatType = dialogView.findViewById(R.id.edit_text_meat_type);

        builder.setView(dialogView);
        builder.setTitle(R.string.stock_product_meat_type_new);

        Button buttonDone = dialogView.findViewById(R.id.btn_done);
        Button buttonCancel = dialogView.findViewById(R.id.btn_cancel);

        buttonDone.setOnClickListener(v -> {
            String newMeatType = editTextMeatType.getText().toString();
            inputLayoutMeatType.setError(null);
            if (newMeatType.isEmpty()) {
                inputLayoutMeatType.setError(getString(R.string.input_error_blank));
            } else if (isNewMeatTypeAlreadyAdded(newMeatType)) {
                inputLayoutMeatType.setError(getString(R.string.input_error_duplicate));
            } else {
                listener.onAddNewMeatTypeDoneAction(newMeatType);
                dismiss();
            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddNewMeatTypeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() +
                    " must implement " + AddNewMeatTypeListener.class.getSimpleName());
        }
    }

    /**
     * Checks whether {@code meatType} already exists as a meat type in the
     * database.
     */
    private boolean isNewMeatTypeAlreadyAdded(String meatType) {
        DBHandler dbHandler = new DBHandler(getContext());
        List<String> meatTypes = dbHandler.getAllMeatTypes();
        return meatTypes.contains(meatType);
    }

    /**
     * Interface to handle done action of the dialog.
     */
    public interface AddNewMeatTypeListener {
        /**
         * This is called when the user confirms adding a meat type from the
         * dialog. {@link AddNewMeatTypeDialog} handles error checking so
         * this should be implemented to store the meat type, the implementation
         * doesn't need to validate it again.
         */
        void onAddNewMeatTypeDoneAction(String meatType);
    }
}
