package com.martinwalls.meatmanager.util.undo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.meatmanager.R;

import java.util.ArrayList;
import java.util.List;

public class UndoStack {

    private static UndoStack INSTANCE = new UndoStack();

    // the maximum number of actions to store
    private final int MAX_SIZE = 100;

    private List<UndoableAction> undoStack = new ArrayList<>();
    private List<UndoableAction> redoStack = new ArrayList<>();

    /**
     * Shouldn't be instantiated, instead this should be accessed via
     * {@link #getInstance()} as a singleton.
     */
    private UndoStack() {}

    /**
     * Returns the instance of UndoStack.
     */
    public static UndoStack getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an action to the undo stack, so the user can undo it later.
     * If the size of the undo stack is greater than the maximum, actions are
     * removed from the bottom of the stack.
     * Clears the redo stack, as the user should not be able to redo actions
     * once they have performed a new action.
     */
    public void push(UndoableAction undoableAction) {
        undoStack.add(undoableAction);
        while (undoStack.size() > MAX_SIZE) {
            undoStack.remove(0);
        }
        redoStack.clear();
    }

    /**
     * Determines whether there is an action that can be undone.
     */
    private boolean canUndo() {
        return undoStack.size() > 0;
    }

    /**
     * Determines whether there is an action that can be redone.
     */
    private boolean canRedo() {
        return redoStack.size() > 0;
    }

    /**
     * Undoes the last action on the undo stack. Shows an appropriate
     * message to the user about what was undone, if anything. If an action
     * was undone, it is added to the redo stack so the user can redo
     * it if they choose.
     *
     * @return the {@link UndoableAction} that was undone
     */
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

    /**
     * Redoes the last action on the redo stack. Shows an appropriate
     * message to the user about what was redone, if anything. If an action
     * was redone, it is added to the redo stack so the user can undo
     * it if they choose.
     *
     * @return the {@link UndoableAction} that was redone
     */
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

    /**
     * Shows a message to the user in the form of a {@link Snackbar}. If no
     * suitable View can be found to show a Snackbar, a {@link Toast} message
     * is shown instead.
     */
    private void showSnackbar(Context context, @StringRes int resId) {
        try {
            View view = ((Activity) context).getWindow().getDecorView();
            Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
        } catch (ClassCastException e) {
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }
}
