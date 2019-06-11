package com.martinwalls.nea;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_dashboard:
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new DashboardFragment())
//                        .commit();
                replaceFragment(new DashboardFragment(), false);
                break;
            case R.id.nav_stock:
//                getSupportFragmentManager().popBackStack();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new StockFragment())
//                        .addToBackStack(StockFragment.class.getSimpleName())
//                        .commit();
                replaceFragment(new StockFragment(), true);
                break;
            case R.id.nav_contracts:
//                getSupportFragmentManager().popBackStack();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new ContractsFragment())
//                        .addToBackStack(ContractsFragment.class.getSimpleName())
//                        .commit();
                replaceFragment(new ContractsFragment(), true);
                break;
            case R.id.nav_exchange:
//                getSupportFragmentManager().popBackStack();
//
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_holder, new ExchangeFragment())
//                        .addToBackStack(ExchangeFragment.class.getSimpleName())
//                        .commit();
                replaceFragment(new ExchangeFragment(), true);
                break;
            case R.id.nav_settings:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment newFragment, boolean clearBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (clearBackStack) {
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        //todo maybe not this back stack implementation - maybe have back not go back to dashboard but stay on current tab?

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_holder, newFragment)
                .addToBackStack(clearBackStack ? BACK_STACK_ROOT_TAG : null)
                .commit();
    }
}
