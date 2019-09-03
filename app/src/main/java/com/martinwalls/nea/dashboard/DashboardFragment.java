package com.martinwalls.nea.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.martinwalls.nea.R;
import com.martinwalls.nea.components.BarChartView;
import com.martinwalls.nea.db.DBHandler;

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

        graphView.setData(dbHandler.getAllStock());

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
