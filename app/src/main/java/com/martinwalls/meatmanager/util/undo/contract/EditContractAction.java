package com.martinwalls.meatmanager.util.undo.contract;

import android.content.Context;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.util.undo.UndoableAction;

public class EditContractAction extends UndoableAction {
    private Contract contractBefore;
    private Contract contractAfter;

    public EditContractAction(Contract contractBefore, Contract contractAfter) {
        this.contractBefore = contractBefore;
        this.contractAfter = contractAfter;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateContract(contractBefore);
    }

    @Override
    public void showUndoMessage(Context context) {
        showUndoSnackbar(context, R.string.undo_edit_contract_success);
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.updateContract(contractAfter);
    }

    @Override
    public void showRedoMessage(Context context) {
        showRedoSnackbar(context, R.string.redo_edit_contract_success);
    }
}
