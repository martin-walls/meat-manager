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

    private Context context;

    private List<BarChartEntry> dataSet = new ArrayList<>();

    private float xMax = 0;

    // no bar selected at start
    private int selectedIndex = -1;
    private int prevSelectedIndex = -1;

    // colour of bar to show amount of stock held
    private Paint amountBarFillPaint;
    // colour of required bar
    private Paint reqBarFillPaint;
    private Paint greenReqBarFillPaint;

    // text colour outside bar
    private Paint barOuterLabelPaint;
    // text colour inside bar
    private Paint barInnerLabelPaint;

    // colour of tooltip for amount bar
    private Paint amountTooltipFillPaint;
    // text colour in amount tooltip
    private Paint amountTooltipTextPaint;

    // colour of tooltip for required bar
    private Paint reqTooltipFillPaint;
    // text colour in required tooltip
    private Paint reqTooltipTextPaint;

    // thickness of each bar, in dp
    private final int barWidthDp = 48;
    // thickness of each bar
    private float barWidth;

    // space between each bar
    private float barSpacing;
    // corner radius for bars
    private float barCornerRadius;

    // space between label text and edge of bar
    private float labelMarginInside;
    private float labelMarginOutside;

    // space around tooltip
    private float tooltipMargin;
    // space between tooltip text and edge of tooltip
    private float tooltipPadding;

    // opacity of tooltips
    private int tooltipAlpha = 0;
    private int prevTooltipAlpha = 255;
    // delay between redrawing the view each time
    private int tooltipAlphaDelay = 10;
    // length of fade animation in ms
    private int tooltipAlphaAnimLength = 200;
    // how much to change opacity by each time the view is redrawn
    private int tooltipAlphaStep = 255 / (tooltipAlphaAnimLength / tooltipAlphaDelay);

    // to store bounds of labels and tooltips
    private Rect labelTextBounds = new Rect();
    private Rect tooltipTextBounds = new Rect();
    private Rect reqTooltipTextBounds = new Rect();

    // to round values to 2 d.p.
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

        // get colours from theme attrs
        int amountBarColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphAmountBarColor);
        int reqBarColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphReqBarColor);
        int barOuterTextColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphBarOuterTextColor);
        int barInnerTextColor = Utils.getColorFromTheme(context, R.attr.dashboardGraphBarInnerTextColor);
        int amountTooltipFillColor = Utils.getColorFromTheme(context, R.attr.dashboardAmountTooltipColor);
        int amountTooltipTextColor = Utils.getColorFromTheme(context, R.attr.dashboardAmountTooltipTextColor);
        int reqTooltipFillColor = Utils.getColorFromTheme(context, R.attr.dashboardReqTooltipColor);
        int reqTooltipTextColor = Utils.getColorFromTheme(context, R.attr.dashboardReqTooltipTextColor);

        // bar paints
        amountBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        amountBarFillPaint.setColor(amountBarColor);

        reqBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        reqBarFillPaint.setColor(reqBarColor);

        //////////////////////
        greenReqBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        int greenColor = Utils.getColorFromTheme(context, R.attr.dashboardGreen);
        greenReqBarFillPaint.setColor(greenColor);

        // label paints
        barOuterLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barOuterLabelPaint.setColor(barOuterTextColor);
        barOuterLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barOuterLabelPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        barInnerLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barInnerLabelPaint.setColor(barInnerTextColor);
        barInnerLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barInnerLabelPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        // tooltip paints
        amountTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        amountTooltipFillPaint.setColor(amountTooltipFillColor);

        amountTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        amountTooltipTextPaint.setColor(amountTooltipTextColor);
        amountTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        amountTooltipTextPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        reqTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        reqTooltipFillPaint.setColor(reqTooltipFillColor);

        reqTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        reqTooltipTextPaint.setColor(reqTooltipTextColor);
        reqTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        reqTooltipTextPaint.setTextSize(Utils.convertSpToPixelSize(14, context));

        // spacing and size values
        barWidth = Utils.convertDpToPixelSize(barWidthDp, context);
        // spacing is 1/10 of bar thickness
        barSpacing = barWidth * 0.1f;
        barCornerRadius = Utils.convertDpToPixelSize(8, context);

        labelMarginInside = Utils.convertDpToPixelSize(12, context);
        labelMarginOutside = Utils.convertDpToPixelSize(8, context);

        tooltipMargin = Utils.convertDpToPixelSize(8, context);
        tooltipPadding = Utils.convertDpToPixelSize(8, context);

        // round to 2 d.p.
        decimalFormat = new DecimalFormat("#.##");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // set custom height of view, from number of bars shown (plus padding),
        // this allows the view to extend below the screen so it can scroll
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Utils.convertDpToPixelSize(barWidthDp * dataSet.size() + getPaddingTop(), context);
        setMeasuredDimension(width, height);
    }

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

            ///////////////////////////////////////////
            drawGreenReqBar(c, entry, pos);

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
            
            // hide tooltip if same bar pressed again
            if (selectedIndex == prevSelectedIndex) {
                selectedIndex = -1;
            }

            // reset tooltip alpha values
            tooltipAlpha = 0;
            prevTooltipAlpha = 255;

            // redraw graph
            invalidate();
        }
        return true;
    }

    private void resetSelectedItem() {
        selectedIndex = -1;
        prevSelectedIndex = -1;
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
        resetSelectedItem();
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

    private void drawGreenReqBar(Canvas c, BarChartEntry data, int pos) {
        if (data.getAmountRequired() <= data.getAmount()) {
            float length = getBarLength(data.getAmountRequired());
            drawBar(c, 0, getBarTop(pos), length, getBarBottom(pos), greenReqBarFillPaint);
        }
    }

    private void drawAmountBar(Canvas c, BarChartEntry data, int pos) {
        float length = getBarLength(data.getAmount());
        drawBar(c, 0, getBarTop(pos), length, getBarBottom(pos), amountBarFillPaint);
    }

    // label drawing methods
    private float getLabelTextWidth(boolean isInside) {
        if (isInside) {
            return labelTextBounds.width() + labelMarginInside * 2f;
        } else {
            return labelTextBounds.width() + labelMarginOutside * 2f;
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
        barOuterLabelPaint.getTextBounds(label, 0, label.length(), labelTextBounds);

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
        barOuterLabelPaint.getTextBounds(label, 0, label.length(), labelTextBounds);

        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        switch (getLabelPos(data)) {
            case INSIDE_NORMAL:
                return labelMarginInside;
            case INSIDE_REQ:
            case OUTSIDE_NO_REQ:
                return barLength + labelMarginOutside;
            case OUTSIDE_REQ:
                return reqBarLength + labelMarginOutside;
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
            c.drawText(label, labelX, labelY, barOuterLabelPaint);
        }
    }

    // tooltip drawing methods
    private float getTooltipWidth() {
        return tooltipTextBounds.width() + tooltipPadding * 2f;
    }

    private float getTooltipWidthWithMargin() {
        return getTooltipWidth() + tooltipMargin * 2f;
    }

    private float getReqTooltipWidth() {
        return reqTooltipTextBounds.width() + tooltipPadding * 2f;
    }

    private float getReqTooltipWidthWithMargin() {
        return getReqTooltipWidth() + tooltipMargin * 2f;
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
                return barLength - getTooltipWidth() - tooltipMargin;
            case INSIDE_REQ:
                return reqBarLength - (getReqTooltipWidth() + tooltipMargin) - (getTooltipWidth() + tooltipMargin);
            case OUTSIDE_REQ_LABEL_IN:
                return reqBarLength + tooltipMargin;
            case OUTSIDE_NO_REQ_LABEL_IN:
                return barLength + tooltipMargin;
            case OUTSIDE_REQ_LABEL_OUT:
                return reqBarLength + getLabelTextWidth(false) + tooltipMargin;
            case OUTSIDE_NO_REQ_LABEL_OUT:
                return barLength + getLabelTextWidth(false) + tooltipMargin;
        }
        return 0;
    }

    private float getTooltipRight(float left) {
        return left + getTooltipWidth();
    }

    private float getTooltipTop(int pos) {
        return (getBarTop(pos) + getBarBottom(pos) - tooltipTextBounds.height()) / 2f - tooltipPadding;
    }

    private float getTooltipBottom(int pos) {
        return (getBarTop(pos) + getBarBottom(pos) + tooltipTextBounds.height()) / 2f + tooltipPadding;
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

        amountTooltipTextPaint.getTextBounds(tooltipText, 0, tooltipText.length(), tooltipTextBounds);

        float left = getTooltipLeft(data, getLabelPos(data));
        float top = getTooltipTop(pos);
        float right = getTooltipRight(left);
        float bottom = getTooltipBottom(pos);

        float textX = left + tooltipPadding;
        float textY = getLabelY(pos);

        drawTooltip(c, left, top, right, bottom, amountTooltipFillPaint, tooltipText, textX, textY, amountTooltipTextPaint);

        return right;
    }

    private float getReqTooltipLeft(BarChartEntry data, LabelPos labelPos, float normalTooltipRight) {
        TooltipPos tooltipPos = getTooltipPos(data, labelPos);

        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        if (getReqTooltipWidthWithMargin() < reqBarLength - barLength) {
            return reqBarLength - getReqTooltipWidth() - tooltipMargin;
        } else if (tooltipPos == TooltipPos.INSIDE_NORMAL) {
            return reqBarLength + tooltipMargin;
        } else {
            return normalTooltipRight + tooltipMargin;
        }
    }

    private void drawReqTooltip(Canvas c, BarChartEntry data, int pos, float normalTooltipRight) {
        MassUnit massUnit = MassUnit.getMassUnit(context);
        String text = context.getString(massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                decimalFormat.format(Utils.convertToCurrentMassUnit(context, data.getAmountRequired())));
        reqTooltipTextPaint.getTextBounds(text, 0, text.length(), reqTooltipTextBounds);

        float left = getReqTooltipLeft(data, getLabelPos(data), normalTooltipRight);
        float top = getTooltipTop(pos);
        float right = getTooltipRight(left);
        float bottom = getTooltipBottom(pos);

        float textX = left + tooltipPadding;
        float textY = getLabelY(pos);

        drawTooltip(c, left, top, right, bottom, reqTooltipFillPaint, text, textX, textY, reqTooltipTextPaint);
    }

    private void drawTooltips(Canvas c, BarChartEntry data, int pos) {
        amountTooltipFillPaint.setAlpha(tooltipAlpha);
        amountTooltipTextPaint.setAlpha(tooltipAlpha);
        float normalTooltipRight = drawAmountTooltip(c, data, pos);

        if (isReqBarShown(data)) {
            reqTooltipFillPaint.setAlpha(tooltipAlpha);
            reqTooltipTextPaint.setAlpha(tooltipAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        }
    }

    private void drawPreviousTooltips(Canvas c, BarChartEntry data, int pos) {
        amountTooltipFillPaint.setAlpha(prevTooltipAlpha);
        amountTooltipTextPaint.setAlpha(prevTooltipAlpha);
        float normalTooltipRight = drawAmountTooltip(c, data, pos);

        if (isReqBarShown(data)) {
            reqTooltipFillPaint.setAlpha(prevTooltipAlpha);
            reqTooltipTextPaint.setAlpha(prevTooltipAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        }
    }

    private void updateTooltipAlpha() {
        if (tooltipAlpha < 255 || prevTooltipAlpha > 0) {
            postInvalidateDelayed(tooltipAlphaDelay);
        }

        if (tooltipAlpha < 255) {
            tooltipAlpha += tooltipAlphaStep;
            if (tooltipAlpha > 255) {
                tooltipAlpha = 255;
            }
        }

        if (prevTooltipAlpha > 0) {
            prevTooltipAlpha -= tooltipAlphaStep;
            if (prevTooltipAlpha < 0) {
                prevTooltipAlpha = 0;
            }
        }
    }
}
