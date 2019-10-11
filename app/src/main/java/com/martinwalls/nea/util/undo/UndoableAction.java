package com.martinwalls.nea.util.undo;

import android.content.Context;

public abstract class UndoableAction {

//    public abstract boolean doAction(Context context);

    public abstract void undoAction(Context context);

    public abstract void redoAction(Context context);
}
