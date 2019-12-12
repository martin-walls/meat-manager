package com.martinwalls.meatmanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.databinding.SearchResultsLayoutBinding;

public class SearchResultsLayout extends LinearLayout {

    private final Context context;

    private SearchResultsLayoutBinding binding;

    public SearchResultsLayout(Context context) {
        this(context, null);
    }

    public SearchResultsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchResultsLayout(Context context, @Nullable AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.search_results_layout, this, true);

        binding = SearchResultsLayoutBinding.inflate(inflater);
    }

    public void openSearch(String name) {
        Transition transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.search_open);
        TransitionManager.beginDelayedTransition((ViewGroup) getRootView(), transition);

        setAlpha(0f);
        setVisibility(VISIBLE);

        animate()
                .alpha(1f)
                .setStartDelay(getResources().getInteger(R.integer.search_results_fade_delay))
                .setDuration(getResources().getInteger(R.integer.search_results_fade_duration))
                .setListener(null);
    }

    public void closeSearch() {
        Transition transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.search_close);
        TransitionManager.beginDelayedTransition((ViewGroup) getRootView(), transition);

        setVisibility(GONE);
    }
}
