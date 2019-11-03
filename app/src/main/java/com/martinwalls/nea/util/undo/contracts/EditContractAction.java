package com.martinwalls.nea.util.undo.contracts;

import android.content.Context;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.util.undo.UndoableAction;

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
