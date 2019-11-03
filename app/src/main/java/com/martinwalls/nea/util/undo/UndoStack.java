package com.martinwalls.nea.util.undo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.nea.R;

import java.util.ArrayList;
import java.util.List;

public class UndoStack {

    private static UndoStack instance = new UndoStack();

    private final int MAX_SIZE = 100;

    private List<UndoableAction> undoStack = new ArrayList<>();
    private List<UndoableAction> redoStack = new ArrayList<>();

    private UndoStack() {}

    public static UndoStack getInstance() {
        return instance;
    }

    public void push(UndoableAction undoableAction) {
        undoStack.add(undoableAction);
        while (undoStack.size() > MAX_SIZE) {
            undoStack.remove(0);
        }
        redoStack.clear();
    }

    private boolean canUndo() {
        return undoStack.size() > 0;
    }

    private boolean canRedo() {
        return redoStack.size() > 0;
    }

    public UndoableAction undo(Context context) {
        if (!canUndo()) {
            showSnackbar(context, R.string.undo_empty);
            return null;
        }
        UndoableAction lastAction = undoStack.get(undoStack.size() - 1);
        undoStack.remove(undoStack.size() - 1);
        redoStack.add(lastAction);
        boolean success = lastAction.undoAction(context);
        if (success) {
            lastAction.showUndoMessage(context);
        }
        return lastAction;
    }

    public UndoableAction redo(Context context) {
        if (!canRedo()) {
            showSnackbar(context, R.string.redo_empty);
            return null;
        }
        UndoableAction lastAction = redoStack.get(redoStack.size() - 1);
        redoStack.remove(redoStack.size() - 1);
        undoStack.add(lastAction);
        boolean success = lastAction.redoAction(context);
        if (success) {
            lastAction.showRedoMessage(context);
        }
        return lastAction;
    }

    private void showSnackbar(Context context, @StringRes int resId) {
        try {
            View view = ((Activity) context).getWindow().getDecorView();
            Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
        } catch (ClassCastException e) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }
}
