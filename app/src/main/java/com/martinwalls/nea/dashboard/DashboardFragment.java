package com.martinwalls.nea.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.martinwalls.nea.R;
import com.martinwalls.nea.components.BarChartEntry;
import com.martinwalls.nea.components.BarChartView;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.ProductQuantity;
import com.martinwalls.nea.models.StockItem;
import com.martinwalls.nea.util.EasyPreferences;
import com.martinwalls.nea.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private final int SORT_BY_NAME = 0;
    private final int SORT_BY_AMOUNT_ASC = 1;
    private final int SORT_BY_AMOUNT_DESC = 2;

    private final int SORT_BY_DEFAULT = SORT_BY_AMOUNT_DESC;

    private DBHandler dbHandler;
    private EasyPreferences prefs;

    private BarChartView graphView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.dashboard_title);
        View fragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHandler = new DBHandler(getContext());
        prefs = EasyPreferences.createForDefaultPreferences(getContext());

        graphView = fragmentView.findViewById(R.id.graph);
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
            case SORT_BY_NAME:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_name).setChecked(true);
                break;
            case SORT_BY_AMOUNT_DESC:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_amount_desc).setChecked(true);
                break;
            case SORT_BY_AMOUNT_ASC:
                menu.findItem(R.id.action_sort_by).getSubMenu().findItem(R.id.action_sort_by_amount_asc).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_name:
                setSortMode(SORT_BY_NAME);
                return true;
            case R.id.action_sort_by_amount_desc:
                setSortMode(SORT_BY_AMOUNT_DESC);
                return true;
            case R.id.action_sort_by_amount_asc:
                setSortMode(SORT_BY_AMOUNT_ASC);
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

        switch (prefs.getInt(R.string.pref_dashboard_sort_by, SORT_BY_DEFAULT)) {
            case SORT_BY_NAME:
                stockList = Utils.mergeSort(stockList, StockItem.comparatorAlpha());
                break;
            case SORT_BY_AMOUNT_ASC:
                stockList = Utils.mergeSort(stockList, StockItem.comparatorAmount(true));
                break;
            case SORT_BY_AMOUNT_DESC:
                stockList = Utils.mergeSort(stockList, StockItem.comparatorAmount(false));
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
