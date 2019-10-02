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
        View fragmentView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHandler = new DBHandler(getContext());

        BarChartView graphView = fragmentView.findViewById(R.id.graph);

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

        return fragmentView;
    }
}
