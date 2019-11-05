package com.martinwalls.nea.ui.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.martinwalls.nea.R;
import com.martinwalls.nea.util.MassUnit;
import com.martinwalls.nea.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private final String DECIMAL_FORMAT_PATTERN = "#.##";

    private Context context;

    private List<BarChartEntry> dataSet = new ArrayList<>();

    private float xMax = 0;

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

        int barColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphBarColor);
        int outerTextColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphBarOuterTextColor);
        int innerTextColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphBarInnerTextColor);
        int reqBarColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphReqBarColor);
        int ttColor = Utils.getColorFromTheme(context, R.attr.dashboardTooltipColor);
        int ttTextColor = Utils.getColorFromTheme(context, R.attr.dashboardTooltipTextColor);
        int reqTtColor = Utils.getColorFromTheme(context, R.attr.dashboardReqTooltipColor);
        int reqTtTextColor = Utils.getColorFromTheme(context, R.attr.dashboardReqTooltipTextColor);


        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barFillPaint.setColor(barColor);

        barLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barLabelPaint.setColor(outerTextColor);
        barLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barLabelPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        barInnerLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barInnerLabelPaint.setColor(innerTextColor);
        barInnerLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barInnerLabelPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        reqBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        reqBarFillPaint.setColor(reqBarColor);

        tooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tooltipFillPaint.setColor(ttColor);

        tooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
        reqTooltipFillPaint.setColor(reqTtColor);

        reqTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

        decimalFormat = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Utils.convertDpToPixelSize(barWidthDp * dataSet.size() + getPaddingTop(), context);
        setMeasuredDimension(width, height);
    }

    //todo add location label to each bar, similar to in stock screen
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        for (int pos = 0; pos < dataSet.size(); pos++) {
            BarChartEntry entry = dataSet.get(pos);

            //todo show green req bar if there is more stock than needed

            // draw bar for amount of stock required
            drawReqBar(c, entry, pos);

            // draw bar for amount of stock held
            drawAmountBar(c, entry, pos);

            // draw bar label
            drawLabelText(c, entry, pos);

            // draw tooltips
            if (pos == selectedIndex) {
                drawTooltips(c, entry, pos);
                updateTooltipAlpha();
            } else if (pos == prevSelectedIndex) {
                drawPreviousTooltips(c, entry, pos);
                updateTooltipAlpha();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float y = event.getY();
            // get which bar was selected
            prevSelectedIndex = selectedIndex;
            selectedIndex = (int) y / (int) barWidth;

            // reset tooltip alpha values
            ttAlpha = 0;
            prevTtAlpha = 255;

            // redraw graph
            invalidate();
        }
        return true;
    }

    public void setData(List<BarChartEntry> newDataSet) {
        if (newDataSet.size() == 0) {
            return;
        }

        // find max value of data
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

    private boolean isReqBarShown(BarChartEntry data) {
        return data.getAmountRequired() > data.getAmount();
    }

    // bar drawing methods
    private float getBarTop(int pos) {
        return getPaddingTop() + barWidth * pos + (barSpacing / 2f);
    }

    private float getBarBottom(int pos) {
        return getPaddingTop() + barWidth * (pos + 1) - (barSpacing / 2f);
    }

    private float getBarLength(float value) {
        return value / xMax * getWidth();
    }

    private void drawBar(Canvas c, float left, float top, float right, float bottom, Paint paint) {
        c.drawRoundRect(left, top, right, bottom, barCornerRadius, barCornerRadius, paint);
    }

    private void drawReqBar(Canvas c, BarChartEntry data, int pos) {
        if (data.getAmountRequired() > data.getAmount()) {
            float length = getBarLength(data.getAmountRequired());
            drawBar(c, 0, getBarTop(pos), length, getBarBottom(pos), reqBarFillPaint);
        }
    }

    private void drawAmountBar(Canvas c, BarChartEntry data, int pos) {
        float length = getBarLength(data.getAmount());
        drawBar(c, 0, getBarTop(pos), length, getBarBottom(pos), barFillPaint);
    }

    // label drawing methods
    private float getLabelTextWidth(boolean isInside) {
        if (isInside) {
            return labelTextBounds.width() + textMarginInside * 2f;
        } else {
            return labelTextBounds.width() + textMarginOutside * 2f;
        }
    }

    // different positions the label can be in
    private enum LabelPos {
        INSIDE_NORMAL,
        INSIDE_REQ,
        OUTSIDE_REQ,
        OUTSIDE_NO_REQ
    }

    private LabelPos getLabelPos(BarChartEntry data) {
        String label = data.getName().toUpperCase();
        barLabelPaint.getTextBounds(label, 0, label.length(), labelTextBounds);

        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        // if text doesn't fit inside, show it outside
        if (getLabelTextWidth(true) > barLength) {
            // if required bar shown
            if (data.getAmountRequired() > data.getAmount()) {
                // if text doesn't fit outside required bar
                if (getLabelTextWidth(false) > getWidth() - reqBarLength) {
                    return LabelPos.INSIDE_REQ;
                } else /* if text fits outside required bar */ {
                    return LabelPos.OUTSIDE_REQ;
                }
            } else /* if required bar not shown */ {
                return LabelPos.OUTSIDE_NO_REQ;
            }
        } else /* if text fits inside */ {
            return LabelPos.INSIDE_NORMAL;
        }
    }

    private float getLabelX(BarChartEntry data) {
        String label = data.getName().toUpperCase();
        barLabelPaint.getTextBounds(label, 0, label.length(), labelTextBounds);

        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        switch (getLabelPos(data)) {
            case INSIDE_NORMAL:
                return textMarginInside;
            case INSIDE_REQ:
            case OUTSIDE_NO_REQ:
                return barLength + textMarginOutside;
            case OUTSIDE_REQ:
                return reqBarLength + textMarginOutside;
        }
        return 0;
    }

    private float getLabelY(int pos) {
        return (getBarTop(pos) + getBarBottom(pos) + labelTextBounds.height()) / 2f;
    }

    private void drawLabelText(Canvas c, BarChartEntry data, int pos) {
        String label = data.getName().toUpperCase();

        float labelX = getLabelX(data);

        // centre label vertically in bar
        float labelY = getLabelY(pos);

        // if inside normal bar, use inside fill paint
        if (getLabelPos(data) == LabelPos.INSIDE_NORMAL) {
            c.drawText(label, labelX, labelY, barInnerLabelPaint);
        } else {
            c.drawText(label, labelX, labelY, barLabelPaint);
        }
    }

    // tooltip drawing methods
    private float getTooltipWidth() {
        return ttTextBounds.width() + ttPadding * 2f;
    }

    private float getTooltipWidthWithMargin() {
        return getTooltipWidth() + ttMargin * 2f;
    }

    private float getReqTooltipWidth() {
        return reqTtTextBounds.width() + ttPadding * 2f;
    }

    private float getReqTooltipWidthWithMargin() {
        return getReqTooltipWidth() + ttMargin * 2f;
    }

    // different positions the tooltip can be in
    private enum TooltipPos {
        INSIDE_NORMAL,
        INSIDE_REQ,
        OUTSIDE_REQ_LABEL_IN,
        OUTSIDE_REQ_LABEL_OUT,
        OUTSIDE_NO_REQ_LABEL_IN,
        OUTSIDE_NO_REQ_LABEL_OUT
    }

    private TooltipPos getTooltipPos(BarChartEntry data, LabelPos labelPos) {
        if (( // enough space inside amount bar for tooltip
                labelPos == LabelPos.INSIDE_NORMAL
                        && getTooltipWidthWithMargin() < getBarLength(data.getAmount()) - getLabelTextWidth(true))
                || (labelPos != LabelPos.INSIDE_NORMAL
                        && getTooltipWidthWithMargin() < getBarLength(data.getAmount()))) {
            return TooltipPos.INSIDE_NORMAL;
        } else if ( // tooltip can't fit outside req bar (if shown)
                isReqBarShown(data)
                && getTooltipWidthWithMargin() > getWidth() - getBarLength(data.getAmountRequired())) {
            return TooltipPos.INSIDE_REQ;
        } else if (// not enough space inside amount bar for tooltip
                // and not shown inside required bar
                // and bar label is shown inside
                labelPos == LabelPos.INSIDE_NORMAL) {
            if (isReqBarShown(data)) {
                return TooltipPos.OUTSIDE_REQ_LABEL_IN;
            } else {
                return TooltipPos.OUTSIDE_NO_REQ_LABEL_IN;
            }
        } else { // not shown inside amount or required bars, and bar label is outside
            if (isReqBarShown(data)) {
                return TooltipPos.OUTSIDE_REQ_LABEL_OUT;
            } else {
                return TooltipPos.OUTSIDE_NO_REQ_LABEL_OUT;
            }
        }
    }

    private float getTooltipLeft(BarChartEntry data, LabelPos labelPos) {
        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        switch (getTooltipPos(data, labelPos)) {
            case INSIDE_NORMAL:
                return barLength - getTooltipWidth() - ttMargin;
            case INSIDE_REQ:
                return reqBarLength - (getReqTooltipWidth() + ttMargin) - (getTooltipWidth() + ttMargin);
            case OUTSIDE_REQ_LABEL_IN:
                return reqBarLength + ttMargin;
            case OUTSIDE_NO_REQ_LABEL_IN:
                return barLength + ttMargin;
            case OUTSIDE_REQ_LABEL_OUT:
                return reqBarLength + getLabelTextWidth(false) + ttMargin;
            case OUTSIDE_NO_REQ_LABEL_OUT:
                return barLength + getLabelTextWidth(false) + ttMargin;
        }
        return 0;
    }

    private float getTooltipRight(float left) {
        return left + getTooltipWidth();
    }

    private float getTooltipTop(int pos) {
        return (getBarTop(pos) + getBarBottom(pos) - ttTextBounds.height()) / 2f - ttPadding;
    }

    private float getTooltipBottom(int pos) {
        return (getBarTop(pos) + getBarBottom(pos) + ttTextBounds.height()) / 2f + ttPadding;
    }

    private void drawTooltip(Canvas c,
                             float left, float top, float right, float bottom, Paint fillPaint,
                             String text, float textX, float textY, Paint textPaint) {
        // half of height so semicircle ends
        float cornerRadius = (bottom - top) / 2f;

        c.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, fillPaint);

        c.drawText(text, textX, textY, textPaint);
    }

    private float drawAmountTooltip(Canvas c, BarChartEntry data, int pos) {

        MassUnit massUnit = MassUnit.getMassUnit(context);

        String tooltipText = context.getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                decimalFormat.format(Utils.convertToCurrentMassUnit(context, data.getAmount())));

        tooltipTextPaint.getTextBounds(tooltipText, 0, tooltipText.length(), ttTextBounds);

        float left = getTooltipLeft(data, getLabelPos(data));
        float top = getTooltipTop(pos);
        float right = getTooltipRight(left);
        float bottom = getTooltipBottom(pos);

        float textX = left + ttPadding;
        float textY = getLabelY(pos);

        drawTooltip(c, left, top, right, bottom, tooltipFillPaint, tooltipText, textX, textY, tooltipTextPaint);

        return right;
    }

    private float getReqTooltipLeft(BarChartEntry data, LabelPos labelPos, float normalTooltipRight) {
        TooltipPos tooltipPos = getTooltipPos(data, labelPos);

        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        if (getReqTooltipWidthWithMargin() < reqBarLength - barLength) {
            return reqBarLength - getReqTooltipWidth() - ttMargin;
        } else if (tooltipPos == TooltipPos.INSIDE_NORMAL) {
            return reqBarLength + ttMargin;
        } else {
            return normalTooltipRight + ttMargin;
        }
    }

    private void drawReqTooltip(Canvas c, BarChartEntry data, int pos, float normalTooltipRight) {
        MassUnit massUnit = MassUnit.getMassUnit(context);
        String text = context.getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                decimalFormat.format(Utils.convertToCurrentMassUnit(context, data.getAmountRequired())));
        reqTooltipTextPaint.getTextBounds(text, 0, text.length(), reqTtTextBounds);

        float left = getReqTooltipLeft(data, getLabelPos(data), normalTooltipRight);
        float top = getTooltipTop(pos);
        float right = getTooltipRight(left);
        float bottom = getTooltipBottom(pos);

        float textX = left + ttPadding;
        float textY = getLabelY(pos);

        drawTooltip(c, left, top, right, bottom, reqTooltipFillPaint, text, textX, textY, reqTooltipTextPaint);
    }

    private void drawTooltips(Canvas c, BarChartEntry data, int pos) {
        tooltipFillPaint.setAlpha(ttAlpha);
        tooltipTextPaint.setAlpha(ttAlpha);
        float normalTooltipRight = drawAmountTooltip(c, data, pos);

        if (isReqBarShown(data)) {
            reqTooltipFillPaint.setAlpha(ttAlpha);
            reqTooltipTextPaint.setAlpha(ttAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        }
    }

    private void drawPreviousTooltips(Canvas c, BarChartEntry data, int pos) {
        tooltipFillPaint.setAlpha(prevTtAlpha);
        tooltipTextPaint.setAlpha(prevTtAlpha);
        float normalTooltipRight = drawAmountTooltip(c, data, pos);

        if (isReqBarShown(data)) {
            reqTooltipFillPaint.setAlpha(prevTtAlpha);
            reqTooltipTextPaint.setAlpha(prevTtAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        }
    }

    private void updateTooltipAlpha() {
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
