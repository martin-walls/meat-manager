package com.martinwalls.meatmanager.ui.orders.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Order;
import com.martinwalls.meatmanager.ui.common.adapter.ProductsQuantityAdapter;
import com.martinwalls.meatmanager.util.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final String DATE_FORMAT = "d MMMM";

    private List<Order> orderList;
    private OrdersAdapterListener listener;

    private boolean isCurrentView = true;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView orderDest;
        private TextView dateDivider;
        private RecyclerView recyclerView;

        ViewHolder(View view, Context context) {
            super(view);
            this.context = context;
            orderDest = view.findViewById(R.id.order_dest);
            dateDivider = view.findViewById(R.id.date_divider);
            recyclerView = view.findViewById(R.id.recycler_view);

            LinearLayout orderInfo = view.findViewById(R.id.order_info);
            orderInfo.setOnClickListener(v ->
                    listener.onOrderClicked(orderList.get(getAdapterPosition())));
        }
    }

    OrdersAdapter(OrdersAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(itemView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderDest.setText(order.getDestName());
        ProductsQuantityAdapter adapter = new ProductsQuantityAdapter(order.getProductList());
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(
                new LinearLayoutManager(holder.context));
        // allow click events to be passed to parent layout
        holder.recyclerView.suppressLayout(true);

        if (position == 0
                || orderList.get(position - 1)
                .getOrderDate().isBefore(order.getOrderDate())) {
            holder.dateDivider.setText(
                    order.getOrderDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            holder.dateDivider.setVisibility(View.VISIBLE);
        } else {
            holder.dateDivider.setVisibility(View.GONE);
        }

        // show past dates in red
        if (isCurrentView && order.getOrderDate().isBefore(LocalDateTime.now())) {
            holder.dateDivider.setTextColor(holder.context.getColor(R.color.error_red));
        } else {
            holder.dateDivider.setTextColor(
                    Utils.getColorFromTheme(holder.context, R.attr.textColorEmphasis));
        }
    }

    @Override
    public int getItemCount() {
        if (orderList == null) {
            return 0;
        }
        return orderList.size();
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    /**
     * Sets whether the adapter is showing current orders or the order history,
     * which is used when formatting colours of the date dividers for past dates.
     */
    public void setCurrentView(boolean isCurrentView) {
        this.isCurrentView = isCurrentView;
    }

    /**
     * Interface to handle clicks on order items.
     */
    public interface OrdersAdapterListener {
        /**
         * This is called when an order in the list is clicked.
         */
        void onOrderClicked(Order order);
    }
}
