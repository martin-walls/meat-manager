package com.martinwalls.nea;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddNewProductDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_add_new_product, null);
        AutoCompleteTextView inputMeatType dialogView.findViewById(R.id.edit_text_meat_type);
        //todo auto complete text

        builder.setView(dialogView);
        builder.setTitle(R.string.stock_product_new);

        builder.setPositiveButton(R.string.stock_product_new_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                //todo handle done action
            }
        });
        builder.setNegativeButton(R.string.stock_product_new_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
