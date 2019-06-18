package com.martinwalls.nea;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {


    private enum Page {
        DASHBOARD(new DashboardFragment(), R.id.nav_dashboard),
        STOCK(new StockFragment(), R.id.nav_stock),
        CONTRACTS(new ContractsFragment(), R.id.nav_contracts),
        EXCHANGE(new ExchangeFragment(), R.id.nav_exchange);

        private Fragment fragment;
        private int navItem;

        Page(Fragment fragment, int navItem) {
            this.fragment = fragment;
            this.navItem = navItem;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public int getNavItem() {
            return navItem;
        }
    }

    private Page currentPage = Page.DASHBOARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // open dashboard screen at start
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_holder, new DashboardFragment())
                    .commit();
            currentPage = Page.DASHBOARD;
            findViewById(R.id.nav_dashboard).setActivated(true);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup nav drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerSlideAnimationEnabled(false);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                findViewById(currentPage.getNavItem()).setActivated(true);
                for (Page page : Page.values()) {
                    if (page != currentPage) {
                        findViewById(page.getNavItem()).setActivated(false);
                    }
                }
            }
        });

        LinearLayout navDrawerContent = findViewById(R.id.nav_drawer_content);
        TextView navDashboard = navDrawerContent.findViewById(R.id.nav_dashboard);
        navDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = Page.DASHBOARD;
                replaceFragment(Page.DASHBOARD);
            }
        });

        TextView navStock = navDrawerContent.findViewById(R.id.nav_stock);
        navStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = Page.STOCK;
                replaceFragment(Page.STOCK);
            }
        });

        TextView navContracts = navDrawerContent.findViewById(R.id.nav_contracts);
        navContracts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = Page.CONTRACTS;
                replaceFragment(Page.CONTRACTS);
            }
        });

        TextView navExchange = navDrawerContent.findViewById(R.id.nav_exchange);
        navExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = Page.EXCHANGE;
                replaceFragment(Page.EXCHANGE);
            }
        });

        TextView navSettings = navDrawerContent.findViewById(R.id.nav_settings);
        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if (currentPage != Page.DASHBOARD) {
                currentPage = Page.DASHBOARD;
            }
        }
    }

    private void replaceFragment(Page newPage) {
        Fragment newFragment = newPage.getFragment();

//        switch (newPage) {
//            case DASHBOARD:
//                newFragment = new DashboardFragment();
//                break;
//            case STOCK:
//                newFragment = new StockFragment();
//                break;
//            case CONTRACTS:
//                newFragment = new ContractsFragment();
//                break;
//            case EXCHANGE:
//                newFragment = new ExchangeFragment();
//                break;
//            default:
//                newFragment = new Fragment();
//                break;
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.popBackStack();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_holder, newFragment)
                .addToBackStack(newFragment.getClass().getSimpleName())
                .commit();

        currentPage = newPage;

        // close nav drawer if open
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
