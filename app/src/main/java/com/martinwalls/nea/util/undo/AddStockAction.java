package com.martinwalls.nea.util.undo;

import android.content.Context;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.StockItem;

public class AddStockAction extends Action {
    private StockItem stockItem;

    public AddStockAction(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public void redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.addStockItem(stockItem);
    }

    @Override
    public void undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.deleteStockItem(stockItem.getStockId());
    }
}
