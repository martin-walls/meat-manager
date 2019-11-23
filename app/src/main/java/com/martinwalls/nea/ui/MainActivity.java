package com.martinwalls.nea.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.martinwalls.nea.R;
import com.martinwalls.nea.ui.contracts.ContractsFragment;
import com.martinwalls.nea.ui.dashboard.DashboardFragment;
import com.martinwalls.nea.ui.exchange.ExchangeFragment;
import com.martinwalls.nea.ui.orders.OrdersFragment;
import com.martinwalls.nea.ui.settings.SettingsActivity;
import com.martinwalls.nea.ui.stock.StockFragment;
import com.martinwalls.nea.util.EasyPreferences;

public class MainActivity extends AppCompatActivity {

    /**
     * Request code for fetching data from the API.
     */
    public static final int REQUEST_EXCHANGE_API_SERVICE = 1;

    /**
     * Stores references to each page of the app, to enable easy switching
     * between them.
     */
    private enum Page {
        DASHBOARD(new DashboardFragment(), R.id.nav_dashboard),
        STOCK(new StockFragment(), R.id.nav_stock),
        ORDERS(new OrdersFragment(), R.id.nav_orders),
        CONTRACTS(new ContractsFragment(), R.id.nav_contracts),
        EXCHANGE(new ExchangeFragment(), R.id.nav_exchange);

        /**
         * The Fragment for this page.
         */
        private Fragment fragment;
        /**
         * The ID of the View for this page in the navigation drawer.
         */
        private int navItem;

        Page(Fragment fragment, @IdRes int navItem) {
            this.fragment = fragment;
            this.navItem = navItem;
        }

        /**
         * Returns the Fragment for this page.
         */
        public Fragment getFragment() {
            return fragment;
        }

        /**
         * Returns the ID of the navigation drawer item for this page.
         */
        @IdRes
        public int getNavItem() {
            return navItem;
        }

        /**
         * Parses a {@link Page} from its name. This should be the same as the String
         * returned by {@link #name()}.
         */
        public static Page parsePage(String name) {
            for (Page page : values()) {
                if (page.name().equalsIgnoreCase(name)) {
                    return page;
                }
            }
            return null;
        }
    }

    private EasyPreferences preferences;

    private Page currentPage = Page.DASHBOARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = EasyPreferences.createForDefaultPreferences(this);

        if (savedInstanceState == null) {
            // open last opened page
            currentPage = Page.parsePage(preferences.getString(
                    R.string.pref_last_opened_page, Page.DASHBOARD.name()));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_holder, currentPage.getFragment())
                    .commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavDrawer(toolbar);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXCHANGE_API_SERVICE) {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_holder);
            if (fragment instanceof ExchangeFragment) {
                ((ExchangeFragment) fragment).onRatesFetched(data);
            }
        }
    }

    /**
     * Switches the Fragment currently open to the one the user has navigated
     * to. Closes the navigation drawer. The previous Fragment is added to
     * history so it can be navigated back to.
     */
    private void replaceFragment(Page newPage) {
        // don't replace fragment if same page
        if (currentPage != newPage) {
            Fragment newFragment = newPage.getFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.popBackStack();

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, newFragment)
                    .addToBackStack(newFragment.getClass().getSimpleName())
                    .commit();

            currentPage = newPage;

            preferences.setString(R.string.pref_last_opened_page, currentPage.name());
        }

        // close nav drawer if open
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Initialises the navigation drawer.
     *
     * @param toolbar A reference to this Activity's Toolbar, this is needed
     *                to set the nav drawer toggle.
     */
    private void initNavDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerSlideAnimationEnabled(false);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                activateCurrentNavDrawerItem();
            }
        });

        setNavDrawerItemListeners();
    }

    /**
     * Activates the nav drawer item for the current page, and dis-activates
     * all other items so only the current one is selected.
     */
    private void activateCurrentNavDrawerItem() {
        findViewById(currentPage.getNavItem()).setActivated(true);
        for (Page page : Page.values()) {
            if (page != currentPage) {
                findViewById(page.getNavItem()).setActivated(false);
            }
        }
    }

    /**
     * Sets listeners for navigation drawer items to open the corresponding
     * page of the app.
     */
    private void setNavDrawerItemListeners() {
        LinearLayout navDrawerContent = findViewById(R.id.nav_drawer_content);

        TextView navDashboard = navDrawerContent.findViewById(R.id.nav_dashboard);
        navDashboard.setOnClickListener(v -> replaceFragment(Page.DASHBOARD));

        TextView navStock = navDrawerContent.findViewById(R.id.nav_stock);
        navStock.setOnClickListener(v -> replaceFragment(Page.STOCK));

        TextView navOrders = navDrawerContent.findViewById(R.id.nav_orders);
        navOrders.setOnClickListener(v -> replaceFragment(Page.ORDERS));

        TextView navContracts = navDrawerContent.findViewById(R.id.nav_contracts);
        navContracts.setOnClickListener(v -> replaceFragment(Page.CONTRACTS));

        TextView navExchange = navDrawerContent.findViewById(R.id.nav_exchange);
        navExchange.setOnClickListener(v -> replaceFragment(Page.EXCHANGE));

        TextView navSettings = navDrawerContent.findViewById(R.id.nav_settings);
        navSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        });
    }
}
