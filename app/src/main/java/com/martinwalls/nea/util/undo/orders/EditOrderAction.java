package com.martinwalls.nea.util.undo.orders;

import android.content.Context;
import android.widget.Toast;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Order;
import com.martinwalls.nea.util.undo.UndoableAction;

public class EditOrderAction extends UndoableAction {
    private Order orderBefore;
    private Order orderAfter;

    public EditOrderAction(Order orderBefore, Order orderAfter) {
        this.orderBefore = orderBefore;
        this.orderAfter = orderAfter;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateOrder(orderBefore);
    }

    @Override
    public void showUndoMessage(Context context) {
        Toast.makeText(context, R.string.undo_edit_order_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateOrder(orderAfter);
    }

    @Override
    public void showRedoMessage(Context context) {
        Toast.makeText(context, R.string.redo_edit_order_success, Toast.LENGTH_SHORT).show();
    }
}
