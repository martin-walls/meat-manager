package com.martinwalls.nea.util.undo;

import android.content.Context;
import android.widget.Toast;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.StockItem;

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
        Toast.makeText(context, R.string.undo_edit_stock_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateStockItem(stockAfter);
    }

    @Override
    public void showRedoMessage(Context context) {
        Toast.makeText(context, R.string.redo_edit_stock_success, Toast.LENGTH_SHORT).show();
    }
}
