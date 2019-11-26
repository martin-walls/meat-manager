package com.martinwalls.meatmanager.util.undo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.meatmanager.R;

public abstract class UndoableAction {
    private enum SnackbarMode {
        UNDO,
        REDO
    }

    /**
     * Undoes the action. This is effectively the opposite of doing the
     * original action.
     */
    public abstract boolean undoAction(Context context);

    /**
     * Shows a message telling the user what was undone.
     */
    public abstract void showUndoMessage(Context context);

    /**
     * Redoes the action. This is effectively the same as doing the original
     * action.
     */
    public abstract boolean redoAction(Context context);

    /**
     * Shows a message telling the user what was redone.
     */
    public abstract void showRedoMessage(Context context);

    /**
     * Shows a message to the user in the form of a {@link Snackbar}. If no
     * suitable View can be found to show a Snackbar, a {@link Toast} message
     * is shown instead. The Snackbar's action is set to undo / redo the action
     * as appropriate.
     *
     * @param text The text to show in the message.
     */
    private void showSnackbar(Context context, String text, SnackbarMode mode) {
        try {
            View view = ((Activity) context).getWindow().getDecorView();
            Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
            switch (mode) {
                case UNDO:
                    snackbar.setAction(R.string.action_redo, v ->
                            UndoStack.getInstance().redo(context));
                    break;
                case REDO:
                    snackbar.setAction(R.string.action_undo, v ->
                            UndoStack.getInstance().undo(context));
                    break;
            }
            snackbar.show();
        } catch (ClassCastException e) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows a message to the user in the form of a {@link Snackbar}. If no
     * suitable View can be found to show a Snackbar, a {@link Toast} message
     * is shown instead. The Snackbar's action is set to undo / redo the action
     * as appropriate.
     *
     * @param resId Resource id of text to show.
     */
    private void showSnackbar(Context context, @StringRes int resId, SnackbarMode mode) {
        showSnackbar(context, context.getString(resId), mode);
    }

    /**
     * Shows a message telling the user what was undone.
     *
     * @see #showSnackbar(Context, String, SnackbarMode)
     */
    protected final void showUndoSnackbar(Context context, String text) {
        showSnackbar(context, text, SnackbarMode.UNDO);
    }
    /**
     * Shows a message telling the user what was undone.
     *
     * @see #showSnackbar(Context, int, SnackbarMode)
     */
    protected final void showUndoSnackbar(Context context, @StringRes int resId) {
        showSnackbar(context, resId, SnackbarMode.UNDO);
    }

    /**
     * Shows a message telling the user what was redone.
     *
     * @see #showSnackbar(Context, String, SnackbarMode)
     */
    protected final void showRedoSnackbar(Context context, String text) {
        showSnackbar(context, text, SnackbarMode.REDO);
    }

    /**
     * Shows a message telling the user what was redone.
     *
     * @see #showSnackbar(Context, int, SnackbarMode)
     */
    protected final void showRedoSnackbar(Context context, @StringRes int resId) {
        showSnackbar(context, resId, SnackbarMode.REDO);
    }
}
