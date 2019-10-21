package com.martinwalls.nea.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.ProductsAddedAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.ProductQuantity;

import java.util.ArrayList;
import java.util.List;

public class EditOrderFragment extends Fragment
        implements ProductsAddedAdapter.ProductsAddedAdapterListener {

    private DBHandler dbHandler;

    private ProductsAddedAdapter productsAddedAdapter;
    private List<ProductQuantity> productsAddedList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.order_edit_title);

        View fragmentView = inflater.inflate(R.layout.fragment_edit_order, container, false);

        dbHandler = new DBHandler(getContext());

        productsAddedAdapter =
                new ProductsAddedAdapter(productsAddedList, this, false, true);
        RecyclerView productsAddedRecyclerView = fragmentView.findViewById(R.id.products_added_recycler_view);
        productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(getContext());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.activity_order_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
