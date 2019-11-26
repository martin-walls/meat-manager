package com.martinwalls.meatmanager.util.undo.contracts;

import android.content.Context;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.util.undo.UndoableAction;

public class DeleteContractAction extends UndoableAction {

    private Contract contract;

    public DeleteContractAction(Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        int newId = dbHandler.addContract(contract);
        contract.setContractId(newId);
        return true;
    }

    @Override
    public void showUndoMessage(Context context) {
        showUndoSnackbar(context, R.string.undo_delete_contract_success);
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.deleteContract(contract.getContractId());
    }

    @Override
    public void showRedoMessage(Context context) {
        showRedoSnackbar(context, R.string.redo_delete_contract_success);
    }
}
