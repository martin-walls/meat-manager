package com.martinwalls.meatmanager.ui.common.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.meatmanager.ui.common.adapter.BaseAdapter;
import com.martinwalls.meatmanager.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    /**
     * The color to show in the 'swiped' area.
     */
    private final ColorDrawable background;

    /**
     * The icon to draw in the 'swiped' area.
     */
    private Drawable deleteIcon;

    /**
     * The adapter attached to the RecyclerView. This handles deleting the
     * item when a swipe gesture is performed.
     */
    private BaseAdapter adapter;

    public SwipeToDeleteCallback(BaseAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        deleteIcon = context.getDrawable(R.drawable.ic_delete);
        // set icon white
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        background = new ColorDrawable(context.getColor(R.color.delete));
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction) {
        int position = holder.getAdapterPosition();
        adapter.deleteItem(position);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * When the item is swiped to the left or right, draws a solid colour
     * background and a delete icon in the area 'behind' the item that is now
     * revealed. The background colour is defined by the {@code ColorDrawable}
     * {@link #background}, and the icon is defined by {@link #deleteIcon}.
     */
    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop()
                + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

        if (dX > 0) { // swipe right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth();
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX), itemView.getBottom());
        } else if (dX < 0) { // swipe left
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
        } else {
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        deleteIcon.draw(c);
    }
}
