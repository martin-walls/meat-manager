package com.martinwalls.nea.util.undo.contracts;

import android.content.Context;
import android.widget.Toast;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.util.undo.UndoableAction;

public class AddContractAction extends UndoableAction {
    private Contract contract;

    public AddContractAction(Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean undoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        return dbHandler.deleteContract(contract.getContractId());
    }

    @Override
    public void showUndoMessage(Context context) {
        Toast.makeText(context, R.string.undo_add_contract_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean redoAction(Context context) {
        DBHandler dbHandler = new DBHandler(context);
        int newId = dbHandler.addContract(contract);
        contract.setContractId(newId);
        return true;
    }

    @Override
    public void showRedoMessage(Context context) {
        Toast.makeText(context, R.string.redo_add_contract_success, Toast.LENGTH_SHORT).show();
    }
}
