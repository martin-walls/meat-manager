package com.martinwalls.nea.util.undo.orders;

import android.content.Context;
import android.widget.Toast;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Order;
import com.martinwalls.nea.util.undo.UndoableAction;

public class DeleteOrderAction extends UndoableAction {

    private Order order;

    public DeleteOrderAction(Order order) {
        this.order = order;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        int newId = dbHandler.addOrder(order);
        order.setOrderId(newId);
        return true;
    }

    @Override
    public void showUndoMessage(Context context) {
        Toast.makeText(context, R.string.undo_delete_order_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.deleteOrder(order.getOrderId());
    }

    @Override
    public void showRedoMessage(Context context) {
        Toast.makeText(context, R.string.redo_delete_order_success, Toast.LENGTH_SHORT).show();
    }
}
