package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;

import androidx.annotation.NonNull;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.data.models.SearchItem;
import com.martinwalls.meatmanager.ui.SearchItemViewModel;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class EditContractSearchItemViewModel extends SearchItemViewModel {

    private final DBHandler dbHandler;

    public EditContractSearchItemViewModel(@NonNull Application application) {
        super(application);

        dbHandler = new DBHandler(application);
    }

    @Override
    public void loadSearchItems(String searchItemType) {
        List<SearchItem> newSearchItems = new ArrayList<>();
        switch (searchItemType) {
            case "product":
                for (Product product : SortUtils.mergeSort(
                        dbHandler.getAllProducts(), Product.comparatorAlpha())) {
                    newSearchItems.add(
                            new SearchItem(product.getProductName(), product.getProductId()));
                }
                break;
            case "destination":
                for (Location location : SortUtils.mergeSort(
                        dbHandler.getAllLocations(Location.LocationType.Destination),
                        Location.comparatorAlpha())) {
                    newSearchItems.add(new SearchItem(
                            location.getLocationName(), location.getLocationId()));
                }
                break;
        }
        searchItemList.setValue(newSearchItems);
    }
}
