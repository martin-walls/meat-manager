package com.martinwalls.nea.util.undo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class UndoStack {

    private static UndoStack instance = new UndoStack();

    private final int MAX_SIZE = 100;

    private List<Action> undoStack = new ArrayList<>();
    private List<Action> redoStack = new ArrayList<>();

    private UndoStack() {}

    public static UndoStack getInstance() {
        return instance;
    }

    public void push(Action action) {
        undoStack.add(action);
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

    public Action undo(Context context) {
        if (!canUndo()) {
            return null;
        }
        Action lastAction = undoStack.get(undoStack.size() - 1);
        undoStack.remove(undoStack.size() - 1);
        redoStack.add(lastAction);
        lastAction.undoAction(context);
        return lastAction;
    }

    public Action redo(Context context) {
        if (!canRedo()) {
            return null;
        }
        Action lastAction = redoStack.get(redoStack.size() - 1);
        redoStack.remove(redoStack.size() - 1);
        undoStack.add(lastAction);
        lastAction.redoAction(context);
        return lastAction;
    }
}
