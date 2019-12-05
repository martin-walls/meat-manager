package com.martinwalls.meatmanager.ui.contracts.detail;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.ProductQuantity;
import com.martinwalls.meatmanager.data.models.RelatedStock;
import com.martinwalls.meatmanager.data.models.StockItem;
import com.martinwalls.meatmanager.util.SortUtils;
import com.martinwalls.meatmanager.util.undo.UndoStack;
import com.martinwalls.meatmanager.util.undo.contract.DeleteContractAction;

import java.util.ArrayList;
import java.util.List;

public class ContractDetailViewModel extends AndroidViewModel {

    private final DBHandler dbHandler;

    private MutableLiveData<Contract> contract;
    private MutableLiveData<List<RelatedStock>> relatedStock;

    private final int contractId;

    public ContractDetailViewModel(Application application, int contractId) {
        super(application);

        this.contractId = contractId;

        dbHandler = new DBHandler(application);
    }

    public LiveData<Contract> getContract() {
        if (contract == null) {
            contract = new MutableLiveData<>();
            loadContract();
        }
        return contract;
    }

    public void loadContract() {
        contract.setValue(dbHandler.getContract(contractId));
    }

    public MutableLiveData<List<RelatedStock>> getRelatedStock() {
        if (relatedStock == null) {
            relatedStock = new MutableLiveData<>();
            loadRelatedStock();
        }
        return relatedStock;
    }

    public void loadRelatedStock() {
        // can't load related stock if contract not yet loaded
        if (contract.getValue() == null) loadContract();

        List<StockItem> stockForContract = SortUtils.mergeSort(
                dbHandler.getAllStockForContract(contractId),
                StockItem.comparatorLocation());

        List<ProductQuantity> productList = contract.getValue().getProductList();

        relatedStock.setValue(getRelatedStockFromStock(stockForContract, productList));
    }

    private List<RelatedStock> getRelatedStockFromStock(List<StockItem> stockForContract,
                                                        List<ProductQuantity> productList) {
        // get list of related stock by product, with child stock items for each location
        List<RelatedStock> relatedStockList = new ArrayList<>();
        int thisProductId;
        int lastProductId = -1;
        for (StockItem stockItem : stockForContract) {
            thisProductId = stockItem.getProduct().getProductId();
            if (relatedStockList.size() == 0 || thisProductId != lastProductId) {
                RelatedStock relatedStock = new RelatedStock();
                relatedStock.setProduct(stockItem.getProduct());
                relatedStock.addStockItem(stockItem);
                relatedStockList.add(relatedStock);
            } else {
                relatedStockList.get(relatedStockList.size() - 1).addStockItem(stockItem);
            }
            lastProductId = thisProductId;
        }

        // show items for products that have no stock
        for (ProductQuantity productQuantity : productList) {
            boolean hasRelatedStock = false;
            for (RelatedStock relatedStock : relatedStockList) {
                if (relatedStock.getProduct().getProductId()
                        == productQuantity.getProduct().getProductId()) {
                    hasRelatedStock = true;
                    break;
                }
            }
            if (!hasRelatedStock) {
                relatedStockList.add(new RelatedStock(productQuantity.getProduct()));
            }
        }

        return relatedStockList;
    }

    public boolean deleteContract() {
        boolean success = dbHandler.deleteContract(contractId);
        if (success) {
            UndoStack.getInstance().push(new DeleteContractAction(contract.getValue()));
        }
        return success;
    }
}
