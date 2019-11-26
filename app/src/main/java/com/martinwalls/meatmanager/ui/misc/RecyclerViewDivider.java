package com.martinwalls.meatmanager.ui.misc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    private Drawable divider;

    public RecyclerViewDivider(Context context, int drawableId) {
        divider = context.getDrawable(drawableId);
    }

    /**
     * Draws the divider at the bottom of each RecyclerView item. The
     * divider takes its intrinsic height, that is the height defined in its
     * drawable resource.
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
