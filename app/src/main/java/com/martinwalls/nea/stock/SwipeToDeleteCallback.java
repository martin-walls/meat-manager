package com.martinwalls.nea.stock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.martinwalls.nea.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private MeatTypesAdapter meatTypesAdapter;

    private Drawable deleteIcon;
    private final ColorDrawable background;

    public SwipeToDeleteCallback(MeatTypesAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.meatTypesAdapter = adapter;
        deleteIcon = context.getDrawable(R.drawable.ic_delete);
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        background = new ColorDrawable(Color.RED); //todo custom colour resource?
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction) {
        int position = holder.getAdapterPosition();
        meatTypesAdapter.deleteItem(position);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

        if (dX > 0) { // swipe right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth();
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
        } else if (dX < 0) { // swipe left
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        deleteIcon.draw(c);
    }
}
