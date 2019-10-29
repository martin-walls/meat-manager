package com.martinwalls.nea.util.undo;

import android.content.Context;
import android.widget.Toast;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.StockItem;

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
        Toast.makeText(context,
                context.getString(R.string.undo_add_stock_success, stockItem.getProduct().getProductName()),
                Toast.LENGTH_SHORT).show();
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
        Toast.makeText(context,
                context.getString(R.string.redo_add_stock_success, stockItem.getProduct().getProductName()),
                Toast.LENGTH_SHORT).show();
    }
}
