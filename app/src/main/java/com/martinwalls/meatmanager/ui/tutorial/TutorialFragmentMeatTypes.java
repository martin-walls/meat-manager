package com.martinwalls.meatmanager.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.ui.misc.RecyclerViewMargin;
import com.martinwalls.meatmanager.util.Utils;

import java.util.Arrays;
import java.util.List;

public class TutorialFragmentMeatTypes extends Fragment
        implements TutorialMeatTypesAdapter.TutorialMeatTypesAdapterListener {

    private final String[] MEAT_TYPES_SUGGESTED = {"Beef", "Pork", "Lamb", "Chicken"};

    private List<String> meatTypesList;

    private DBHandler dbHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_meat_types, container, false);

        dbHandler = new DBHandler(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_meat_types);

        meatTypesList = Arrays.asList(MEAT_TYPES_SUGGESTED);
        TutorialMeatTypesAdapter adapter = new TutorialMeatTypesAdapter(meatTypesList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerViewMargin margin =
                new RecyclerViewMargin(Utils.convertDpToPixelSize(16, getContext()),
                        0, 0,
                        RecyclerViewMargin.VERTICAL);
        recyclerView.addItemDecoration(margin);

        Button btnCustom = view.findViewById(R.id.btn_custom_meat_type);
        btnCustom.setOnClickListener(v -> addCustomMeatType());

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
            if (!dbHandler.isMeatTypeInDb(name)) {
                dbHandler.addMeatType(name);
            }
        } else {
            if (dbHandler.isMeatTypeInDb(name)) {
                dbHandler.deleteMeatType(name);
            }
        }
    }

    /**
     * This is called when the add custom meat type button is clicked.
     */
    private void addCustomMeatType() {
        Toast.makeText(getContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
    }

}
