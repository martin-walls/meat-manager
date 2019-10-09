package com.martinwalls.nea.util.undo;

import android.content.Context;

public abstract class Action {

    public abstract void redoAction(Context context);

    public abstract void undoAction(Context context);
}
