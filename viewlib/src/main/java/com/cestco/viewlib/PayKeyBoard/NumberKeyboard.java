package com.cestco.viewlib.PayKeyBoard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.cestco.viewlib.R;
import com.wdy.common.utils.DensityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wdy on 2017/11/13.
 */

public class NumberKeyboard extends View {


  private int viewWidth;
  private int viewHeight;

  private int rectWidth;
  private int rectHeight;

  private Context mContext;

  private boolean isLine;
  /**
   * 是否随机数字
   */
  private boolean isRom;
  /**
   * 相邻按钮之间距离
   */
  private int distance;
  private int boundColor;
  private int cutLineColor;
  private int numBg;
  /**
   * 删除按钮背景色
   */
  private int clearBg;
  private Rect rectClear;
  private List<String> nums;
  /**
   * 删除按键图标
   */
  private Drawable delDrawable;
  private float numSize;
  /**
   * 按键圆角
   */
  private float numRound;
  /**
   * 是否需要上边界
   */
  private boolean isNeedTopBound;
  private int clickNum = -1;
  private int numClickBg;
  private int clearClickBg;
  private Paint numPaint;


  public NumberKeyboard(Context context) {
    super(context);
  }

  public NumberKeyboard(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.mContext = context;
    initAttrs(context, attrs);
  }

  private void initAttrs(Context context, AttributeSet attrs) {
    distance = 0;
    boundColor = Color.GRAY;
    cutLineColor = Color.BLACK;
    numBg = Color.WHITE;
    clearBg = Color.LTGRAY;
    numSize = 40;
    numClickBg = Color.DKGRAY;
    clearClickBg = Color.GRAY;
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberKeyboard);
    int indexCount = typedArray.getIndexCount();

    boundColor = typedArray.getColor(R.styleable.NumberKeyboard_boundLineColor, boundColor);

    clearBg = typedArray.getColor(R.styleable.NumberKeyboard_clearBg, clearBg);

    clearClickBg = typedArray.getColor(R.styleable.NumberKeyboard_clearClickBg, clearClickBg);

    isLine = typedArray.getBoolean(R.styleable.NumberKeyboard_isLine, isLine);

    distance = (int) typedArray.getDimension(R.styleable.NumberKeyboard_distance, distance);

    cutLineColor = typedArray.getColor(R.styleable.NumberKeyboard_cutLineColor, cutLineColor);

    isRom = typedArray.getBoolean(R.styleable.NumberKeyboard_isRom, isRom);

    numBg = typedArray.getColor(R.styleable.NumberKeyboard_numberBg, numBg);

    numClickBg = typedArray.getColor(R.styleable.NumberKeyboard_numberClickBg, numClickBg);

    delDrawable = typedArray.getDrawable(R.styleable.NumberKeyboard_delIcon);

    numRound = typedArray.getDimension(R.styleable.NumberKeyboard_numRound, numRound);

    numSize = typedArray.getDimension(R.styleable.NumberKeyboard_numSize, numSize);

    isNeedTopBound = typedArray.getBoolean(R.styleable.NumberKeyboard_isNeedTopBound, isNeedTopBound);

    typedArray.recycle();

