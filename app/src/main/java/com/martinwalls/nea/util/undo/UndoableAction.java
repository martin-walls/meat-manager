package com.martinwalls.nea.util.undo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import com.martinwalls.nea.R;

public abstract class UndoableAction {
    private enum SnackbarMode {
        UNDO,
        REDO
    }

    public abstract boolean undoAction(Context context);

    public abstract void showUndoMessage(Context context);

    public abstract boolean redoAction(Context context);

    public abstract void showRedoMessage(Context context);

    private void showSnackbar(Context context, String text, SnackbarMode mode) {
        try {
            View view = ((Activity) context).getWindow().getDecorView();
            Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
            switch (mode) {
                case UNDO:
                    snackbar.setAction(R.string.action_redo, v -> UndoStack.getInstance().redo(context));
                    break;
                case REDO:
                    snackbar.setAction(R.string.action_undo, v -> UndoStack.getInstance().undo(context));
                    break;
            }
            snackbar.show();
        } catch (ClassCastException e) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackbar(Context context, @StringRes int resId, SnackbarMode mode) {
        showSnackbar(context, context.getString(resId), mode);
    }

    protected final void showUndoSnackbar(Context context, String text) {
        showSnackbar(context, text, SnackbarMode.UNDO);
    }

    protected final void showUndoSnackbar(Context context, @StringRes int resId) {
        showSnackbar(context, resId, SnackbarMode.UNDO);
    }

    protected final void showRedoSnackbar(Context context, String text) {
        showSnackbar(context, text, SnackbarMode.REDO);
    }

    protected final void showRedoSnackbar(Context context, @StringRes int resId) {
        showSnackbar(context, resId, SnackbarMode.REDO);
    }
}
