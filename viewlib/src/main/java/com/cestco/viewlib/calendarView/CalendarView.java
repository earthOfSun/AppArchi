package com.cestco.viewlib.calendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.cestco.viewlib.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by wdy on 2018/3/1.
 */

public class CalendarView extends View {


    private int viewWidth;
    private int viewHeight;
    //是否可以切换月份
    private boolean isScroll;
    private int signBg;
    private int noSignBg;
    private int signFg;
    private int noSignFg;
    private int weekBg;
    private int weekFg;
    private float daySize;
    private float weekSize;
    private int minColu;
    private int minRow;
    private Paint weekPaint;
    private Paint weekBgPaint;
    private Paint dayPaint;
    private int ovalPadding;
    private String[] week = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    private Paint signOvalPaint;


    private Set<Integer> signDays;
    private Paint ovalPaint;
    private Context mContext;

    private long time;
    private int ovalRadius;
    private int ovalPaddingLong;
    private RectF rectF;
    private Paint otherMonthDayPaint;
    private boolean needOval;
    //已签到状态
    private Drawable signBitmap;
    //当天未签到状态背景图
    private Drawable onDayBitmap;
    private int weekHeight;
    private int textY;
    private int textX;
    private int lineColor;
    private Paint linePaint;

    public CalendarView(Context context) {
        super(context);
        mContext = context;
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        isScroll = typedArray.getBoolean(R.styleable.CalendarView_isScroll, false);

        signBg = typedArray.getColor(R.styleable.CalendarView_signBg, Color.RED);
        noSignBg = typedArray.getColor(R.styleable.CalendarView_noSignBg, Color.WHITE);
        signFg = typedArray.getColor(R.styleable.CalendarView_signFg, Color.WHITE);
        noSignFg = typedArray.getColor(R.styleable.CalendarView_noSignFg, Color.BLACK);
        weekBg = typedArray.getColor(R.styleable.CalendarView_weekBg, Color.WHITE);
        weekFg = typedArray.getColor(R.styleable.CalendarView_weekFg, Color.BLACK);
        daySize = typedArray.getDimension(R.styleable.CalendarView_daySize, 20);
        weekSize = typedArray.getDimension(R.styleable.CalendarView_weekSize, 20);
        needOval = typedArray.getBoolean(R.styleable.CalendarView_needOval, false);
        signBitmap = typedArray.getDrawable(R.styleable.CalendarView_signBitmap);
        onDayBitmap = typedArray.getDrawable(R.styleable.CalendarView_onDayBitmap);
        weekHeight = /* DensityUtils.dp2px(context,*/ (int) typedArray.getDimension(R.styleable.CalendarView_weekHeight, 100);
        lineColor = typedArray.getColor(R.styleable.CalendarView_calendar_lineColor, Color.WHITE);
        typedArray.recycle();

        weekPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weekPaint.setTextSize(weekSize);
        weekPaint.setColor(weekFg);

        weekBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weekBgPaint.setColor(weekBg);

        dayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setColor(noSignFg);
        dayPaint.setTextSize(daySize);
        dayPaint.setTextAlign(Paint.Align.CENTER);

        otherMonthDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        otherMonthDayPaint.setColor(Color.GRAY);
        otherMonthDayPaint.setTextSize(daySize);
        otherMonthDayPaint.setTextAlign(Paint.Align.CENTER);

        signOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        signOvalPaint.setColor(signFg);
        signOvalPaint.setStyle(Paint.Style.STROKE);

        ovalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ovalPaint.setColor(weekBg);
        ovalPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);

    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        minColu = viewWidth / 7;
        minRow = (viewHeight - weekHeight) / 6;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWeekBg(canvas);
        drawWeekSplitLine(canvas);
//        drawSplitLine(canvas);
        drawWeek(canvas);
        drawDays(canvas);

    }

    /**
     * 绘制日期
     *
     * @param canvas
     */
    private void drawDays(Canvas canvas) {
        DateHandle dateHandle = new DateHandle(System.currentTimeMillis());
//        DateHandle dateHandle = new DateHandle(1547053261000l);

        List<SignDate> lastMonthDays = null;
        List<SignDate> nextMonthDays = null;
        List<SignDate> data = dateHandle.getData(signDays);
        for (SignDate signDate : data) {

            if (signDate.getDay() == 1) {
                lastMonthDays = dateHandle.getLastMonthDays();
            }

            if (signDate.getDay() == data.size()) {
                nextMonthDays = dateHandle.getNextMonthDays();
            }

            drawDates(canvas, signDate, dayPaint);
        }
        if (lastMonthDays != null)
            for (SignDate signDate : lastMonthDays) {
                drawDates(canvas, signDate, otherMonthDayPaint);
            }
        if (nextMonthDays != null)
            for (SignDate signDate : nextMonthDays) {
                drawDates(canvas, signDate, otherMonthDayPaint);
            }
    }

    /**
     * 真正绘制不同状态日期地方
     *
     * @param canvas
     * @param signDate
     * @param paint
     */
    private void drawDates(Canvas canvas, SignDate signDate, Paint paint) {
        //日期
        Rect trect = new Rect();
        paint.getTextBounds(String.valueOf(signDate.getDay()), 0, String.valueOf(signDate.getDay()).length(), trect);
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int deltaTextCellY = (fontMetricsInt.bottom + fontMetricsInt.top) / 2;
        textY = weekHeight + (signDate.getRowIndex()) * minRow + minRow / 2 - deltaTextCellY;
        textX = (signDate.getColuIndex()) * minColu + (minColu) / 2;
        String text = String.valueOf(signDate.getDay());

        //图片
        int bitmapHeight;
        int bitmapWidth;
        if (minColu > minRow) {
            bitmapHeight = bitmapWidth = minRow - 10;
        } else bitmapHeight = bitmapWidth = minColu - 10;


        Rect rect = new Rect((signDate.getColuIndex()) * minColu + (minColu - bitmapWidth) / 2
                , (signDate.getRowIndex()) * minRow + weekHeight + (minRow - bitmapHeight) / 2
                , (signDate.getColuIndex() + 1) * minColu - (minColu - bitmapWidth) / 2
                , (signDate.getRowIndex() + 1) * minRow + weekHeight - (minRow - bitmapHeight) / 2);


        if (needOval) canvas.drawOval(rectF, signOvalPaint);//画圆

        if (signDate.getDateType() == DateType.SIGNED) {

            signBitmap.setBounds(rect);
            signBitmap.draw(canvas);

        } else if (signDate.getDateType() == DateType.UNSIGNED) {

            canvas.drawText(text, textX, textY, paint);
        } else if (signDate.getDateType() == DateType.WAITING) {

            onDayBitmap.setBounds(rect);
            onDayBitmap.draw(canvas);
            canvas.drawText(text, textX, textY, paint);
        }


    }

    /**
     * 绘制星期数
     *
     * @param canvas
     */

    private void drawWeek(Canvas canvas) {
        for (int i = 0; i < week.length; i++) {
            canvas.drawText(week[i], (i) * minColu + (minColu / 2 - weekSize / 2), weekHeight / 2 + weekSize / 2, weekPaint);
        }
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */

    private void drawSplitLine(Canvas canvas) {
        Paint splitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        splitPaint.setColor(Color.BLUE);
        for (int i = 1; i < week.length; i++) {
            canvas.drawLine((i) * minColu, weekHeight, (i) * minColu, getMeasuredHeight(), splitPaint);
        }
        for (int i = 1; i < 7; i++) {
            canvas.drawLine(0, weekHeight + i * minRow, getMeasuredWidth(), weekHeight + i * minRow, splitPaint);
        }
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */

    private void drawWeekSplitLine(Canvas canvas) {
        for (int i = 1; i < week.length; i++) {
            canvas.drawLine((i) * minColu, 0, (i) * minColu, weekHeight, linePaint);
        }

    }

    /**
     * 绘制星期背景
     *
     * @param canvas
     */
    private void drawWeekBg(Canvas canvas) {
        Rect rect = new Rect(0, 0, viewWidth, weekHeight);
        canvas.drawRect(rect, weekBgPaint);
    }

    /**
     * 设置已签到日期
     *
     * @param signDays
     */
    public void setSignDays(Set<Integer> signDays) {
        this.signDays = signDays;
        invalidate(0, viewHeight - minRow, viewWidth, viewHeight);
//        invalidate();
    }

    public void setTime(int day) {
        if (signDays != null)
            signDays.add(day);
        else {
            signDays = new HashSet<>();
            signDays.add(day);
        }
        invalidate(0, viewHeight - minRow, viewWidth, viewHeight);
//        invalidate();
    }

    private int dp2px(float dpValue) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
