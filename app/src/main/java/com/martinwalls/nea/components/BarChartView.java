package com.martinwalls.nea.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.martinwalls.nea.R;
import com.martinwalls.nea.Utils;

import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private List<BarChartEntry> dataSet = new ArrayList<>();

    private float xMax   = 0;

    private Paint barFillPaint;
    private Paint barLabelPaint;
    private Paint reqBarFillPaint;
    private Paint tooltipFillPaint;
    private Paint tooltipTextPaint;

    private int barWidthDp = 48;

    private int selectedIndex = -1;

    private int ttAlpha = 0;
    private int ttAlphaDelay = 50;
    private int ttAlphaLength = 500;

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

        reqBarFillPaint = new Paint();
        int reqBarColour = ContextCompat.getColor(getContext(), R.color.dashboard_graph_bar_req);
        reqBarFillPaint.setColor(reqBarColour);

        tooltipFillPaint = new Paint();
        int ttColour = ContextCompat.getColor(getContext(), R.color.dashboard_tooltip);
        tooltipFillPaint.setColor(ttColour);

        tooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tooltipTextPaint.setColor(textColour);
        tooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        tooltipTextPaint.setTextSize(Utils.convertSpToPixelSize(14, getContext()));
    }

    public void setData(List<BarChartEntry> newDataSet) {
        xMax = newDataSet.get(0).getAmount();

        for (BarChartEntry entry : newDataSet) {
            if (entry.getAmount() > xMax) {
                xMax = entry.getAmount();
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

        //todo move to init
        float barWidth = Utils.convertDpToPixelSize(barWidthDp, getContext());
        float barSpacing = barWidth * 0.1f;
        float cornerRadius = Utils.convertDpToPixelSize(8, getContext());

        float textMarginInside = Utils.convertDpToPixelSize(12, getContext());
        float textMarginOutside = Utils.convertDpToPixelSize(8, getContext());

        float ttMargin = Utils.convertDpToPixelSize(8, getContext());
        float ttPadding = Utils.convertDpToPixelSize(8, getContext());
        float ttCornerRadius = Utils.convertDpToPixelSize(8, getContext());

        for (int i = 0; i < dataSet.size(); i++) {
            BarChartEntry entry = dataSet.get(i);

            float barLeft = 0;
            float barTop = getPaddingTop() + barWidth * i + (barSpacing / 2f);
            float barLength = entry.getAmount() / xMax * getWidth();
            float barBottom = getPaddingTop() + barWidth * (i + 1) - (barSpacing / 2f);

            float reqBarLength = 0;
            if (entry.getAmountRequired() > entry.getAmount()) {
                reqBarLength = entry.getAmountRequired() / xMax * getWidth();
                c.drawRoundRect(barLeft, barTop, reqBarLength, barBottom, cornerRadius, cornerRadius, reqBarFillPaint);
            }


            c.drawRoundRect(barLeft, barTop, barLength, barBottom, cornerRadius, cornerRadius, barFillPaint);

            String label = entry.getName().toUpperCase();

//            if (label.length() > 12) {
//                label = label.substring(0, 13) + "…";
//            }

            barLabelPaint.getTextBounds(label, 0, label.length(), textBounds);

            float labelX;
            if (textBounds.width() + textMarginInside * 2f > barLength) {
                if (reqBarLength > barLength) {
                    labelX = barLeft + reqBarLength + textMarginOutside;
                } else {
                    labelX = barLeft + barLength + textMarginOutside;
                }
            } else {
                labelX = barLeft + textMarginInside ;
            }

            float labelY = (barTop + barBottom + textBounds.height()) / 2f;

            c.drawText(label, labelX, labelY, barLabelPaint);

            if (i == selectedIndex) {
                String ttText = String.valueOf(entry.getAmount());
                tooltipTextPaint.getTextBounds( ttText, 0, ttText.length(), textBounds);

                float ttLeft = barLength - textBounds.width() - ttMargin - ttPadding * 2f;
                float ttTop = (barTop + barBottom - textBounds.height()) / 2f - ttPadding;
                float ttRight = barLength - ttMargin;
                float ttBottom = (barTop + barBottom + textBounds.height()) / 2f + ttPadding;

                tooltipFillPaint.setAlpha(ttAlpha);
                c.drawRoundRect(ttLeft, ttTop, ttRight, ttBottom, ttCornerRadius, ttCornerRadius, tooltipFillPaint);

                float ttTextX = barLength - textBounds.width() - ttMargin - ttPadding;
                float ttTextY = labelY;

//                AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
//                fadeIn.setDuration(500);

                tooltipTextPaint.setAlpha(ttAlpha);
                c.drawText(ttText, ttTextX, ttTextY, tooltipTextPaint);

                if (ttAlpha < 255) {
                    ttAlpha += 255 / (ttAlphaLength / ttAlphaDelay);
                    if (ttAlpha > 255) {
                        ttAlpha = 255;
                    }
                    postInvalidateDelayed(ttAlphaDelay);
                }
            }
        }
    }

    GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }
    };

    GestureDetector detector = new GestureDetector(getContext(), listener);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = detector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float y = event.getY();
                selectedIndex = (int) y / Utils.convertDpToPixelSize(barWidthDp, getContext());
                ttAlpha = 0;
                invalidate();
                result = true;
            }
        }
        return result;
    }
}
