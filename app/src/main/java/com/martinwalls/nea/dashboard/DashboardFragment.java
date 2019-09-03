package com.martinwalls.nea.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.martinwalls.nea.R;
import com.martinwalls.nea.components.BarChartEntry;
import com.martinwalls.nea.components.BarChartView;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.ProductQuantity;
import com.martinwalls.nea.models.StockItem;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DBHandler dbHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.dashboard_title);
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dbHandler = new DBHandler(getContext());

        BarChartView graphView = view.findViewById(R.id.graph);

        List<BarChartEntry> entries = new ArrayList<>();

        List<StockItem> stockList = dbHandler.getAllStock();
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

/*
        HorizontalBarChart chart = view.findViewById(R.id.chart);

        List<StockItem> stockList = dbHandler.getAllStock();

        List<BarEntry> entries = new ArrayList<>();

        List<String> axisLabels = new ArrayList<>();

        float i = 0;
        for (StockItem stockItem : stockList) {
            entries.add(new BarEntry(i, (float) stockItem.getMass()));
            axisLabels.add(stockItem.getProduct().getProductName());
            i++;
        }

        IAxisValueFormatter formatter = (value, axis) -> axisLabels.get((int) value);

        BarDataSet dataSet = new BarDataSet(entries, "Stock");
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
        chart.setTouchEnabled(false);
        chart.getDescription().setText("");
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
//        chart.getXAxis().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setLabelRotationAngle(-90);
        chart.getXAxis().setValueFormatter(formatter);

        chart.getLegend().setEnabled(false);
        chart.setData(barData);
        chart.invalidate();
*/
    }
}
