package com.martinwalls.meatmanager.util.undo.stock;

import android.content.Context;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.util.undo.UndoableAction;

public class EditStockAction extends UndoableAction {
    private StockItem stockBefore;
    private StockItem stockAfter;

    public EditStockAction(StockItem stockBefore, StockItem stockAfter) {
        this.stockBefore = stockBefore;
        this.stockAfter = stockAfter;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateStockItem(stockBefore);
    }

    @Override
    public void showUndoMessage(Context context) {
        showUndoSnackbar(context, R.string.undo_edit_stock_success);
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateStockItem(stockAfter);
    }

    @Override
    public void showRedoMessage(Context context) {
        showRedoSnackbar(context, R.string.redo_edit_stock_success);
    }
}
