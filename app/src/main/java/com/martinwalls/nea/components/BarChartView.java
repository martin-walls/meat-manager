package com.martinwalls.nea.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import com.martinwalls.nea.R;
import com.martinwalls.nea.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private Context context;

    private List<BarChartEntry> dataSet = new ArrayList<>();

    private float xMax   = 0;

    private Paint barFillPaint;
    private Paint barLabelPaint;
    private Paint barInnerLabelPaint;
    private Paint reqBarFillPaint;
    private Paint tooltipFillPaint;
    private Paint tooltipTextPaint;
    private Paint prevTooltipFillPaint;
    private Paint prevTooltipTextPaint;
    private Paint reqTooltipFillPaint;
    private Paint reqTooltipTextPaint;
    private Paint prevReqTooltipFillPaint;
    private Paint prevReqTooltipTextPaint;

    private final int barWidthDp = 48;
    private float barWidth;
    private float barSpacing;
    private float barCornerRadius;
    private float textMarginInside;
    private float textMarginOutside;
    private float ttMargin;
    private float ttPadding;

    private int selectedIndex = -1;

    private int ttAlpha = 0;
    private int ttAlphaDelay = 10;
    private int ttAlphaLength = 200;
    private int ttAlphaStep = 255 / (ttAlphaLength / ttAlphaDelay);

    private int prevSelectedIndex = -1;
    private int prevTtAlpha = 255;

    private Rect labelTextBounds = new Rect();
    private Rect ttTextBounds = new Rect();
    private Rect reqTtTextBounds = new Rect();

    private DecimalFormat decimalFormat;

    public BarChartView(Context context) {
        super(context);
        init(context);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int barColor = getColorFromTheme(context, R.attr.dashboardGraphBarColor);
        barFillPaint.setColor(barColor);

        barLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int outerTextColor = getColorFromTheme(context, R.attr.dashboardGraphBarOuterTextColor);
        barLabelPaint.setColor(outerTextColor);
        barLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barLabelPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        barInnerLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int innerTextColor = getColorFromTheme(context, R.attr.dashboardGraphBarInnerTextColor);
        barInnerLabelPaint.setColor(innerTextColor);
        barInnerLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barInnerLabelPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        reqBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int reqBarColor = getColorFromTheme(context, R.attr.dashboardGraphReqBarColor);
        reqBarFillPaint.setColor(reqBarColor);

        tooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int ttColor = getColorFromTheme(context, R.attr.dashboardTooltipColor);
        tooltipFillPaint.setColor(ttColor);

        tooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int ttTextColor = getColorFromTheme(context, R.attr.dashboardTooltipTextColor);
        tooltipTextPaint.setColor(ttTextColor);
        tooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        tooltipTextPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        prevTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        prevTooltipFillPaint.setColor(ttColor);

        prevTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        prevTooltipTextPaint.setColor(ttTextColor);
        prevTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        prevTooltipTextPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        reqTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int reqTtColor = getColorFromTheme(context, R.attr.dashboardReqTooltipColor);
        reqTooltipFillPaint.setColor(reqTtColor);

        reqTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int reqTtTextColor = getColorFromTheme(context, R.attr.dashboardReqTooltipTextColor);
        reqTooltipTextPaint.setColor(reqTtTextColor);
        reqTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        reqTooltipTextPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        prevReqTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        prevReqTooltipFillPaint.setColor(reqTtColor);

        prevReqTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        prevReqTooltipTextPaint.setColor(reqTtTextColor);
        prevReqTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        prevReqTooltipTextPaint.setTextSize(Utils.convertSpToPixelSize(14, context));


        barWidth = Utils.convertDpToPixelSize(barWidthDp, context);
        barSpacing = barWidth * 0.1f;
        barCornerRadius = Utils.convertDpToPixelSize(8, context);

        textMarginInside = Utils.convertDpToPixelSize(12, context);
        textMarginOutside = Utils.convertDpToPixelSize(8, context);

        ttMargin = Utils.convertDpToPixelSize(8, context);
        ttPadding = Utils.convertDpToPixelSize(8, context);

        decimalFormat = new DecimalFormat("#.###");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Utils.convertDpToPixelSize(barWidthDp * dataSet.size() + getPaddingTop(), context);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        for (int i = 0; i < dataSet.size(); i++) {
            BarChartEntry entry = dataSet.get(i);

            float barLeft = 0;
            float barTop = getPaddingTop() + barWidth * i + (barSpacing / 2f);
            float barLength = entry.getAmount() / xMax * getWidth();
            float barBottom = getPaddingTop() + barWidth * (i + 1) - (barSpacing / 2f);

            float reqBarLength = 0;
            boolean isReqBarShown = false;
            if (entry.getAmountRequired() > entry.getAmount()) {
                reqBarLength = entry.getAmountRequired() / xMax * getWidth();
                c.drawRoundRect(barLeft, barTop, reqBarLength, barBottom,
                        barCornerRadius, barCornerRadius, reqBarFillPaint);
                isReqBarShown = true;
            }

            c.drawRoundRect(barLeft, barTop, barLength, barBottom, barCornerRadius, barCornerRadius, barFillPaint);

            String label = entry.getName().toUpperCase();

            barLabelPaint.getTextBounds(label, 0, label.length(), labelTextBounds);

            float labelX;
            boolean isTextInside;
            if (labelTextBounds.width() + textMarginInside * 2f > barLength) {
                if (reqBarLength > barLength) {
                    labelX = barLeft + reqBarLength + textMarginOutside;
                } else {
                    labelX = barLeft + barLength + textMarginOutside;
                }
                isTextInside = false;
            } else {
                labelX = barLeft + textMarginInside;
                isTextInside = true;
            }

            float labelY = (barTop + barBottom + labelTextBounds.height()) / 2f;

            c.drawText(label, labelX, labelY, isTextInside ? barInnerLabelPaint : barLabelPaint);

            // show tooltips
            if (i == selectedIndex || i == prevSelectedIndex) {
                String ttText = context.getString(R.string.amount_kg, decimalFormat.format(entry.getAmount()));
                tooltipTextPaint.getTextBounds(ttText, 0, ttText.length(), ttTextBounds);

                float ttLeft;
                boolean isTooltipShownInside = false;
                if ((isTextInside && ttTextBounds.width() + (ttPadding * 2f) + ttMargin
                        < barLength - labelTextBounds.width() - textMarginInside * 2f)
                        || (!isTextInside && ttTextBounds.width() + (ttPadding + ttMargin) * 2f < barLength)) {
                    ttLeft = barLength - ttTextBounds.width() - ttMargin - ttPadding * 2f;
                    isTooltipShownInside = true;
                } else if (isTextInside) {
                    ttLeft = (isReqBarShown ? reqBarLength : barLength) + ttMargin;
                } else {
                    ttLeft = (isReqBarShown ? reqBarLength : barLength) + labelTextBounds.width()
                            + textMarginOutside * 2f + ttMargin;
                }

                float ttTop = (barTop + barBottom - ttTextBounds.height()) / 2f - ttPadding;
                float ttRight = ttLeft + ttTextBounds.width() + ttPadding * 2f;
                float ttBottom = (barTop + barBottom + ttTextBounds.height()) / 2f + ttPadding;

                tooltipFillPaint.setAlpha(ttAlpha);
                prevTooltipFillPaint.setAlpha(prevTtAlpha);

                float ttCornerRadius = (ttBottom - ttTop) / 2f;

                c.drawRoundRect(ttLeft, ttTop, ttRight, ttBottom, ttCornerRadius, ttCornerRadius,
                        i == selectedIndex ? tooltipFillPaint : prevTooltipFillPaint);

                float ttTextX = ttLeft + ttPadding;
                float ttTextY = labelY;

                tooltipTextPaint.setAlpha(ttAlpha);
                prevTooltipTextPaint.setAlpha(prevTtAlpha);
                c.drawText(ttText, ttTextX, ttTextY, i == selectedIndex ? tooltipTextPaint : prevTooltipTextPaint);


                // required amount tooltip
                if (isReqBarShown) {
                    String reqTtText = context.getString(R.string.amount_kg,
                            decimalFormat.format(entry.getAmountRequired()));
                    reqTooltipTextPaint.getTextBounds(reqTtText, 0, reqTtText.length(), reqTtTextBounds);

                    float reqTtLeft;
                    if (reqTtTextBounds.width() + (ttPadding + ttMargin) * 2f < reqBarLength - barLength) {
                        reqTtLeft = reqBarLength - reqTtTextBounds.width() - ttMargin - ttPadding * 2f;
                    } else if (isTooltipShownInside) {
                        reqTtLeft = reqBarLength + ttMargin;
                    } else {
                        reqTtLeft = ttRight + ttMargin;
                    }

                    float reqTtRight = reqTtLeft + reqTtTextBounds.width() + ttPadding * 2f;

                    reqTooltipFillPaint.setAlpha(ttAlpha);
                    prevReqTooltipFillPaint.setAlpha(prevTtAlpha);

                    c.drawRoundRect(reqTtLeft, ttTop, reqTtRight, ttBottom, ttCornerRadius, ttCornerRadius,
                            i == selectedIndex ? reqTooltipFillPaint : prevReqTooltipFillPaint);

                    float reqTtTextX = reqTtLeft + ttPadding;

                    reqTooltipTextPaint.setAlpha(ttAlpha);
                    prevReqTooltipTextPaint.setAlpha(prevTtAlpha);
                    c.drawText(reqTtText, reqTtTextX, ttTextY,
                            i == selectedIndex ? reqTooltipTextPaint : prevReqTooltipTextPaint);
                }


                // update alpha values for fade in/out
                if (ttAlpha < 255 || prevTtAlpha > 0) {
                    postInvalidateDelayed(ttAlphaDelay);
                }

                if (ttAlpha < 255) {
                    ttAlpha += ttAlphaStep;
                    if (ttAlpha > 255) {
                        ttAlpha = 255;
                    }
                }
                if (prevTtAlpha > 0) {
                    prevTtAlpha -= ttAlphaStep;
                    if (prevTtAlpha < 0) {
                        prevTtAlpha = 0;
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float y = event.getY();
            prevSelectedIndex = selectedIndex;
            selectedIndex = (int) y / (int) barWidth;
            ttAlpha = 0;
            prevTtAlpha = 255;
            invalidate();
        }
        return true;
    }

    public void setData(List<BarChartEntry> newDataSet) {
        xMax = newDataSet.get(0).getAmount();

        for (BarChartEntry entry : newDataSet) {
            if (entry.getAmount() > xMax) {
                xMax = entry.getAmount();
            }
            if (entry.getAmountRequired() > xMax) {
                xMax = entry.getAmountRequired();
            }
        }

        dataSet.clear();
        dataSet.addAll(newDataSet);
        invalidate();
    }

    private int getColorFromTheme(Context context, @AttrRes int resId) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, outValue, true);
        return outValue.data;
    }
}
