package com.martinwalls.meatmanager.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.martinwalls.meatmanager.R;

public class TutorialFragmentNotifications extends Fragment {

    private TextView btnOff;
    private TextView btnOn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_notifications, container, false);

        btnOff = view.findViewById(R.id.btn_off);
        btnOn = view.findViewById(R.id.btn_on);

        btnOff.setOnClickListener(v -> setSelected(false));
        btnOn.setOnClickListener(v -> setSelected(true));

        setSelected(true);

        return view;
    }

    private void setSelected(boolean isOn) {
        btnOn.setSelected(isOn);
        btnOff.setSelected(!isOn);
    }
}
