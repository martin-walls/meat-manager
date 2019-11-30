package com.martinwalls.meatmanager.ui.tutorial;

import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.util.AppTheme;
import com.martinwalls.meatmanager.util.EasyPreferences;

public class TutorialFragmentTheme extends Fragment {

    private EasyPreferences prefs;

    private RelativeLayout rootLayout;

    private int currentTheme = AppTheme.MODE_LIGHT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_theme, container, false);

        prefs = EasyPreferences.getInstance(getContext());

        rootLayout = view.findViewById(R.id.root_layout);

        TextView light = view.findViewById(R.id.light);
        TextView dark = view.findViewById(R.id.dark);
        Button systemDefault = view.findViewById(R.id.system_default);

        light.setOnClickListener(v -> setTheme(AppTheme.MODE_LIGHT));
        dark.setOnClickListener(v -> setTheme(AppTheme.MODE_DARK));
        systemDefault.setOnClickListener(v -> setTheme(AppTheme.MODE_AUTO));

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            systemDefault.setVisibility(View.GONE);
        }

        setTheme(AppTheme.MODE_AUTO);

        return view;
    }

    /**
     * Sets the theme to the new value.
     */
    private void setTheme(int theme) {
        if (currentTheme != theme) {
            if (theme == AppTheme.MODE_AUTO) {
                int nightMode = getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;
                switch (nightMode) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        if (currentTheme != AppTheme.MODE_DARK) {
                            setBackground(AppTheme.MODE_DARK);
                        }
                        break;
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    case Configuration.UI_MODE_NIGHT_NO:
                        if (currentTheme != AppTheme.MODE_LIGHT) {
                            setBackground(AppTheme.MODE_LIGHT);
                        }
                        break;
                }
            } else {
                // if new theme same as system theme set by auto
                if (currentTheme == AppTheme.MODE_AUTO
                        && AppTheme.getCurrentSystemTheme(getContext()) == theme) {
                    updateCurrentTheme(theme);
                    return;
                }
                setBackground(theme);
            }
            updateCurrentTheme(theme);
        }
    }

    /**
     * Stores the new theme in preferences and updates app to show the new theme.
     */
    private void updateCurrentTheme(int theme) {
        currentTheme = theme;
        AppTheme.setAppTheme(theme);
        prefs.setIntToString(R.string.pref_theme, theme);
    }

    /**
     * Sets the background colour of the fragment to light/dark depending on the
     * theme that is set. Fades between colours.
     */
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
        animator.setDuration(
                getResources().getInteger(R.integer.tutorial_theme_color_fade_duration));
        animator.start();
    }
}