    if (!isLine && distance == 0) {
      distance = 2;
    } else if (isLine) {
      distance = 0;
      numRound = 0;
    }
    initNum();
  }

  private void initNum() {
    numPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    numPaint.setColor(Color.BLACK);
    numPaint.setTextSize(DensityUtils.dp2px(mContext, numSize));
    nums = new ArrayList<>();
    for (int i = 1; i < 10; i++) {
      nums.add(i + "");
    }
    nums.add("0");

  }

  public void setLine(boolean line) {
    isLine = line;
  }

  public void setRom(boolean rom) {
    isRom = rom;
    if (isRom) {
      Collections.shuffle(nums);
    }
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public void setBoundColor(int boundColor) {
    this.boundColor = boundColor;
  }

  public void setCutLineColor(int cutLineColor) {
    this.cutLineColor = cutLineColor;
  }

  public void setNumBg(int numBg) {
    this.numBg = numBg;
  }

  public void setClearBg(int clearBg) {
    this.clearBg = clearBg;
  }

  public void setDelDrawable(Drawable delDrawable) {
    this.delDrawable = delDrawable;
  }

  public void setNumSize(float numSize) {
    this.numSize = numSize;
  }

  public void setNumRound(float numRound) {
    this.numRound = numRound;
  }

  public NumberKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    viewWidth = getWidth();
    viewHeight = getHeight();
    rectWidth = viewWidth / 3;
    rectHeight = viewHeight / 4;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    drawNumRect(canvas);
    drawNum(canvas);
    if (isLine) {//先绘制线段会被覆盖掉
      drawBound(canvas);
      drawCutLine(canvas);
    }
  }

  /**
   * 绘制数字
   *
   * @param canvas
   */
  private void drawNum(Canvas canvas) {


    for (int i = 0; i < nums.size(); i++) {
      switch (i) {
        case 0:
        case 1:
        case 2:
          canvas.drawText(nums.get(i), rectWidth / 2 + (i * rectWidth) - numSize / 2, rectHeight / 2 + numSize / 2, numPaint);
          break;
        case 3:
        case 4:
        case 5:
          canvas.drawText(nums.get(i), rectWidth / 2 + (i - 3) * rectWidth - numSize / 2, rectHeight / 2 * 3 + numSize / 2, numPaint);
          break;
        case 6:
        case 7:
        case 8:
          canvas.drawText(nums.get(i), rectWidth / 2 + (i - 6) * rectWidth - numSize / 2, rectHeight / 2 * 5 + numSize / 2, numPaint);
          break;
        case 9:
          canvas.drawText(nums.get(i), rectWidth / 2 * 3 - numSize / 2, rectHeight / 2 * 7 + numSize / 2, numPaint);
          break;
      }
    }


    if (delDrawable == null)
      return;
    int delWidth = delDrawable.getIntrinsicWidth();
    int delHeight = delDrawable.getIntrinsicHeight();
    if (delHeight > rectHeight) {
      delHeight = rectHeight;
    }
    if (delWidth > rectWidth) {
      delWidth = rectWidth;
    }
    Rect rect = new Rect(rectWidth * 2 + (rectWidth - delWidth) / 2, rectHeight * 3 + (rectHeight - delHeight) / 2,
        rectWidth * 2 + (rectWidth - delWidth) / 2 + delWidth, rectHeight * 3 + (rectHeight - delHeight) / 2 + delHeight);
    delDrawable.setBounds(rect);
    delDrawable.draw(canvas);
    //绘制小数点
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.BLACK);
    canvas.drawCircle(rectWidth / 2, rectHeight / 2 * 7, 10, paint);
  }

  /**
   * 绘制分割线
   *
   * @param canvas
   */
  private void drawCutLine(Canvas canvas) {
    Paint cutLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    cutLinePaint.setColor(cutLineColor);
    cutLinePaint.setStrokeWidth(2);
    canvas.drawLine(rectWidth, 0, rectWidth, viewHeight, cutLinePaint);//左竖线
    canvas.drawLine(rectWidth * 2, 0, rectWidth * 2, viewHeight, cutLinePaint);//右竖线

    canvas.drawLine(0, rectHeight, viewWidth, rectHeight, cutLinePaint);//第一根横线
    canvas.drawLine(0, rectHeight * 2, viewWidth, rectHeight * 2, cutLinePaint);//第二根
    canvas.drawLine(0, rectHeight * 3, viewWidth, rectHeight * 3, cutLinePaint);//第三根
  }

  /**
   * 绘制边界
   *
   * @param canvas
   */
  private void drawBound(Canvas canvas) {
    Paint boundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    boundPaint.setColor(boundColor);
    boundPaint.setStrokeWidth(2);
    if (isNeedTopBound)
      canvas.drawLine(0, 0, viewWidth, 0, boundPaint);//上边界
    canvas.drawLine(0, 0, 0, viewHeight, boundPaint);//左边界
    canvas.drawLine(0, viewHeight, viewWidth, viewHeight, boundPaint);//下边界
    canvas.drawLine(viewWidth, 0, viewWidth, viewHeight, boundPaint);//右边界
  }

  /**
   * 绘制数字区域
   *
   * @param canvas
   */
  private void drawNumRect(Canvas canvas) {
    Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    Rect rect1 = new Rect(distance, distance, rectWidth - distance, rectHeight - distance);
    Rect rect2 = new Rect(rectWidth + distance, distance, rectWidth * 2 - distance, rectHeight - distance);
    Rect rect3 = new Rect(rectWidth * 2 + distance, distance, viewWidth - distance, rectHeight - distance);

    Rect rect4 = new Rect(distance, rectHeight + distance, rectWidth - distance, rectHeight * 2 - distance);
    Rect rect5 = new Rect(rectWidth + distance, rectHeight + distance, rectWidth * 2 - distance, rectHeight * 2 - distance);
    Rect rect6 = new Rect(rectWidth * 2 + distance, rectHeight + distance, viewWidth - distance, rectHeight * 2 - distance);

    Rect rect7 = new Rect(distance, rectHeight * 2 + distance, rectWidth - distance, rectHeight * 3 - distance);
    Rect rect8 = new Rect(rectWidth + distance, rectHeight * 2 + distance, rectWidth * 2 - distance, rectHeight * 3 - distance);
    Rect rect9 = new Rect(rectWidth * 2 + distance, rectHeight * 2 + distance, viewWidth - distance, rectHeight * 3 - distance);

    Rect rectBlank = new Rect(distance, rectHeight * 3 + distance, rectWidth - distance, viewHeight - distance);
    Rect rect0 = new Rect(rectWidth + distance, rectHeight * 3 + distance, rectWidth * 2 - distance, viewHeight - distance);
    rectClear = new Rect(rectWidth * 2 + distance, rectHeight * 3 + distance, viewWidth - distance, viewHeight - distance);

    switch (clickNum) {
      case 1:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);

        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);
        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 2:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 3:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 4:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 5:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 6:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 7:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 8:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 9:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case 0:
        rectPaint.setColor(numClickBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case -1:
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case -2:
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearClickBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
      case -3:
        rectPaint.setColor(numBg);
        canvas.drawRoundRect(new RectF(rect0), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect1), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect2), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect3), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect4), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect5), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect6), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect7), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect8), numRound, numRound, rectPaint);
        canvas.drawRoundRect(new RectF(rect9), numRound, numRound, rectPaint);

        rectPaint.setColor(clearBg);
        canvas.drawRoundRect(new RectF(rectClear), numRound, numRound, rectPaint);
        rectPaint.setColor(clearClickBg);
        canvas.drawRoundRect(new RectF(rectBlank), numRound, numRound, rectPaint);
        break;
    }

  }

  /**
   * 点击事件
   *
   * @param event
   * @return
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        float x = event.getX();
        float y = event.getY();
        dragLocation(x, y);
        invalidate();
        break;
      case MotionEvent.ACTION_UP:
        clickNum = -1;
        invalidate();
        break;
    }
    return true;
  }

  /**
   * 判断点击区域
   *
   * @param x
   * @param y
   */
  private void dragLocation(float x, float y) {
    if (x < 0 || y < 0 || x > viewWidth || y > viewHeight) return;
    int num = -1;
    if (y < rectHeight - distance && y > distance) {
      if (x < rectWidth - distance && x > distance) {
        num = Integer.parseInt(nums.get(0));
        clickNum = 1;
      } else if (x < rectWidth * 2 - distance && x > rectWidth + distance) {
        num = Integer.parseInt(nums.get(1));
        clickNum = 2;
      } else if (viewWidth - distance > x && x > 2 * rectWidth + distance) {
        num = Integer.parseInt(nums.get(2));
        clickNum = 3;
      }
    } else if (y < 2 * rectHeight - distance && y > rectHeight + distance) {
      if (x < rectWidth - distance && x > distance) {
        num = Integer.parseInt(nums.get(3));
        clickNum = 4;
      } else if (x < rectWidth * 2 - distance && x > rectWidth + distance) {
        num = Integer.parseInt(nums.get(4));
        clickNum = 5;
      } else if (viewWidth - distance > x && x > 2 * rectWidth + distance) {
        num = Integer.parseInt(nums.get(5));
        clickNum = 6;
      }
    } else if (y < 3 * rectHeight && y > 2 * rectHeight + distance) {
      if (x < rectWidth - distance && x > distance) {
        num = Integer.parseInt(nums.get(6));
        clickNum = 7;
      } else if (x < rectWidth * 2 - distance && x > rectWidth + distance) {
        num = Integer.parseInt(nums.get(7));
        clickNum = 8;
      } else if (viewWidth - distance > x && x > 2 * rectWidth + distance) {
        num = Integer.parseInt(nums.get(8));
        clickNum = 9;
      }

    } else if (y < 4 * rectHeight && y > 3 * rectHeight + distance) {
      if (x < rectWidth - distance && x > distance) {
        num = -1;//无效点击
        clickNum = -3;
      } else if (x < rectWidth * 2 - distance && x > rectWidth + distance) {
        num = Integer.parseInt(nums.get(9));
        clickNum = 0;
      } else if (viewWidth - distance > x && x > 2 * rectWidth + distance) {
        clickNum = -2;
        num = -2;//删除按钮
      }

    }

    if (onNumKeyClickListener != null && num != -1) {
      onNumKeyClickListener.click(num);
    }

  }

  public interface OnNumKeyClickListener {
    void click(int num);
  }

  OnNumKeyClickListener onNumKeyClickListener;

  public void setOnNumKeyClickListener(OnNumKeyClickListener onNumKeyClickListener) {
    this.onNumKeyClickListener = onNumKeyClickListener;
  }
}
