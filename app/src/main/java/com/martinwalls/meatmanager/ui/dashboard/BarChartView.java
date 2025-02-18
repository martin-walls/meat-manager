package com.martinwalls.meatmanager.ui.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.util.MassUnit;
import com.martinwalls.meatmanager.util.Utils;

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
    private Paint moreReqBarFillPaint;
    private Paint lessReqBarFillPaint;

    // text colour outside bar
    private Paint barOuterLabelPaint;
    // text colour inside bar
    private Paint barInnerLabelPaint;

    // colour of tooltip for amount bar
    private Paint amountTooltipFillPaint;
    // text colour in amount tooltip
    private Paint amountTooltipTextPaint;

    // colour of tooltip for required bar
    private Paint moreReqTooltipFillPaint;
    // text colour in required tooltip
    private Paint moreReqTooltipTextPaint;

    private Paint lessReqTooltipFillPaint;
    private Paint lessReqTooltipTextPaint;

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

    // text size of labels and tooltips (in sp)
    private final int TEXT_SIZE = 14;

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
    private Rect amountTooltipTextBounds = new Rect();
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

    /**
     * Initialises variables for drawing the view. Gets colour values and
     * creates {@link Paint} objects, to avoid doing so in {@link #onDraw} to
     * improve efficiency.
     */
    private void init(Context context) {
        this.context = context;

        initPaints();

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

    /**
     * Initialises all paint objects, getting colour values from theme attributes.
     */
    private void initPaints() {
        // get colours from theme attrs
        int amountBarColor = Utils.getColorFromTheme(context,
                R.attr.dashboardGraphAmountBarColor);
        int reqBarColor = Utils.getColorFromTheme(context,
                R.attr.dashboardGraphMoreReqBarColor);
        int lessReqBarColor = Utils.getColorFromTheme(context,
                R.attr.dashboardGraphLessReqBarColor);

        int barOuterTextColor = Utils.getColorFromTheme(context,
                R.attr.dashboardGraphBarOuterTextColor);
        int barInnerTextColor = Utils.getColorFromTheme(context,
                R.attr.dashboardGraphBarInnerTextColor);

        int amountTooltipFillColor = Utils.getColorFromTheme(context,
                R.attr.dashboardAmountTooltipColor);
        int amountTooltipTextColor = Utils.getColorFromTheme(context,
                R.attr.dashboardAmountTooltipTextColor);
        int reqTooltipFillColor = Utils.getColorFromTheme(context,
                R.attr.dashboardMoreReqTooltipColor);
        int reqTooltipTextColor = Utils.getColorFromTheme(context,
                R.attr.dashboardMoreReqTooltipTextColor);
        int lessReqTooltipFillColor = Utils.getColorFromTheme(context,
                R.attr.dashboardLessReqTooltipColor);
        int lessReqTooltipTextColor = Utils.getColorFromTheme(context,
                R.attr.dashboardLessReqTooltipTextColor);

        // bar paints
        amountBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        amountBarFillPaint.setColor(amountBarColor);

        moreReqBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        moreReqBarFillPaint.setColor(reqBarColor);

        lessReqBarFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lessReqBarFillPaint.setColor(lessReqBarColor);

        int textSize = Utils.convertSpToPixelSize(TEXT_SIZE, context);

        // label paints
        barOuterLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barOuterLabelPaint.setColor(barOuterTextColor);
        barOuterLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barOuterLabelPaint.setTextSize(textSize);

        barInnerLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barInnerLabelPaint.setColor(barInnerTextColor);
        barInnerLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        barInnerLabelPaint.setTextSize(textSize);

        // tooltip paints
        amountTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        amountTooltipFillPaint.setColor(amountTooltipFillColor);

        amountTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        amountTooltipTextPaint.setColor(amountTooltipTextColor);
        amountTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        amountTooltipTextPaint.setTextSize(textSize);

        moreReqTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        moreReqTooltipFillPaint.setColor(reqTooltipFillColor);

        moreReqTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        moreReqTooltipTextPaint.setColor(reqTooltipTextColor);
        moreReqTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        moreReqTooltipTextPaint.setTextSize(textSize);

        lessReqTooltipFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lessReqTooltipFillPaint.setColor(lessReqTooltipFillColor);

        lessReqTooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lessReqTooltipTextPaint.setColor(lessReqTooltipTextColor);
        lessReqTooltipTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        lessReqTooltipTextPaint.setTextSize(textSize);
    }

    /**
     * Sets a custom height for the View, so it can scroll to show all bars.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // get height from number of bars shown, this allows the view to extend
        // below the screen so it can scroll
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Utils.convertDpToPixelSize(
                barWidthDp * dataSet.size() + getPaddingTop(), context);
        setMeasuredDimension(width, height);
    }

    /**
     * Draws the bar chart for the given data.
     */
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        for (int pos = 0; pos < dataSet.size(); pos++) {
            BarChartEntry entry = dataSet.get(pos);

            // draw bar for amount of stock required if more than amount held
            drawMoreReqBar(c, entry, pos);

            // draw bar for amount of stock held
            drawAmountBar(c, entry, pos);

            // draw bar for amount of stock required if less than amount held
            drawLessReqBar(c, entry, pos);

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

    /**
     * Handles clicks on the View, to show tooltips when the user clicks
     * on a bar.
     */
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

    /**
     * Initialises the selected bar to none selected.
     */
    private void resetSelectedItem() {
        selectedIndex = -1;
        prevSelectedIndex = -1;
    }

    /**
     * Sets the data set to draw the graph for. Redraws the graph with
     * the updated data.
     */
    public void setData(@NonNull List<BarChartEntry> newDataSet) {
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
        requestLayout();
        invalidate();
    }

    /**
     * Calculates whether the required amount bar should be shown for this data
     * and is more than the amount of stock held.
     */
    private boolean isMoreReqBarShown(BarChartEntry data) {
        return data.getAmountRequired() > data.getAmount();
    }

    /**
     * Calculates whether the required amount bar should be shown for this data
     * and is less than the amount of stock held.
     */
    private boolean isLessReqBarShown(BarChartEntry data) {
        return data.getAmountRequired() > 0
                && data.getAmountRequired() < data.getAmount();
    }

    //region bars
    /**
     * Calculates the y-value of the top edge of the bar at position {@code pos}.
     */
    private float getBarTop(int pos) {
        return getPaddingTop() + barWidth * pos + (barSpacing / 2f);
    }

    /**
     * Calculates the y-value of the bottom edge of the bar at position {@code pos}.
     */
    private float getBarBottom(int pos) {
        return getPaddingTop() + barWidth * (pos + 1) - (barSpacing / 2f);
    }

    /**
     * Calculates the pixel length of the bar.
     */
    private float getBarLength(float value) {
        return value / xMax * getWidth();
    }

    /**
     * Draws a bar with the specified bounds and {@link Paint}.
     */
    private void drawBar(Canvas c,
                         float left, float top, float right, float bottom,
                         Paint paint) {
        c.drawRoundRect(left, top, right, bottom, barCornerRadius, barCornerRadius, paint);
    }

    /**
     * Draws a required amount bar for {@code data} at position {@code pos}
     * outside the amount bar, if there is less stock than required.
     */
    private void drawMoreReqBar(Canvas c, BarChartEntry data, int pos) {
        if (isMoreReqBarShown(data)) {
            float length = getBarLength(data.getAmountRequired());
            drawBar(c, 0, getBarTop(pos), length, getBarBottom(pos), moreReqBarFillPaint);
        }
    }

    /**
     * Draws a required amount bar for {@code data} at position {@code pos}
     * inside the amount bar, if there is more stock than required.
     */
    private void drawLessReqBar(Canvas c, BarChartEntry data, int pos) {
        if (data.getAmountRequired() <= data.getAmount()) {
            float length = getBarLength(data.getAmountRequired());
            drawBar(c, 0, getBarTop(pos), length, getBarBottom(pos), lessReqBarFillPaint);
        }
    }

    /**
     * Draws an amount bar for {@code data} at position {@code pos}.
     */
    private void drawAmountBar(Canvas c, BarChartEntry data, int pos) {
        float length = getBarLength(data.getAmount());
        drawBar(c, 0, getBarTop(pos), length, getBarBottom(pos), amountBarFillPaint);
    }
    //endregion bars

    //region label
    /**
     * Calculates the pixel width of a bar label including its margin.
     *
     * @param isInside whether to calculate the width for showing the label
     *                 inside a bar or outside.
     */
    private float getLabelTextWidth(boolean isInside) {
        if (isInside) {
            return labelTextBounds.width() + labelMarginInside * 2f;
        } else {
            return labelTextBounds.width() + labelMarginOutside * 2f;
        }
    }

    /**
     * Stores the different positions a label can be in, relative to its bar.
     */
    private enum LabelPos {
        /**
         * Label shown inside amount bar.
         */
        INSIDE_AMOUNT,
        /**
         * Label shown inside required bar.
         */
        INSIDE_REQ,
        /**
         * Label shown to right of required bar.
         */
        OUTSIDE_REQ,
        /**
         * Label shown to right of amount bar (more required bar is not showing).
         */
        OUTSIDE_NO_REQ
    }

    /**
     * Calculates the {@link LabelPos} for the label shown for {@code data}.
     */
    private LabelPos getLabelPos(BarChartEntry data) {
        String label = data.getName().toUpperCase();
        barOuterLabelPaint.getTextBounds(label, 0, label.length(), labelTextBounds);

        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        // if text doesn't fit inside, show it outside
        if (getLabelTextWidth(true) > barLength) {
            // if required bar shown
            if (isMoreReqBarShown(data)) {
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
            return LabelPos.INSIDE_AMOUNT;
        }
    }

    /**
     * Calculates the x-position of the label to be shown for {@code data}.
     */
    private float getLabelX(BarChartEntry data) {
        String label = data.getName().toUpperCase();
        barOuterLabelPaint.getTextBounds(label, 0, label.length(), labelTextBounds);

        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        switch (getLabelPos(data)) {
            case INSIDE_AMOUNT:
                return labelMarginInside;
            case INSIDE_REQ:
            case OUTSIDE_NO_REQ:
                return barLength + labelMarginOutside;
            case OUTSIDE_REQ:
                return reqBarLength + labelMarginOutside;
        }
        return 0;
    }

    /**
     * Calculates the y-position of the label for the bar at position {@code pos}.
     */
    private float getLabelY(int pos) {
        return (getBarTop(pos) + getBarBottom(pos) + labelTextBounds.height()) / 2f;
    }

    /**
     * Draws the label for {@code data} at position {@code pos}.
     */
    private void drawLabelText(Canvas c, BarChartEntry data, int pos) {
        String label = data.getName().toUpperCase();

        float labelX = getLabelX(data);

        // centre label vertically in bar
        float labelY = getLabelY(pos);

        // if inside normal bar, use inside fill paint
        if (getLabelPos(data) == LabelPos.INSIDE_AMOUNT) {
            c.drawText(label, labelX, labelY, barInnerLabelPaint);
        } else {
            c.drawText(label, labelX, labelY, barOuterLabelPaint);
        }
    }
    //endregion label

    //region tooltips
    /**
     * Calculates the width of a tooltip with text bounds stored in
     * {@link #amountTooltipTextBounds}.
     */
    private float getTooltipWidth() {
        return amountTooltipTextBounds.width() + tooltipPadding * 2f;
    }

    /**
     * Calculates the width of a tooltip including its margin.
     *
     * @see #getTooltipWidth()
     */
    private float getTooltipWidthWithMargin() {
        return getTooltipWidth() + tooltipMargin * 2f;
    }

    /**
     * Calculates the width of a required tooltip with text bounds stored in
     * {@link #reqTooltipTextBounds}.
     */
    private float getReqTooltipWidth() {
        return reqTooltipTextBounds.width() + tooltipPadding * 2f;
    }

    /**
     * Calculates the width of a required tooltip including its margin.
     *
     * @see #getReqTooltipWidth()
     */
    private float getReqTooltipWidthWithMargin() {
        return getReqTooltipWidth() + tooltipMargin * 2f;
    }

    /**
     * Stores the different positions the amount tooltip can be in relative to
     * its bar.
     */
    private enum AmountTooltipPos {
        /**
         * Tooltip inside amount bar (to right edge of bar).
         */
        INSIDE_AMOUNT,
        /**
         * Tooltip inside more required bar (to right edge of bar).
         */
        INSIDE_MORE_REQ,
        /**
         * Tooltip outside required bar, with the label shown inside one of the bars.
         */
        OUTSIDE_REQ_LABEL_IN,
        /**
         * Tooltip outside required bar, with the label also outside, so tooltip
         * shown to right of label.
         */
        OUTSIDE_REQ_LABEL_OUT,
        /**
         * Tooltip outside amount bar, with the label shown inside the bar.
         */
        OUTSIDE_AMOUNT_LABEL_IN,
        /**
         * Tooltip outside amount bar, with the label also outside, so tooltip
         * shown to right of label.
         */
        OUTSIDE_AMOUNT_LABEL_OUT
    }

    /**
     * Calculates the {@link AmountTooltipPos} for the tooltip shown for {@code data},
     * with the bar label in position {@code labelPos}.
     */
    private AmountTooltipPos getTooltipPos(BarChartEntry data, LabelPos labelPos) {
        if (( // enough space inside amount bar for tooltip
                labelPos == LabelPos.INSIDE_AMOUNT
                        && getTooltipWidthWithMargin()
                        < getBarLength(data.getAmount()) - getLabelTextWidth(true))
                || (labelPos != LabelPos.INSIDE_AMOUNT
                        && getTooltipWidthWithMargin() < getBarLength(data.getAmount()))) {
            return AmountTooltipPos.INSIDE_AMOUNT;
        } else if ( // tooltip can't fit outside req bar (if shown)
                isMoreReqBarShown(data)
                && getTooltipWidthWithMargin()
                        > getWidth() - getBarLength(data.getAmountRequired())) {
            return AmountTooltipPos.INSIDE_MORE_REQ;
        } else if (// not enough space inside amount bar for tooltip
                // and not shown inside required bar
                // and bar label is shown inside
                labelPos == LabelPos.INSIDE_AMOUNT) {
            if (isMoreReqBarShown(data)) {
                return AmountTooltipPos.OUTSIDE_REQ_LABEL_IN;
            } else {
                return AmountTooltipPos.OUTSIDE_AMOUNT_LABEL_IN;
            }
        } else { // not shown inside amount or required bars, and bar label is outside
            if (isMoreReqBarShown(data)) {
                return AmountTooltipPos.OUTSIDE_REQ_LABEL_OUT;
            } else {
                return AmountTooltipPos.OUTSIDE_AMOUNT_LABEL_OUT;
            }
        }
    }

    /**
     * Calculates the x-value of the left edge of the tooltip for {@code data},
     * with the bar label in position {@code labelPos}.
     */
    private float getTooltipLeft(BarChartEntry data, LabelPos labelPos) {
        float barLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        switch (getTooltipPos(data, labelPos)) {
            case INSIDE_AMOUNT:
                return barLength - getTooltipWidth() - tooltipMargin;
            case INSIDE_MORE_REQ:
                return reqBarLength - (getReqTooltipWidth() + tooltipMargin)
                        - (getTooltipWidth() + tooltipMargin);
            case OUTSIDE_REQ_LABEL_IN:
                return reqBarLength + tooltipMargin;
            case OUTSIDE_AMOUNT_LABEL_IN:
                return barLength + tooltipMargin;
            case OUTSIDE_REQ_LABEL_OUT:
                return reqBarLength + getLabelTextWidth(false) + tooltipMargin;
            case OUTSIDE_AMOUNT_LABEL_OUT:
                return barLength + getLabelTextWidth(false) + tooltipMargin;
        }
        return 0;
    }

    /**
     * Calculates the x-value of the right edge of the tooltip with its left
     * edge at {@code left}.
     */
    private float getTooltipRight(float left) {
        return left + getTooltipWidth();
    }

    /**
     * Calculates the y-value of the top edge of the tooltip.
     */
    private float getTooltipTop(int pos) {
        return (getBarTop(pos) + getBarBottom(pos)
                - amountTooltipTextBounds.height()) / 2f - tooltipPadding;
    }

    /**
     * Calculates the y-value of the bottom edge of the tooltip.
     */
    private float getTooltipBottom(int pos) {
        return (getBarTop(pos) + getBarBottom(pos) + amountTooltipTextBounds.height()) / 2f
                + tooltipPadding;
    }

    /**
     * Draws a tooltip with the specified bounds, text, and {@link Paint}s.
     */
    private void drawTooltip(Canvas c,
                             float left, float top, float right, float bottom, Paint fillPaint,
                             String text, float textX, float textY, Paint textPaint) {
        // half of height so semicircle ends
        float cornerRadius = (bottom - top) / 2f;

        c.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, fillPaint);

        c.drawText(text, textX, textY, textPaint);
    }

    /**
     * Draws a tooltip to show amount of stock for {@code data} at position {@code pos}.
     *
     * @return the x position of the right edge of the tooltip, in px
     */
    private float drawAmountTooltip(Canvas c, BarChartEntry data, int pos) {
        String tooltipText = getTooltipText(data.getAmount());

        float left = getTooltipLeft(data, getLabelPos(data));
        float top = getTooltipTop(pos);
        float right = getTooltipRight(left);
        float bottom = getTooltipBottom(pos);

        float textX = left + tooltipPadding;
        float textY = getLabelY(pos);

        drawTooltip(c,
                left, top, right, bottom, amountTooltipFillPaint,
                tooltipText, textX, textY, amountTooltipTextPaint);

        return right;
    }

    /**
     * Stores the different positions the required tooltip can be in.
     */
    private enum ReqTooltipPos {
        /**
         * Tooltip inside required bar (to right edge of bar).
         */
        INSIDE_REQ,
        /**
         * Tooltip outside required bar, with the label shown inside one of the bars.
         */
        OUTSIDE_REQ_LABEL_IN,
        /**
         * Tooltip outside required bar, with the label also outside, so tooltip
         * shown to right of the label.
         */
        OUTSIDE_REQ_LABEL_OUT,
        /**
         * Tooltip shown to the right of the amount tooltip.
         */
        TO_RIGHT_OF_AMOUNT,
        /**
         * Tooltip inside amount bar, with the amount tooltip also inside amount bar.
         */
        INSIDE_AMOUNT_WITH_AMOUNT,
        /**
         * Tooltip outside normal bar (more required bar not showing), with the
         * label shown inside the bar.
         */
        OUTSIDE_NORMAL_LABEL_IN,
        /**
         * Tooltip outside normal bar (more required bar not showing), with the
         * label also outside, so tooltip shown to right of the label.
         */
        OUTSIDE_NORMAL_LABEL_OUT
    }

    /**
     * Calculate whether the required tooltip will fit inside the required bar,
     * with the label in position {@code labelPos}.
     */
    private boolean reqTooltipFitsInsideReqBar(float tooltipWidth, LabelPos labelPos,
                                               float reqBarLength, float amountBarLength) {

        if (labelPos == LabelPos.INSIDE_REQ) { // label inside required bar
            // can tooltip fit inside required bar with label
            return tooltipWidth < reqBarLength - amountBarLength
                                          - getLabelTextWidth(true);
        } else { // label not inside required bar
            // can tooltip fit inside required bar
            return tooltipWidth < reqBarLength - amountBarLength;
        }
    }

    /**
     * Calculate whether the required tooltip will fit inside the amount bar
     * with the label also inside the amount bar.
     */
    private boolean reqTooltipFitsInsideAmountBarWithLabel(float tooltipWidth,
                                                           float amountBarLength) {
        return tooltipWidth < amountBarLength
                                      - getTooltipWidthWithMargin()
                                      - getLabelTextWidth(true);
    }

    /**
     * Calculates the {@link ReqTooltipPos} for the required tooltip shown for {@code data},
     * with the bar label in position {@code labelPos} and the amount tooltip
     * in position {@code amountTooltipPos}.
     */
    private ReqTooltipPos getReqTooltipPos(BarChartEntry data, LabelPos labelPos,
                                           AmountTooltipPos amountTooltipPos) {
        float reqBarLength = getBarLength(data.getAmountRequired());
        float amountBarLength = getBarLength(data.getAmount());

        float tooltipWidth = getReqTooltipWidthWithMargin();

        if (isMoreReqBarShown(data)) {
            if (reqTooltipFitsInsideReqBar(tooltipWidth, labelPos,
                    reqBarLength, amountBarLength)) {
                // req tooltip fits inside req bar
                return ReqTooltipPos.INSIDE_REQ;
            } else if ( // amount tooltip inside amount bar
                    amountTooltipPos == AmountTooltipPos.INSIDE_AMOUNT) {
                if (labelPos == LabelPos.OUTSIDE_REQ) { // label outside bar
                    return ReqTooltipPos.OUTSIDE_REQ_LABEL_OUT;
                } else { // label not outside bar
                    return ReqTooltipPos.OUTSIDE_REQ_LABEL_IN;
                }
            } else { // otherwise, just show req tooltip to right of amount tooltip
                return ReqTooltipPos.TO_RIGHT_OF_AMOUNT;
            }
        } else if (isLessReqBarShown(data)) {
            // amount tooltip inside amount bar
            if (amountTooltipPos == AmountTooltipPos.INSIDE_AMOUNT) {
                if (labelPos == LabelPos.INSIDE_AMOUNT) { // label inside amount bar
                    // fits inside with label
                    if (reqTooltipFitsInsideAmountBarWithLabel(
                            tooltipWidth, amountBarLength)) {
                        return ReqTooltipPos.INSIDE_AMOUNT_WITH_AMOUNT;
                    } else { // doesn't fit inside with label
                        return ReqTooltipPos.OUTSIDE_NORMAL_LABEL_IN;
                    }
                } else { // label outside amount bar
                    if (tooltipWidth < amountBarLength - getTooltipWidthWithMargin()) {
                        return ReqTooltipPos.INSIDE_AMOUNT_WITH_AMOUNT;
                    } else {
                        return ReqTooltipPos.OUTSIDE_NORMAL_LABEL_OUT;
                    }
                }
            } else { // normal tt out
                return ReqTooltipPos.TO_RIGHT_OF_AMOUNT;
            }
        }
        return ReqTooltipPos.TO_RIGHT_OF_AMOUNT;
    }

    /**
     * Calculates the x-value of the left edge of the required tooltip.
     * <p>Depending on space, this may be:
     * <ul>
     *     <li>To the right of the amount tooltip
     *     <li>Inside the more required bar, if shown
     *     <li>Inside the amount bar with the amount tooltip
     *     <li>Outside the bar when the amount tooltip is inside the bar
     */
    private float getReqTooltipLeft(BarChartEntry data, LabelPos labelPos,
                                    float amountTooltipRight) {
        AmountTooltipPos amountTooltipPos = getTooltipPos(data, labelPos);

        float amountBarLength = getBarLength(data.getAmount());
        float reqBarLength = getBarLength(data.getAmountRequired());

        switch (getReqTooltipPos(data, labelPos, amountTooltipPos)) {
            case INSIDE_REQ:
                return reqBarLength - getReqTooltipWidth() - tooltipMargin;
            case OUTSIDE_REQ_LABEL_IN:
                return reqBarLength + tooltipMargin;
            case OUTSIDE_REQ_LABEL_OUT:
                return reqBarLength + getLabelTextWidth(false) + tooltipMargin;
            case TO_RIGHT_OF_AMOUNT:
                return amountTooltipRight + tooltipMargin;
            case INSIDE_AMOUNT_WITH_AMOUNT:
                return amountTooltipRight - getTooltipWidth()
                               - getReqTooltipWidth() - tooltipMargin;
            case OUTSIDE_NORMAL_LABEL_IN:
                return amountBarLength + tooltipMargin;
            case OUTSIDE_NORMAL_LABEL_OUT:
                return amountBarLength + getLabelTextWidth(false) + tooltipMargin;
        }
        return 0;
    }

    /**
     * Calculates the x-value of the right edge of the required tooltip with its
     * left edge at {@code left}.
     */
    private float getReqTooltipRight(float left) {
        return left + getReqTooltipWidth();
    }

    /**
     * Draws a tooltip to show amount of stock required for {@code data} at
     * position {@code pos}.
     *
     * @param normalTooltipRight X-value of the right edge of the amount tooltip.
     */
    private void drawReqTooltip(Canvas c, BarChartEntry data, int pos,
                                float normalTooltipRight) {
        String tooltipText = getTooltipText(data.getAmountRequired());

        float left = getReqTooltipLeft(data, getLabelPos(data), normalTooltipRight);
        float top = getTooltipTop(pos);
        float right = getReqTooltipRight(left);
        float bottom = getTooltipBottom(pos);

        float textX = left + tooltipPadding;
        float textY = getLabelY(pos);

        if (isMoreReqBarShown(data)) {
            drawTooltip(c,
                    left, top, right, bottom, moreReqTooltipFillPaint,
                    tooltipText, textX, textY, moreReqTooltipTextPaint);
        } else if (isLessReqBarShown(data)) {
            drawTooltip(c,
                    left, top, right, bottom, lessReqTooltipFillPaint,
                    tooltipText, textX, textY, lessReqTooltipTextPaint);
        }
    }

    /**
     * Formats the given value with the current mass unit so it can be displayed
     * in a tooltip.
     */
    private String getTooltipText(float value) {
        MassUnit massUnit = MassUnit.getMassUnit(context);
        return context.getString(
                massUnit == MassUnit.KG ? R.string.amount_kg : R.string.amount_lbs,
                decimalFormat.format(Utils.convertToCurrentMassUnit(context, value)));
    }

    /**
     * Calculates the text bounds for the given text with the given Paint.
     * Stores the result in {@code outRect}.
     */
    private void calculateTooltipTextBounds(String text, Paint paint, Rect outRect) {
        paint.getTextBounds(text, 0, text.length(), outRect);
    }

    /**
     * Calculates the text bounds for the amount tooltip for this data. Stores
     * the result in {@link #amountTooltipTextBounds}.
     */
    private void calculateAmountTooltipTextBounds(BarChartEntry data) {
        calculateTooltipTextBounds(getTooltipText(data.getAmount()),
                amountTooltipTextPaint, amountTooltipTextBounds);
    }

    /**
     * Calculates the text bounds for the required tooltip for this data. Stores
     * the result in {@link #reqTooltipTextBounds}.
     */
    private void calculateReqTooltipTextBounds(BarChartEntry data) {
        calculateTooltipTextBounds(getTooltipText(data.getAmountRequired()),
                moreReqTooltipTextPaint, reqTooltipTextBounds);
    }

    /**
     * Draws all tooltips that should be shown for {@code data} at position {@code pos}.
     */
    private void drawTooltips(Canvas c, BarChartEntry data, int pos) {
        calculateAmountTooltipTextBounds(data);
        calculateReqTooltipTextBounds(data);
        amountTooltipFillPaint.setAlpha(tooltipAlpha);
        amountTooltipTextPaint.setAlpha(tooltipAlpha);
        float normalTooltipRight = drawAmountTooltip(c, data, pos);

        if (isMoreReqBarShown(data)) {
            moreReqTooltipFillPaint.setAlpha(tooltipAlpha);
            moreReqTooltipTextPaint.setAlpha(tooltipAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        } else if (isLessReqBarShown(data)) {
            lessReqTooltipFillPaint.setAlpha(tooltipAlpha);
            lessReqTooltipTextPaint.setAlpha(tooltipAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        }
    }

    /**
     * Draws all tooltips that should be shown for the previously selected
     * bar, these fade out as the alpha value is decreased.
     */
    private void drawPreviousTooltips(Canvas c, BarChartEntry data, int pos) {
        calculateAmountTooltipTextBounds(data);
        calculateReqTooltipTextBounds(data);
        amountTooltipFillPaint.setAlpha(prevTooltipAlpha);
        amountTooltipTextPaint.setAlpha(prevTooltipAlpha);
        float normalTooltipRight = drawAmountTooltip(c, data, pos);

        if (isMoreReqBarShown(data)) {
            moreReqTooltipFillPaint.setAlpha(prevTooltipAlpha);
            moreReqTooltipTextPaint.setAlpha(prevTooltipAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        } else if (isLessReqBarShown(data)) {
            lessReqTooltipFillPaint.setAlpha(prevTooltipAlpha);
            lessReqTooltipTextPaint.setAlpha(prevTooltipAlpha);
            drawReqTooltip(c, data, pos, normalTooltipRight);
        }
    }

    /**
     * Changes the alpha values for tooltips so the fade in and out.
     */
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
    //endregion tooltips
}
