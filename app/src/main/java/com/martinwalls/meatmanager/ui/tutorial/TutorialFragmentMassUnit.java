package com.martinwalls.meatmanager.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.util.MassUnit;

public class TutorialFragmentMassUnit extends Fragment {

    private TextView kgs;
    private TextView lbs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_mass_unit, container, false);

        kgs = view.findViewById(R.id.kgs);
        lbs = view.findViewById(R.id.lbs);

        kgs.setOnClickListener(v -> setSelected(MassUnit.KG));
        lbs.setOnClickListener(v -> setSelected(MassUnit.LBS));

        setSelected(MassUnit.KG);

        return view;
    }

    private void setSelected(MassUnit massUnit) {
        switch (massUnit) {
            case KG:
                kgs.setSelected(true);
                lbs.setSelected(false);
                break;
            case LBS:
                kgs.setSelected(false);
                lbs.setSelected(true);
                break;
        }
    }
}
