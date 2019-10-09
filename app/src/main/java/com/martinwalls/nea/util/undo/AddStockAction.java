package com.martinwalls.nea.util.undo;

import com.martinwalls.nea.models.StockItem;

public class AddStockAction extends Action {
    private StockItem stockItem;

    public AddStockAction(StockItem stockItem) {
        this.stockItem = stockItem;
    }
}
