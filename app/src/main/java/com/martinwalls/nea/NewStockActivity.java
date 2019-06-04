package com.martinwalls.nea;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.R;

import java.util.ArrayList;
import java.util.List;

public class NewStockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextInputEditText inputProduct = findViewById(R.id.input_product_edit_text);
        inputProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputProductOnClick();
                }
            }
        });
    }

    private void inputProductOnClick() {
        DialogFragment dialog = new SelectItemDialog();
        dialog.show(getSupportFragmentManager(), "select item dialog");
    }
}
