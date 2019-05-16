package com.cestco.viewlib.PayKeyBoard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.cestco.viewlib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */

public class PSBoxView extends View {

  private final Context mContext;
  private int lineWidth;

  private int lineColor;
  private int viewWidth;
  private int viewHeight;
  private int boxWidth;


  private int position = 0;

  //输入的数值
  private List<Integer> passwords = new ArrayList<>();
  //圆形边框半径
  private float radius;

  public PSBoxView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    initAttrs(context, attrs);
  }

  private void initAttrs(Context context, AttributeSet attrs) {

    lineWidth = 2;
    lineColor = Color.GRAY;
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PSBoxView);
    int indexCount = typedArray.getIndexCount();

    lineColor = typedArray.getColor(R.styleable.PSBoxView_lineColor, lineColor);

    lineWidth = (int) typedArray.getDimension(R.styleable.PSBoxView_linesWidth, lineWidth);

    radius = typedArray.getDimension(R.styleable.PSBoxView_rect_radius, radius);


    typedArray.recycle();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    viewWidth = w;
    viewHeight = h;
    if (w > 0) {
      boxWidth = w / 6;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {

    drawBox(canvas);
    drawBlackPoint(canvas);
  }

  /**
   * 绘制黑点
   *
   * @param canvas
   */
  private void drawBlackPoint(Canvas canvas) {
    Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    pointPaint.setColor(Color.BLACK);
    for (int i = 1; i <= position; i++) {
      canvas.drawCircle(boxWidth / 2 + (i - 1) * boxWidth, viewHeight / 2,
          boxWidth / 10, pointPaint);
    }
  }

  /**
   * 画边框
   *
   * @param canvas
   */
  private void drawBox(Canvas canvas) {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStrokeWidth(lineWidth);
    paint.setColor(lineColor);
    RectF rectF = new RectF(0, 0, viewWidth, viewHeight);
    canvas.drawRoundRect(rectF, radius, radius, paint);
    //上边界
//        canvas.drawLine(0 ,0,viewWidth ,0 ,paint);
    //下边界
//        canvas.drawLine(0,viewHeight ,viewWidth ,viewHeight,paint);
    Paint bgPaint = new Paint();
    bgPaint.setColor(Color.WHITE);
    RectF bgRectF = new RectF(lineWidth, lineWidth, viewWidth - lineWidth, viewHeight - lineWidth);
    canvas.drawRoundRect(bgRectF, radius, radius, bgPaint);
    //竖线
    for (int i = 1; i < 6; i++) {
      canvas.drawLine(i * boxWidth, 0, i * boxWidth, viewHeight, paint);
    }

  }

  public void setPS(boolean isAdd, int num) {
    if (isAdd && position < 6) {
      position++;
      passwords.add(num);
    } else if (!isAdd && position > 0) {
      position--;
      passwords.remove(passwords.size() - 1);
    }
    if (position >= 0 && position <= 6)
      invalidate();
  }

  public int getPosition() {
    return position;
  }

  public String getPS() {
    StringBuffer stringBuffer = new StringBuffer();
    for (Integer integer : passwords) {
      stringBuffer.append(integer);
    }
    return stringBuffer.toString();
  }

  public void clearAll() {
    position = 0;
    passwords.clear();
    invalidate();
  }
}
