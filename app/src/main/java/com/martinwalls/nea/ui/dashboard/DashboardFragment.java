package com.martinwalls.nea.ui.dashboard;

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
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.ProductQuantity;
import com.martinwalls.nea.data.models.StockItem;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.SortMode;
import com.martinwalls.nea.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private final int SORT_BY_DEFAULT = SortMode.AMOUNT_DESC;

    private DBHandler dbHandler;
    private EasyPreferences prefs;

    private BarChartView graphView;
    private TextView emptyView;
    private ScrollView graphLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.dashboard_title);
        View fragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHandler = new DBHandler(getContext());
        prefs = EasyPreferences.createForDefaultPreferences(getContext());

        graphView = fragmentView.findViewById(R.id.graph);
        emptyView = fragmentView.findViewById(R.id.empty);
        graphLayout = fragmentView.findViewById(R.id.graph_layout);
        loadData();

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
            case SortMode.NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_name).setChecked(true);
                break;
            case SortMode.AMOUNT_DESC:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_amount_desc).setChecked(true);
                break;
            case SortMode.AMOUNT_ASC:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_amount_asc).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                setSortMode(SortMode.NAME);
                return true;
            case R.id.action_sort_by_amount_desc:
                setSortMode(SortMode.AMOUNT_DESC);
                return true;
            case R.id.action_sort_by_amount_asc:
                setSortMode(SortMode.AMOUNT_ASC);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSortMode(int sortMode) {
        prefs.setInt(R.string.pref_dashboard_sort_by, sortMode);
        getActivity().invalidateOptionsMenu();
        loadData();
    }

    private void loadData() {
        List<BarChartEntry> entries = new ArrayList<>();

        List<StockItem> stockList = dbHandler.getAllStock();

        if (stockList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            graphLayout.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            graphLayout.setVisibility(View.VISIBLE);
        }


        switch (prefs.getInt(R.string.pref_dashboard_sort_by, SORT_BY_DEFAULT)) {
            case SortMode.NAME:
                stockList = Utils.mergeSort(stockList, StockItem.comparatorAlpha());
                break;
            case SortMode.AMOUNT_DESC:
                stockList = Utils.mergeSort(stockList, StockItem.comparatorAmount(false));
                break;
            case SortMode.AMOUNT_ASC:
                stockList = Utils.mergeSort(stockList, StockItem.comparatorAmount(true));
                break;
        }

        List<ProductQuantity> productsRequiredList = dbHandler.getAllProductsRequired();

        for (StockItem stockItem : stockList) {
            int productId = stockItem.getProduct().getProductId();
            float amountRequired = 0;
            for (ProductQuantity productRequired : productsRequiredList) {
                if (productRequired.getProduct().getProductId() == productId) {
                    amountRequired += productRequired.getQuantityMass();
                }
            }

            entries.add(new BarChartEntry(stockItem.getProduct().getProductName(),
                    (float) stockItem.getMass(), amountRequired));
        }

        graphView.setData(entries);
    }
}
