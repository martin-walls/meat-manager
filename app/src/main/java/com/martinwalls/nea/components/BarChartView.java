package com.martinwalls.nea.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.martinwalls.nea.R;
import com.martinwalls.nea.Utils;
import com.martinwalls.nea.models.StockItem;

import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private List<StockItem> dataSet = new ArrayList<>();

    private float maxValue = 0;

    private Paint barFillPaint;
    private Paint barLabelPaint;

    private int barWidthDp = 48;

    Rect textBounds = new Rect();

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barFillPaint = new Paint();
        int barColour = ContextCompat.getColor(getContext(), R.color.dashboard_graph_bar);
        barFillPaint.setColor(barColour);

        barLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int textColour = ContextCompat.getColor(getContext(), R.color.text_dark);
        barLabelPaint.setColor(textColour);
        barLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barLabelPaint.setTextSize(Utils.convertSpToPixelSize(14, getContext()));
    }

    public void setData(List<StockItem> newDataSet) {
        maxValue = (float) newDataSet.get(0).getMass();

        for (StockItem stockItem : newDataSet) {
            if (stockItem.getMass() > maxValue) {
                maxValue = (float) stockItem.getMass();
            }
        }

        dataSet.clear();
        dataSet.addAll(newDataSet);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Utils.convertDpToPixelSize(barWidthDp * dataSet.size() + getPaddingTop(), getContext());
        setMeasuredDimension(width, height);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        float barWidth = Utils.convertDpToPixelSize(barWidthDp, getContext());
        float barSpacing = barWidth * 0.1f;
        float cornerRadius = Utils.convertDpToPixelSize(8, getContext());

        float textMarginInside = Utils.convertDpToPixelSize(12, getContext());
        float textMarginOutside = Utils.convertDpToPixelSize(8, getContext());

        for (int i = 0; i < dataSet.size(); i++) {
            StockItem currentItem = dataSet.get(i);

            float barLeft = 0;
            float barTop = getPaddingTop() + barWidth * i + (barSpacing / 2f);
            float barLength = (float) currentItem.getMass() / maxValue * getWidth();
            float barBottom = getPaddingTop() + barWidth * (i + 1) - (barSpacing / 2f);

            c.drawRoundRect(barLeft, barTop, barLength, barBottom, cornerRadius, cornerRadius, barFillPaint);

            String label = currentItem.getProduct().getProductName().toUpperCase();

//            if (label.length() > 12) {
//                label = label.substring(0, 13) + "…";
//            }

            barLabelPaint.getTextBounds(label, 0, label.length(), textBounds);

//            float rectX = getWidth();
//            float rectX = cornerRadius * 2f;
            float rectX;

            if (textBounds.width() > barLength + textMarginInside * 2f) {
                rectX = barLeft + barLength + textMarginOutside;
            } else {
                rectX = barLeft + textMarginInside ;
            }

            float rectY = (barTop + barBottom + textBounds.height()) / 2f;

            c.drawText(label, rectX, rectY, barLabelPaint);
        }
    }
}
