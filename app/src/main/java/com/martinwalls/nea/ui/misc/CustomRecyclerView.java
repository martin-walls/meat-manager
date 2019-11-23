package com.martinwalls.nea.ui.misc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {

    private View emptyView;

    /**
     * Data observer that updates the empty view when data changes, by calling
     * {@link #updateEmptyView()}.
     */
    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyView();
        }
    };

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(dataObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver);
        }
        super.setAdapter(adapter);
        updateEmptyView();
    }

    /**
     * Sets the View to use as the empty view for this RecyclerView. This is
     * the View shown when there is no data, so the user is not just shown
     * a blank screen.
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        // if adapter already set, make sure the empty view is visible/hidden as required
        if (getAdapter() != null) {
            updateEmptyView();
        }
    }

    /**
     * Updates the state of the empty view. If there is data to be shown, the
     * empty view will be hidden, but if there is no data, the empty view will
     * be shown.
     */
    private void updateEmptyView() {
        if (emptyView != null && getAdapter() != null) {
            boolean showEmptyView = getAdapter().getItemCount() == 0;
            if (showEmptyView) {
                emptyView.setVisibility(VISIBLE);
                setVisibility(GONE);
            } else {
                emptyView.setVisibility(GONE);
                setVisibility(VISIBLE);
            }
        }
    }
}
