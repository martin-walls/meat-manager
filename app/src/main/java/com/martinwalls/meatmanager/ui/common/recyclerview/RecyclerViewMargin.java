package com.martinwalls.meatmanager.ui.common.recyclerview;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewMargin extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private final int direction;

    private final int margin;
    private int marginStart;
    private int marginEnd;

    public RecyclerViewMargin(int margin, int direction) {
        this(margin, margin, margin, direction);
    }

    public RecyclerViewMargin(int margin, int marginStart, int marginEnd,
                              int direction) {
        this.margin = margin;
        this.marginStart = marginStart;
        this.marginEnd = marginEnd;
        this.direction = direction;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (direction == HORIZONTAL) {
            if (position == 0) {
                outRect.left = marginStart;
            } else {
                outRect.left = margin;
            }
            if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.right = marginEnd;
            }
        } else {
            if (position == 0) {
                outRect.top = marginStart;
            } else {
                outRect.top = margin;
            }
            if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = marginEnd;
            }
        }
    }
}
