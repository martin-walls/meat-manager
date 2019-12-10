package com.martinwalls.meatmanager.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Order;
import com.martinwalls.meatmanager.ui.common.recyclerview.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.common.recyclerview.RecyclerViewDivider;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.undo.UndoStack;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment
        implements OrdersAdapter.OrdersAdapterListener {

    private DBHandler dbHandler;

    private OrdersAdapter ordersAdapter;
    private List<Order> orderList = new ArrayList<>();

    /**
     * Stores whether the layout is currently showing current orders ({@code true})
     * or the order history ({@code false}).
     */
    private boolean isCurrentView = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setActionbarTitle();
        View fragmentView = inflater.inflate(R.layout.fragment_orders, container, false);

        dbHandler = new DBHandler(getContext());

        initOrdersList(fragmentView);
        loadOrders();

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startNewOrderActivity());

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
        if (isCurrentView) {
            inflater.inflate(R.menu.fragment_orders_current, menu);
        } else {
            inflater.inflate(R.menu.fragment_orders_history, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                UndoStack.getInstance().undo(getContext());
                loadOrders();
                return true;
            case R.id.action_redo:
                UndoStack.getInstance().redo(getContext());
                loadOrders();
                return true;
            case R.id.action_current_orders:
            case R.id.action_order_history:
                isCurrentView = !isCurrentView;
                setActionbarTitle();
                getActivity().invalidateOptionsMenu();
                loadOrders();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This is called when an order item is clicked in the list. Opens the
     * detail page for that order.
     */
    @Override
    public void onOrderClicked(Order order) {
        Intent detailIntent = new Intent(getContext(), OrderDetailActivity.class);
        detailIntent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
        startActivity(detailIntent);
    }

    /**
     * Sets the title for the fragment, depending on whether the layout is
     * showing current orders or the order history.
     */
    private void setActionbarTitle() {
        if (isCurrentView) {
            getActivity().setTitle(R.string.orders_title);
        } else {
            getActivity().setTitle(R.string.orders_history_title);
        }
    }

    /**
     * Initialises the orders list. Doesn't load any data, this should be done
     * with {@link #loadOrders()}.
     */
    private void initOrdersList(View fragmentView) {
        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        ordersAdapter = new OrdersAdapter(orderList, this);
        recyclerView.setAdapter(ordersAdapter);

        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Starts {@link EditOrderActivity} to allow the user to add a new order.
     */
    private void startNewOrderActivity() {
        Intent newOrderIntent = new Intent(getContext(), EditOrderActivity.class);
        newOrderIntent.putExtra(EditOrderActivity.EXTRA_EDIT_TYPE,
                EditOrderActivity.EDIT_TYPE_NEW);
        startActivity(newOrderIntent);
    }

    /**
     * Gets either all completed or not completed orders in the database,
     * depending on whether the layout is showing current orders or the order
     * history. Sorts the orders and updates the layout to show the updated data.
     */
    private void loadOrders() {
        orderList.clear();
        // sort by date
        if (isCurrentView) {
            orderList.addAll(SortUtils.mergeSort(
                    dbHandler.getAllOrdersNotCompleted(), Order.comparatorDate()));
        } else {
            orderList.addAll(SortUtils.mergeSort(
                    dbHandler.getAllOrdersCompleted(), Order.comparatorDate()));
        }
        ordersAdapter.setCurrentView(isCurrentView);
        ordersAdapter.notifyDataSetChanged();
    }
}
