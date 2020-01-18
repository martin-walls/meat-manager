package com.martinwalls.meatmanager.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.meatmanager.R;

public class EditTextValidator implements TextWatcher {

    public static final int VALIDATE_EMPTY = 1;
    public static final int VALIDATE_NON_ZERO = 2;

    private final TextInputLayout inputLayout;
    private final TextInputEditText editText;
    private final int flags;

    public EditTextValidator(TextInputLayout inputLayout,
                             TextInputEditText editText,
                             int flags) {
        this.inputLayout = inputLayout;
        this.editText = editText;
        this.flags = flags;
    }

    @Override
    public void afterTextChanged(Editable s) {
        validate();
    }

    public boolean validate() {
        if ((flags & VALIDATE_EMPTY) == VALIDATE_EMPTY) {
            if (TextUtils.isEmpty(editText.getText())) {
                inputLayout.setError(inputLayout.getContext().getString(R.string.input_error_blank));
                return false;
            }
        }

        if ((flags & VALIDATE_NON_ZERO) == VALIDATE_NON_ZERO) {
            if (!validateNonZeroNumber(editText.getText())) {
                inputLayout.setError(inputLayout.getContext().getString(R.string.input_error_non_zero));
                return false;
            }
        }

        // if got this far, no validation errors have been found
        inputLayout.setError(null);
        return true;
    }

    private boolean validateNonZeroNumber(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            String text = s.toString();
            try {
                double value = Double.parseDouble(text);
                if (value == 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
