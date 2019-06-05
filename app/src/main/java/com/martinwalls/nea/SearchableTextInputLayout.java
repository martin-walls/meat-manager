package com.martinwalls.nea;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SearchableTextInputLayout extends TextInputLayout {

    public SearchableTextInputLayout(Context context) {
        super(context);
    }

    public SearchableTextInputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchableTextInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas arg0) {
        super.onDraw(arg0);
        setEndIconMode(END_ICON_CUSTOM);
        setEndIconDrawable(R.drawable.ic_cancel);
        setEndIconVisible(false);

        final TextInputEditText editText = (TextInputEditText) getEditText();
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    setEndIconVisible(true);
                }
            }
        });

        setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                editText.clearFocus();
                setEndIconVisible(false);
            }
        });
    }
}
