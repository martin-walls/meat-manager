package com.martinwalls.meatmanager.ui.contracts.edit;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.Utils;

import java.util.List;

public class SelectProductViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private final MutableLiveData<List<Product>> productList;

    public SelectProductViewModel(Application application) {
        super(application);

        dbHandler = new DBHandler(application);

        productList = new MutableLiveData<>();
    }

    public LiveData<List<Product>> getProductListObservable() {
        loadProducts();
        return productList;
    }

    public void loadProducts() {
        productList.setValue(
                SortUtils.mergeSort(
                        dbHandler.getAllProducts(),
                        Product.comparatorAlpha()));
    }
}
