package com.martinwalls.meatmanager.ui.tutorial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.ui.common.recyclerview.RecyclerViewMargin;
import com.martinwalls.meatmanager.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TutorialFragmentMeatTypes extends Fragment
        implements TutorialMeatTypesAdapter.TutorialMeatTypesAdapterListener {

    /**
     * Meat types that are shown to the user as suggestions to add.
     */
    private final String[] MEAT_TYPES_SUGGESTED = {"Beef", "Pork", "Lamb", "Chicken"};

    private List<String> meatTypesList;
    private TutorialMeatTypesAdapter adapter;
    private LinearLayoutManager layoutManager;

    private DBHandler dbHandler;

    private TextView title;
    private LinearLayout meatTypesListLayout;
    private Button btnCustom;
    private EditText editTextCustom;
    private View greenBg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_meat_types, container, false);

        title = view.findViewById(R.id.title);
        meatTypesListLayout = view.findViewById(R.id.meat_types_layout);
        editTextCustom = view.findViewById(R.id.edit_text_custom);
        greenBg = view.findViewById(R.id.green_bg);

        dbHandler = new DBHandler(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_meat_types);

        meatTypesList = new ArrayList<>();
        meatTypesList.addAll(Arrays.asList(MEAT_TYPES_SUGGESTED));
        adapter = new TutorialMeatTypesAdapter(meatTypesList, this);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerViewMargin margin =
                new RecyclerViewMargin(Utils.convertDpToPixelSize(16, getContext()),
                        0, 0, RecyclerViewMargin.VERTICAL);
        recyclerView.addItemDecoration(margin);

        btnCustom = view.findViewById(R.id.btn_custom_meat_type);
        btnCustom.setOnClickListener(v -> showCustomMeatTypeView());

        Button btnCustomDone = view.findViewById(R.id.btn_custom_done);
        btnCustomDone.setOnClickListener(v -> onCustomInputDone());

        return view;
    }

    /**
     * If the checkbox is checked, adds the meat type to the database if it doesn't
     * already exist. If the checkbox is unchecked, removes the meat type from the
     * database if it is there.
     */
    @Override
    public void onMeatTypeChecked(String name, boolean isChecked) {
        if (isChecked) {
            addMeatTypeToDb(name);
        } else {
            removeMeatTypeFromDb(name);
        }
    }

    /**
     * Adds a new meat type to the database with the given name, if it doesn't
     * already exist.
     */
    private void addMeatTypeToDb(String name) {
        if (!dbHandler.isMeatTypeInDb(name)) {
            dbHandler.addMeatType(name);
        }
    }

    /**
     * Removes the given meat type from the database, if it exists.
     */
    private void removeMeatTypeFromDb(String name) {
        if (dbHandler.isMeatTypeInDb(name)) {
            dbHandler.deleteMeatType(name);
        }
    }

    /**
     * This is called when the user selects the done button from the custom input
     * layout. Returns to the normal layout, with a list of meat types, and adds
     * the custom meat type to the list.
     */
    private void onCustomInputDone() {
        showNormalView();

        String customInput = editTextCustom.getText().toString();
        if (!TextUtils.isEmpty(customInput)) {
            boolean isValid = true;

            for (String meatType : meatTypesList) {
                if (meatType.toLowerCase().equals(customInput.toLowerCase())) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                meatTypesList.add(customInput);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), R.string.tutorial_meat_types_custom_not_unique,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Shows the layout that allows the user to input a custom meat type.
     */
    private void showCustomMeatTypeView() {
        int cx = (int) (btnCustom.getX() + btnCustom.getWidth() / 2);
        int cy = (int) (btnCustom.getY() + btnCustom.getHeight() / 2);
        float finalRadius = (float) Math.hypot(cx, cy);

        Animator animator = ViewAnimationUtils.createCircularReveal(greenBg, cx, cy, 0, finalRadius);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                meatTypesListLayout.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                btnCustom.setVisibility(View.GONE);

                editTextCustom.requestFocus();
                showKeyboard(true);
            }
        });

        greenBg.setVisibility(View.VISIBLE);
        animator.start();

        btnCustom.setEnabled(false);
    }

    /**
     * Returns to the normal layout.
     */
    private void showNormalView() {
        int cx = (int) (btnCustom.getX() + btnCustom.getWidth() / 2);
        int cy = (int) (btnCustom.getY() + btnCustom.getHeight() / 2);
        float startRadius = (float) Math.hypot(cx, cy);

        Animator animator = ViewAnimationUtils.createCircularReveal(greenBg, cx, cy, startRadius, 0);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                greenBg.setVisibility(View.GONE);
                btnCustom.setEnabled(true);
                editTextCustom.setText("");
            }
        });

        animator.start();

        meatTypesListLayout.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        btnCustom.setVisibility(View.VISIBLE);

        showKeyboard(false);
    }

    /**
     * Shows/hides the on-screen keyboard.
     */
    private void showKeyboard(boolean show) {
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (show) {
                imm.showSoftInput(editTextCustom, 0);
            } else {
                imm.hideSoftInputFromWindow(editTextCustom.getWindowToken(), 0);
            }
        }
    }
}
