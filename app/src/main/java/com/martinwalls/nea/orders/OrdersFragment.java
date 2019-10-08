package com.martinwalls.nea.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.Utils;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdersFragment extends Fragment
        implements OrdersAdapter.OrdersAdapterListener {

    private DBHandler dbHandler;

    private OrdersAdapter ordersAdapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.orders_title);
        View fragmentView = inflater.inflate(R.layout.fragment_orders, container, false);

        dbHandler = new DBHandler(getContext());

        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        ordersAdapter = new OrdersAdapter(orderList, this);
        recyclerView.setAdapter(ordersAdapter);
        loadOrders();

        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent newOrderIntent = new Intent(getContext(), NewOrderActivity.class);
            startActivity(newOrderIntent);
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(getContext());
        }
        loadOrders();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_orders, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // todo implement undo/redo
            case R.id.action_undo:
                Toast.makeText(getContext(), "UNDO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_redo:
                Toast.makeText(getContext(), "REDO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_order_history:
                //todo order history
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOrderClicked(Order order) {
        Intent detailIntent = new Intent(getContext(), OrderDetailActivity.class);
        detailIntent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
        startActivity(detailIntent);
    }

    private void loadOrders() {
        orderList.clear();
        // sort by date
        orderList.addAll(Utils.mergeSort(dbHandler.getAllOrders(), new Comparator<Order>() {
            @Override
            public int compare(Order order1, Order order2) {
                return order1.getOrderDate().compareTo(order2.getOrderDate());
            }
        }));
        ordersAdapter.notifyDataSetChanged();
    }
}
