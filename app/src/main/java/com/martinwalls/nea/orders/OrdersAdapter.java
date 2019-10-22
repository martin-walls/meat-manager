package com.martinwalls.nea.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.ProductsQuantityAdapter;
import com.martinwalls.nea.R;
import com.martinwalls.nea.models.Order;
import com.martinwalls.nea.util.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final String DATE_FORMAT = "d MMMM";

    private List<Order> orderList;
    private OrdersAdapterListener listener;

    private boolean isCurrentView = true;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderDest, dateDivider;
        private RecyclerView recyclerView;

        ViewHolder(View view) {
            super(view);
            orderDest = view.findViewById(R.id.order_dest);
            dateDivider = view.findViewById(R.id.date_divider);
            recyclerView = view.findViewById(R.id.recycler_view);

            LinearLayout orderInfo = view.findViewById(R.id.order_info);
            orderInfo.setOnClickListener(v -> listener.onOrderClicked(orderList.get(getAdapterPosition())));
        }
    }

    OrdersAdapter(List<Order> orderList, OrdersAdapterListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Order order = orderList.get(position);
        viewHolder.orderDest.setText(order.getDestName());
        ProductsQuantityAdapter adapter = new ProductsQuantityAdapter(order.getProductList());
        viewHolder.recyclerView.setAdapter(adapter);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder.recyclerView.getContext()));
        // allow click events to be passed to parent layout
        viewHolder.recyclerView.suppressLayout(true);

        if (position > 0 && orderList.get(position - 1).getOrderDate().isBefore(order.getOrderDate())
                || position == 0) {
            viewHolder.dateDivider.setText(order.getOrderDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            viewHolder.dateDivider.setVisibility(View.VISIBLE);
        }

        // show past dates in red
        if (isCurrentView && order.getOrderDate().isBefore(LocalDateTime.now())) {
            viewHolder.dateDivider.setTextColor(viewHolder.dateDivider.getContext().getColor(R.color.error_red));
        } else {
            viewHolder.dateDivider.setTextColor(
                    Utils.getColorFromTheme(viewHolder.dateDivider.getContext(), R.attr.textColorEmphasis));
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setCurrentView(boolean isCurrentView) {
        this.isCurrentView = isCurrentView;
    }

    public interface OrdersAdapterListener {
        void onOrderClicked(Order order);
    }
}
