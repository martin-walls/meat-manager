package com.martinwalls.meatmanager.ui.tutorial;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.martinwalls.meatmanager.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class TutorialActivity extends FragmentActivity {

    private static final int NUM_PAGES = 5;

    private ViewPager2 viewPager;

    private FragmentStateAdapter pagerAdapter;
    private WormDotsIndicator dotsIndicator;

    private Button btnNext;
    private Button btnSkip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // transparent status bar
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);

        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);

        btnNext.setOnClickListener(v -> {
            int currentPage = viewPager.getCurrentItem();
            if (currentPage < NUM_PAGES - 1) {
                viewPager.setCurrentItem(currentPage + 1);
            }
        });

        dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager2(viewPager);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

            if (position == NUM_PAGES - 1) {
                btnNext.setText(R.string.btn_done);
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText(R.string.btn_next);
                btnSkip.setVisibility(View.VISIBLE);
            }
        }
    };

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                default:
                case 0:
                    return new TutorialFragmentWelcome();
                case 1:
                    return new TutorialFragmentTheme();
                case 2:
                    return new TutorialFragmentMeatTypes();
                case 3:
                    return new TutorialFragmentMassUnit();
                case 4:
                    return new TutorialFragmentNotifications();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
