package com.martinwalls.meatmanager.ui.tutorial;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.util.AppTheme;

public class TutorialFragmentTheme extends Fragment {

    RelativeLayout rootLayout;

    private int currentTheme = AppTheme.MODE_LIGHT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_theme, container, false);

        rootLayout = view.findViewById(R.id.root_layout);

        TextView light = view.findViewById(R.id.light);
        TextView dark = view.findViewById(R.id.dark);

        light.setOnClickListener(v -> setTheme(AppTheme.MODE_LIGHT));
        dark.setOnClickListener(v -> setTheme(AppTheme.MODE_DARK));

        return view;
    }

    private void setTheme(int theme) {
        if (currentTheme != theme) {
            currentTheme = theme;
            setBackground(theme);
        }
    }

    private void setBackground(int theme) {
        int startColor = 0;
        int endColor = 0;
        switch (theme) {
            case AppTheme.MODE_DARK:
                startColor = getResources().getColor(R.color.light_blue, null);
                endColor = getResources().getColor(R.color.dark_blue, null);
                break;
            case AppTheme.MODE_LIGHT:
                startColor = getResources().getColor(R.color.dark_blue, null);
                endColor = getResources().getColor(R.color.light_blue, null);
                break;
        }

        ObjectAnimator animator = ObjectAnimator.ofObject(rootLayout, "backgroundColor",
                new ArgbEvaluator(),
                startColor,
                endColor);
        animator.setDuration(500);
        animator.start();
    }
}
