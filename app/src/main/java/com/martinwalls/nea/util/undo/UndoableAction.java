package com.martinwalls.nea.util.undo;

import android.content.Context;

public abstract class UndoableAction {

    public abstract boolean undoAction(Context context);

    public abstract void showUndoMessage(Context context);

    public abstract boolean redoAction(Context context);

    public abstract void showRedoMessage(Context context);
}
