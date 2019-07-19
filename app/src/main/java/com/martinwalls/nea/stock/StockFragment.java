package com.martinwalls.nea.stock;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.db.models.StockItem;
import com.martinwalls.nea.ui_components.CustomRecyclerView;
import com.martinwalls.nea.ui_components.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

public class StockFragment extends Fragment {

    private StockItemAdapter stockAdapter;
    private List<StockItem> stockList = new ArrayList<>();

    private DBHandler dbHandler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbHandler = new DBHandler(getContext());

        View fragmentView = inflater.inflate(R.layout.fragment_stock, container, false);

//        stockList.addAll(SampleData.getSampleStock());

        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        stockAdapter = new StockItemAdapter(stockList);
        recyclerView.setAdapter(stockAdapter);
        loadStock();

        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent newStockIntent = new Intent(getContext(), NewStockActivity.class);
            startActivity(newStockIntent);
        });

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.stock_title);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStock(); //todo maybe needs to be in onStart()?
    }

    @Override
    public void onStop() {
        super.onStop();
        dbHandler.close();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_stock, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_undo:
                Toast.makeText(getContext(), "UNDO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_redo:
                Toast.makeText(getContext(), "REDO", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadStock() { //todo currently not returning any data due to inner joins, no data in other tables
        stockList.clear();
        stockList.addAll(dbHandler.getAllStock());
        stockAdapter.notifyDataSetChanged();
    }
}
