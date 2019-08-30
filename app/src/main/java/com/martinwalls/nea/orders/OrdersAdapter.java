package com.martinwalls.nea.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;
import com.martinwalls.nea.models.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<Order> orderList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderDest, dateDivider;
        private RecyclerView recyclerView;

        ViewHolder(View view) {
            super(view);
            orderDest = view.findViewById(R.id.order_dest);
            dateDivider = view.findViewById(R.id.date_divider);
            recyclerView = view.findViewById(R.id.recycler_view);
        }
    }

    OrdersAdapter(List<Order> orderList) {
        this.orderList = orderList;
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
        OrderProductsAdapter adapter = new OrderProductsAdapter(order.getProductList());
        viewHolder.recyclerView.setAdapter(adapter);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder.recyclerView.getContext()));

        if (position > 0 && orderList.get(position - 1).getOrderDate().isBefore(order.getOrderDate())) {
            viewHolder.dateDivider.setText(order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMMM")));
            viewHolder.dateDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
