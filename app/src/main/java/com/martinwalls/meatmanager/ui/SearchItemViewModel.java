package com.martinwalls.meatmanager.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.SearchItem;

import java.util.HashMap;
import java.util.List;

public abstract class SearchItemViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    protected MutableLiveData<List<SearchItem>> searchItemList;

    public SearchItemViewModel(@NonNull Application application) {
        super(application);

        dbHandler = new DBHandler(application);
    }

    public LiveData<List<SearchItem>> getSearchItemList() {
        if (searchItemList == null) searchItemList = new MutableLiveData<>();
        return searchItemList;
    }

    public abstract void loadSearchItems(String searchItemType);
}
