package com.martinwalls.meatmanager.util.undo.stock;

import android.content.Context;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.util.undo.UndoableAction;

public class AddStockAction extends UndoableAction {
    private StockItem stockItem;

    public AddStockAction(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.deleteStockItem(stockItem.getStockId());
    }

    @Override
    public void showUndoMessage(Context context) {
        showUndoSnackbar(context, context.getString(
                R.string.undo_add_stock_success, stockItem.getProduct().getProductName()));
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        int newId = dbHandler.addStockItem(stockItem);
        stockItem.setStockId(newId);
        return true;
    }

    @Override
    public void showRedoMessage(Context context) {
        showRedoSnackbar(context, context.getString(
                R.string.redo_add_stock_success, stockItem.getProduct().getProductName()));
    }
}
