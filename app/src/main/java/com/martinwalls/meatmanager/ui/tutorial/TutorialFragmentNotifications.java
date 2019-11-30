package com.martinwalls.meatmanager.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.martinwalls.meatmanager.R;

public class TutorialFragmentNotifications extends Fragment {

    private TextView off;
    private TextView on;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_notifications, container, false);

        off = view.findViewById(R.id.off);
        on = view.findViewById(R.id.on);

        off.setOnClickListener(v -> setSelected(false));
        on.setOnClickListener(v -> setSelected(true));

        setSelected(true);

        return view;
    }

    private void setSelected(boolean isOn) {
        on.setSelected(isOn);
        off.setSelected(!isOn);
    }
}
