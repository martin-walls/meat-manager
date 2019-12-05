package com.martinwalls.meatmanager.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.ui.misc.RecyclerViewMargin;
import com.martinwalls.meatmanager.util.EasyPreferences;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.Utils;

public class DashboardFragment extends Fragment
        implements LocationsMenuAdapter.LocationsMenuAdapterListener {

    private final int SORT_BY_DEFAULT = SortUtils.SORT_AMOUNT_DESC;

    private EasyPreferences prefs;
    private DashboardViewModel viewModel;

    private BarChartView chartView;
    private TextView emptyView;
    private ScrollView chartLayout;

    LocationsMenuAdapter locationsAdapter;

    private LocationFilter filterLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.dashboard_title);
        View fragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        viewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);

        prefs = EasyPreferences.getInstance(getContext());

        if (filterLocation == null) {
            filterLocation = new LocationFilter(true);
        }

        initLocationFilterMenu(fragmentView);

        chartView = fragmentView.findViewById(R.id.graph);
        emptyView = fragmentView.findViewById(R.id.empty);
        chartLayout = fragmentView.findViewById(R.id.graph_layout);

        viewModel.getChartDataObservable().observe(getViewLifecycleOwner(), entries -> {
            chartView.setData(entries);
            showEmptyView(entries.size() == 0);
        });

        viewModel.getLocationsObservable().observe(getViewLifecycleOwner(),
                locations -> locationsAdapter.setLocationsList(locations));

        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_dashboard, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        switch (prefs.getInt(R.string.pref_dashboard_sort_by, SORT_BY_DEFAULT)) {
            case SortUtils.SORT_NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_name).setChecked(true);
                break;
            case SortUtils.SORT_AMOUNT_DESC:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_amount_desc).setChecked(true);
                break;
            case SortUtils.SORT_AMOUNT_ASC:
                menu.findItem(R.id.action_sort_by).getSubMenu()
                        .findItem(R.id.action_sort_by_amount_asc).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                setSortMode(SortUtils.SORT_NAME);
                return true;
            case R.id.action_sort_by_amount_desc:
                setSortMode(SortUtils.SORT_AMOUNT_DESC);
                return true;
            case R.id.action_sort_by_amount_asc:
                setSortMode(SortUtils.SORT_AMOUNT_ASC);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationItemClicked(LocationFilter location) {
        filterLocation = location;
        if (filterLocation.filterAll()) {
            viewModel.filterByLocation(DashboardViewModel.FILTER_ALL);
        } else {
            viewModel.filterByLocation(location.getId());
        }
    }

    /**
     * Initialises menu to allow the user to filter by location.
     */
    private void initLocationFilterMenu(View fragmentView) {
        RecyclerView locationsRecyclerView =
                fragmentView.findViewById(R.id.recycler_view_locations);
        locationsAdapter = new LocationsMenuAdapter(this);
        locationsRecyclerView.setAdapter(locationsAdapter);

        RecyclerViewMargin margins = new RecyclerViewMargin(
                Utils.convertDpToPixelSize(16, getContext()), RecyclerViewMargin.HORIZONTAL);
        locationsRecyclerView.addItemDecoration(margins);

        locationsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    /**
     * Sets which property to sort stock by, then reloads the data.
     */
    private void setSortMode(int sortMode) {
        prefs.setInt(R.string.pref_dashboard_sort_by, sortMode);
        getActivity().invalidateOptionsMenu();
        viewModel.sortChartData(sortMode);
    }

    /**
     * Shows/hides the empty view.
     */
    private void showEmptyView(boolean showEmptyView) {
        if (showEmptyView) {
            emptyView.setVisibility(View.VISIBLE);
            chartLayout.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            chartLayout.setVisibility(View.VISIBLE);
        }
    }
}
