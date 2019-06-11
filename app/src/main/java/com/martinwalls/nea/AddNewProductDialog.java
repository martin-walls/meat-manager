package com.martinwalls.nea;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class AddNewProductDialog extends DialogFragment {
    private AddNewProductListener listener;
    private TextInputLayout inputLayoutProductName;
    private TextInputEditText editTextProductName;
    private TextInputLayout inputLayoutMeatType;
    private AutoCompleteTextView editTextMeatType;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_add_new_product, null);
        editTextProductName = dialogView.findViewById(R.id.edit_text_product_name);
        inputLayoutProductName = dialogView.findViewById(R.id.input_layout_product_name);
        editTextMeatType = dialogView.findViewById(R.id.edit_text_meat_type);
        inputLayoutMeatType = dialogView.findViewById(R.id.input_layout_meat_type);

        List<String> meatTypes = SampleData.getSampleMeatTypes();
        ArrayAdapter<String> autocompleteAdapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, meatTypes);
        editTextMeatType.setAdapter(autocompleteAdapter);
        editTextMeatType.setThreshold(0);

        editTextMeatType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextMeatType.showDropDown();
                }
            }
        });
        //todo add button

        builder.setView(dialogView);
        builder.setTitle(R.string.stock_product_new);

        Button buttonDone = dialogView.findViewById(R.id.btn_done);
        Button buttonCancel = dialogView.findViewById(R.id.btn_cancel);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                String newProductName = editTextProductName.getText().toString();
                String newMeatType = editTextMeatType.getText().toString();
                inputLayoutProductName.setError(null);
                inputLayoutMeatType.setError(null);
                if (newProductName.isEmpty()) {
                    inputLayoutProductName.setError(getString(R.string.input_error_blank));
                    isValid = false;
                }
                if (newMeatType.isEmpty()) {
                    inputLayoutMeatType.setError(getString(R.string.input_error_blank));
                    isValid = false;
                } else if (!isMeatTypeValid(newMeatType)) {
                    inputLayoutMeatType.setError(getString(R.string.input_error_invalid));
                    isValid = false;
                }
                if (isValid) {
                    Product newProduct = new Product();
                    newProduct.setProductName(newProductName);
                    newProduct.setMeatType(newMeatType);
                    listener.onAddNewProductPositiveClick(newProduct);
                    getDialog().dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddNewProductListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() +
                    " must implement " + AddNewProductListener.class.getSimpleName());
        }
    }

    public interface AddNewProductListener {
        void onAddNewProductPositiveClick(Product newProduct);
    }

    private boolean isMeatTypeValid(String meatType) {
        List<String> meatTypes = SampleData.getSampleMeatTypes();
        return meatTypes.contains(meatType);
    }

    private void showProductNameError(String message) {
        editTextProductName.setError(message);
    }
}
