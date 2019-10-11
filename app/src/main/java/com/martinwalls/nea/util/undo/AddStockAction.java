package com.martinwalls.nea.util.undo;

import android.content.Context;
import android.widget.Toast;
import com.martinwalls.nea.R;
import com.martinwalls.nea.db.DBHandler;
import com.martinwalls.nea.models.StockItem;

public class AddStockAction extends UndoableAction {
    private StockItem stockItem;

    public AddStockAction(StockItem stockItem) {
        this.stockItem = stockItem;
    }

//    @Override
//    public boolean doAction(Context context) {
//        DBHandler dbHandler = new DBHandler(context);
//        int newRowId = dbHandler.addStockItem(stockItem);
//        stockItem.setStockId(newRowId);
//        return newRowId != -1;
//    }

    @Override
    public void undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.deleteStockItem(stockItem.getStockId());
        Toast.makeText(context,
                context.getString(R.string.undo_add_stock_success, stockItem.getProduct().getProductName()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        dbHandler.addStockItem(stockItem);
        Toast.makeText(context,
                context.getString(R.string.redo_add_stock_success, stockItem.getProduct().getProductName()),
                Toast.LENGTH_SHORT).show();
    }
}
