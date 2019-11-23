package com.martinwalls.nea.ui.products;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Product;
import com.martinwalls.nea.util.SortUtils;

import java.util.List;
import java.util.stream.Collectors;

public class AddNewProductDialog extends DialogFragment {

    private DBHandler dbHandler;

    private AddNewProductListener listener;

    private TextInputLayout inputLayoutProductName;
    private TextInputEditText editTextProductName;
    private TextInputLayout inputLayoutMeatType;
    private AutoCompleteTextView editTextMeatType;

    private View dialogView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_new_product, null);
        bindViews(dialogView);

        dbHandler = new DBHandler(getContext());

        initMeatTypesInput();

        Button btnDone = dialogView.findViewById(R.id.btn_done);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        btnDone.setOnClickListener(v -> onDoneAction());
        btnCancel.setOnClickListener(v -> dismiss());

        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddNewProductListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() +
                    " must implement " + AddNewProductListener.class.getSimpleName());
        }
    }

    /**
     * Binds views in the layout to their variables, so they can be referenced
     * later.
     */
    private void bindViews(View dialogView) {
        editTextProductName = dialogView.findViewById(R.id.edit_text_product_name);
        inputLayoutProductName = dialogView.findViewById(R.id.input_layout_product_name);
        editTextMeatType = dialogView.findViewById(R.id.edit_text_meat_type);
        inputLayoutMeatType = dialogView.findViewById(R.id.input_layout_meat_type);
    }

    /**
     * Initialises the meat type input field with an autocomplete dropdown.
     */
    private void initMeatTypesInput() {
        List<String> meatTypesList = SortUtils.mergeSort(dbHandler.getAllMeatTypes());
        ArrayAdapter<String> autocompleteAdapter =
                new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, meatTypesList);
        editTextMeatType.setAdapter(autocompleteAdapter);
        editTextMeatType.setThreshold(0);

        editTextMeatType.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editTextMeatType.showDropDown();
            }
        });
    }

    /**
     * Checks whether the given String is a valid meat type, that is
     * one that is is the database.
     */
    private boolean isMeatTypeValid(String meatType) {
        List<String> meatTypes = dbHandler.getAllMeatTypes();
        return meatTypes.contains(meatType);
    }

    /**
     * This is called when the user clicks the done button. Validates user input
     * and, if valid, calls {@link AddNewProductListener#onAddNewProductDoneAction}
     * so the calling Activity can handle adding the product to the database.
     */
    private void onDoneAction() {
        boolean isValid = true;
        String newProductName = editTextProductName.getText().toString();
        String newMeatType = editTextMeatType.getText().toString();
        inputLayoutProductName.setError(null);
        inputLayoutMeatType.setError(null);
        // is name valid (not blank, unique)
        if (newProductName.isEmpty()) {
            inputLayoutProductName.setError(getString(R.string.input_error_blank));
            isValid = false;
        } else if (dbHandler.getAllProducts().stream().map(Product::getProductName)
                .collect(Collectors.toList()).contains(newProductName)) {
            inputLayoutProductName.setError(getString(R.string.input_error_duplicate));
            isValid = false;
        }
        // is meat type valid
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
            listener.onAddNewProductDoneAction(newProduct);
            dismiss();
        }
    }

    /**
     * Interface to handle done action from dialog.
     */
    public interface AddNewProductListener {
        /**
         * This is called when the user confirms adding a product from the
         * dialog. {@link AddNewProductDialog} handles error checking so the
         * implementation should store the new product, but doesn't need to
         * perform any additional validation.
         */
        void onAddNewProductDoneAction(Product newProduct);
    }
}
