package com.martinwalls.nea;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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

        // open dashboard screen at start
        if (savedInstanceState == null) {
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout navDrawerContent = findViewById(R.id.nav_drawer_content);
        TextView navDashboard = navDrawerContent.findViewById(R.id.nav_dashboard);
        navDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPage(Page.DASHBOARD);
            }
        });

        TextView navStock = navDrawerContent.findViewById(R.id.nav_stock);
        navStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPage(Page.STOCK);
            }
        });

        TextView navContracts = navDrawerContent.findViewById(R.id.nav_contracts);
        navContracts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPage(Page.CONTRACTS);
            }
        });

        TextView navExchange = navDrawerContent.findViewById(R.id.nav_exchange);
        navExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPage(Page.EXCHANGE);
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_dashboard:
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new DashboardFragment())
//                        .commit();
                replaceFragment(Page.DASHBOARD);
                break;
            case R.id.nav_stock:
//                getSupportFragmentManager().popBackStack();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new StockFragment())
//                        .addToBackStack(StockFragment.class.getSimpleName())
//                        .commit();
                replaceFragment(Page.STOCK);
                break;
            case R.id.nav_contracts:
//                getSupportFragmentManager().popBackStack();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new ContractsFragment())
//                        .addToBackStack(ContractsFragment.class.getSimpleName())
//                        .commit();
                replaceFragment(Page.CONTRACTS);
                break;
            case R.id.nav_exchange:
//                getSupportFragmentManager().popBackStack();
//
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new ExchangeFragment())
//                        .addToBackStack(ExchangeFragment.class.getSimpleName())
//                        .commit();
                replaceFragment(Page.EXCHANGE);
                break;
            case R.id.nav_settings:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchPage(Page newPage) {
        findViewById(newPage.getNavItem()).setActivated(true);
        for (Page page : Page.values()) {
            if (page != newPage) {
                findViewById(page.getNavItem()).setActivated(false);
            }
        }

        replaceFragment(newPage);
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
