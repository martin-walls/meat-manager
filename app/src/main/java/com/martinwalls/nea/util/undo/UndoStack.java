package com.martinwalls.nea.util.undo;

import android.content.Context;
import android.widget.Toast;
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

    public boolean canUndo() {
        return undoStack.size() > 0;
    }

    public boolean canRedo() {
        return redoStack.size() > 0;
    }

    public UndoableAction undo(Context context) {
        if (!canUndo()) {
            Toast.makeText(context, R.string.undo_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        UndoableAction lastAction = undoStack.get(undoStack.size() - 1);
        undoStack.remove(undoStack.size() - 1);
        redoStack.add(lastAction);
        lastAction.undoAction(context);
        return lastAction;
    }

    public UndoableAction redo(Context context) {
        if (!canRedo()) {
            Toast.makeText(context, R.string.redo_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        UndoableAction lastAction = redoStack.get(redoStack.size() - 1);
        redoStack.remove(redoStack.size() - 1);
        undoStack.add(lastAction);
        lastAction.redoAction(context);
        return lastAction;
    }
}
