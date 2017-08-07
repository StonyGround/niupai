package com.xiuxiu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiuxiu.R;

import java.math.BigDecimal;

/**
 * Created by hzdykj on 2017/7/6.
 */

public class DoubleSeekBar<T extends Number> extends ImageView {
    private static final String TAG = DoubleSeekBar.class.getSimpleName();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbValuePaint = getThumbValuePaint();
    private final Bitmap thumbImageLeft = BitmapFactory.decodeResource(getResources(), R.drawable.slide_left);
    private final Bitmap thumbImageRight = BitmapFactory.decodeResource(getResources(), R.drawable.slide_right);

    private final float thumbWidth = thumbImageLeft.getWidth();
    private final float thumbHeight = thumbImageLeft.getHeight();
    private final float thumbHalfWidth = 0.5f * thumbWidth;
    private final float thumbHalfHeight = 0.5f * thumbHeight;
    private final float padding = thumbHalfWidth;
    private final T absoluteMinValue, absoluteMaxValue;
    private final int seekbarHeight;
    private float bottom;
    private int seekbar = 15;
    private float seekbarLeft = 10;
    //    private final T seekWidth, seekHeight;
    private final NumberType numberType;
    private final double absoluteMinValuePrim, absoluteMaxValuePrim;
    private double normalizedMinValue = 0;
    private double normalizedMaxValue = 1;
    private Thumb pressedThumb = null;
    private boolean notifyWhileDragging = false;
    private OnRangeSeekBarChangeListener<T> listener;

    public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);

    public static final int INVALID_POINTER_ID = 255;

    public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00,
            ACTION_POINTER_INDEX_SHIFT = 8;

    private float mDownMotionX;
    private int mActivePointerId = INVALID_POINTER_ID;

    private int mScaledTouchSlop;
    private boolean mIsDragging;

    private boolean isDrawIcon = true;

    public void setDrawIcon(boolean drawIcon) {
        isDrawIcon = drawIcon;
        invalidate();
    }

    private boolean isClear = false;

    public void setClear(boolean clear) {
        isClear = clear;
        invalidate();
    }

    // 创建一个画笔
    private Paint mPaint = new Paint();

    public DoubleSeekBar(T absoluteMinValue, T absoluteMaxValue, int seekbarHeight, Context context) throws IllegalArgumentException {
        super(context);
        this.absoluteMinValue = absoluteMinValue;
        this.absoluteMaxValue = absoluteMaxValue;
//        this.seekWidth = seekWidth;
//        this.seekHeight = seekHeight;
        this.seekbarHeight = seekbarHeight;
        bottom = (float) seekbarHeight;
        Log.e("DoubleSeekBar", bottom + "----bottom---");
        absoluteMinValuePrim = absoluteMinValue.doubleValue();
        absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
        numberType = NumberType.fromNumber(absoluteMinValue);

        setFocusable(true);
        setFocusableInTouchMode(true);
        init();
    }

    private final void init() {
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 画矩形
     */
    private void initPaints(Canvas canvas, RectF rectF, int color, float width, int alpha) {
        mPaint.setColor(color);           //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);//设置画笔模式为填充
        mPaint.setStrokeWidth(width);     //设置画笔宽度为
        mPaint.setAlpha(alpha);           //设置画笔透明度
        canvas.drawRect(rectF, mPaint);   //绘制矩形
    }

    public int getSeekbarHeight() {
        return seekbarHeight;
    }

    public boolean isNotifyWhileDragging() {
        return notifyWhileDragging;
    }

    public void setNotifyWhileDragging(boolean flag) {
        this.notifyWhileDragging = flag;
    }

    public T getAbsoluteMinValue() {
        return absoluteMinValue;
    }

    public T getAbsoluteMaxValue() {
        return absoluteMaxValue;
    }

//    public T getSeekWidth() {
//        return seekWidth;
//    }
//
//    public T getSeekHeight() {
//        return seekHeight;
//    }

    public T getSelectedMinValue() {
        return normalizedToValue(normalizedMinValue);
    }

    public void setSelectedMinValue(T value) {

        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {

            setNormalizedMinValue(0);
        } else {
            setNormalizedMinValue(valueToNormalized(value));
        }
    }

    public T getSelectedMaxValue() {
        return normalizedToValue(normalizedMaxValue);
    }

    public void setSelectedMaxValue(T value) {
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMaxValue(1);
        } else {
            setNormalizedMaxValue(valueToNormalized(value));
        }
    }

    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
        this.listener = listener;
    }

    public static void drawRoundRect(DrawingCanvas canvas, int color, int alpha) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setAlpha(alpha);
        canvas.drawRoundRect(canvas.getRectF(), 20, 20, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled())
            return false;

        int pointerIndex;

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                int xs = (int) event.getX();
                int ys = (int) event.getY();
                Log.e(TAG, "xs = " + xs + "--" + "ys = " + ys + "--");
                mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownMotionX = event.getX(pointerIndex);
                pressedThumb = evalPressedThumb(mDownMotionX);
                if (pressedThumb == null)
                    return super.onTouchEvent(event);
                setPressed(true);
                invalidate();
                Log.e(TAG, "mActivePointerId = " + mActivePointerId + "--" + "pointerIndex = " +
                        pointerIndex + "--" + "mDownMotionX = " + mDownMotionX + "--" + "pressedThumb =" + pressedThumb);
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag();
                break;
            case MotionEvent.ACTION_MOVE:
                if (pressedThumb != null) {
                    if (mIsDragging) {
                        trackTouchEvent(event);
                    } else {
                        pointerIndex = event.findPointerIndex(mActivePointerId);
                        final float x = event.getX(pointerIndex);
                        Log.e(TAG, "pressedThumb = " + pressedThumb + "--" + "pointerIndex = " +
                                pointerIndex + "--");
                        if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
                            setPressed(true);
                            Log.e(TAG, "-------");
                            invalidate();
                            Log.e(TAG, "mDownMotionX = " + mDownMotionX + "--" + "mScaledTouchSlop =" + mScaledTouchSlop);
                            onStartTrackingTouch();
                            trackTouchEvent(event);
                            attemptClaimDrag();
                        }
                    }
                    if (notifyWhileDragging && listener != null) {
                        listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
                        Log.e(TAG, "onRangeSeekBarValuesChanged = " + getSelectedMinValue() + "-----" + getSelectedMaxValue());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
                pressedThumb = null;
                invalidate();
                if (listener != null) {
                    listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
                    Log.e(TAG, "onRangeSeekBarValuesChanged-listener = " + getSelectedMinValue() + "-----" + getSelectedMaxValue());
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = event.getPointerCount() - 1;
                mDownMotionX = event.getX(index);
                mActivePointerId = event.getPointerId(index);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate();
                break;
        }
        return true;
    }

    private final void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mDownMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private final void trackTouchEvent(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        final float x = event.getX(pointerIndex);

        if (Thumb.MIN.equals(pressedThumb)) {
            setNormalizedMinValue(screenToNormalized(x));
        } else if (Thumb.MAX.equals(pressedThumb)) {
            setNormalizedMaxValue(screenToNormalized(x));
        }
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    void onStartTrackingTouch() {
        mIsDragging = true;
    }

    void onStopTrackingTouch() {
        mIsDragging = false;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 200;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = seekbarHeight - seekbar;
//        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
//            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec)) + (int) getFontHeight(thumbValuePaint)
//                    * 3;
//        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*Bitmap m_progress = BitmapFactory.decodeResource(getResources(), R.drawable.red_seekbar);
        float rangeL = normalizedToScreen(normalizedMinValue);
        float rangeR = normalizedToScreen(normalizedMaxValue);
        Log.e(TAG, "rangeL = " + rangeL + "-------" +"rangeR = " + rangeR);
        float pro_scale = (rangeR - rangeL) / m_progress.getWidth(); //中间缩放比例
        Log.e(TAG, "pro_scale = " + pro_scale + "-----");
        if (pro_scale > 0) {
            Matrix pro_mx = new Matrix();
            pro_mx.postScale(pro_scale, 1f);
            try {
                Bitmap m_progress_new = Bitmap.createBitmap(m_progress, 0, 0, m_progress.getWidth(),
                        m_progress.getHeight(), pro_mx, true);
                canvas.drawBitmap(m_progress_new, rangeL, 0.5f * (getHeight() - m_progress.getHeight()), paint);
            } catch (Exception e) {
                Log.e(TAG,
                        "IllegalArgumentException--width=" + m_progress.getWidth() + "Height=" + m_progress.getHeight()
                                + "pro_scale=" + pro_scale, e);
            }
        }*/

        //绘画左右两个游标
//        drawThumbMinValue(normalizedToScreen(normalizedMinValue), "", canvas);
//
//        drawThumbMaxValue(normalizedToScreen(normalizedMaxValue), "", canvas);

        if (!isClear) {
            drawThumbSelectArea(normalizedToScreen(normalizedMinValue), normalizedToScreen(normalizedMaxValue), canvas);

            if (isDrawIcon) {

                drawThumbLeft(normalizedToScreen(normalizedMinValue), canvas, thumbImageLeft);

                drawThumbRight(normalizedToScreen(normalizedMaxValue), canvas, thumbImageRight);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("SUPER", super.onSaveInstanceState());
        bundle.putDouble("MIN", normalizedMinValue);
        bundle.putDouble("MAX", normalizedMaxValue);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        final Bundle bundle = (Bundle) parcel;
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
        normalizedMinValue = bundle.getDouble("MIN");
        normalizedMaxValue = bundle.getDouble("MAX");
    }

    private void drawThumbSelectArea(float rangeL, float rangeR, Canvas canvas) {
        Log.e(TAG, "rangeL = " + rangeL + "-------" + "rangeR = " + rangeR);
        Log.e(TAG, "onDraw=" + bottom);
        try {
            RectF rectF = new RectF(rangeL, 0, rangeR, bottom);
            initPaints(canvas, rectF, Color.parseColor("#ACA890"), 10f, 110);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawThumbLeft(float screenCoord, Canvas canvas, Bitmap thumbImageLeft) {
        try {
            RectF rectF = new RectF((screenCoord - thumbHalfWidth) - seekbarLeft, 0,
                    (screenCoord + thumbHalfWidth) + seekbarLeft, bottom);
            initPaints(canvas, rectF, Color.parseColor("#FFD201"), 10f, 255);
            canvas.drawBitmap(thumbImageLeft, screenCoord - thumbHalfWidth,
                    (float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawThumbRight(float screenCoord, Canvas canvas, Bitmap thumbImageRight) {
        //#FFD201  0xFFFFD201
        //#ACA890  0xFFACA890
        try {
            RectF rectF = new RectF((screenCoord - thumbHalfWidth) - seekbarLeft, 0,
                    (screenCoord + thumbHalfWidth) + seekbarLeft, bottom);
            initPaints(canvas, rectF, Color.parseColor("#FFD201"), 10f, 255);
            canvas.drawBitmap(thumbImageRight, screenCoord - thumbHalfWidth,
                    (float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制左游标
     *
     * @param screenCoord
     * @param text
     * @param canvas
     */
    private void drawThumbMinValue(float screenCoord, String text, Canvas canvas) {

        // 右游标的起始点
        float maxThumbleft = normalizedToScreen(normalizedMaxValue) - thumbHalfWidth;

        // 游标区域的右边界位置
        float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

        if (textRight >= maxThumbleft) {
            // 左游标与右游标重叠
            if (pressedThumb == Thumb.MIN) {
                canvas.drawText(text, maxThumbleft - getFontlength(thumbValuePaint, text) - 2,
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);

            } else {
                canvas.drawText(text, textRight - getFontlength(thumbValuePaint, text) - 2,
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
            }

        } else {
            // 正常情况
            canvas.drawText(text, screenCoord - thumbHalfWidth - 2, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
                    thumbValuePaint);

        }

    }

    /**
     * 绘制右游标
     *
     * @param screenCoord
     * @param text
     * @param canvas
     */
    private void drawThumbMaxValue(float screenCoord, String text, Canvas canvas) {

        // 左游标的右边界
        float minThumbValueRight = normalizedToScreen(normalizedMinValue) - thumbHalfWidth
                + getFontlength(thumbValuePaint, "----" + getSelectedMinValue());

        // 游标区域的右边界位置
        float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

        if (textRight >= getWidth()) {
            // 右边界超出or等于seekbar宽度
            canvas.drawText(text, getWidth() - getFontlength(thumbValuePaint, text) - 1,
                    (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);

        } else if ((screenCoord - thumbHalfWidth) <= minThumbValueRight) {
            // 左右游标重叠
            if (pressedThumb == Thumb.MAX) {

                canvas.drawText(text, minThumbValueRight - 1, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
                        thumbValuePaint);

            } else {
                canvas.drawText(text, screenCoord - thumbHalfWidth - 1,
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);

            }

        } else {
            //正常情况
            canvas.drawText(text, screenCoord - thumbHalfWidth - 1, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
                    thumbValuePaint);
        }

    }

    private Paint getThumbValuePaint() {
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        p.setTextSize(25);
        return p;
    }


    private float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    private float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    private Thumb evalPressedThumb(float touchX) {
        Thumb result = null;
        boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
        boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
        if (minThumbPressed && maxThumbPressed) {
            result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
        } else if (minThumbPressed) {
            result = Thumb.MIN;
        } else if (maxThumbPressed) {
            result = Thumb.MAX;
        }
        return result;
    }

    private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth + 20;
    }


    public void setNormalizedMinValue(double value) {
        normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
        invalidate();
    }

    public void setNormalizedMaxValue(double value) {
        normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
        invalidate();
    }

    @SuppressWarnings("unchecked")
    private T normalizedToValue(double normalized) {
        return (T) numberType.toNumber(absoluteMinValuePrim + normalized
                * (absoluteMaxValuePrim - absoluteMinValuePrim));

    }

    private double valueToNormalized(T value) {
        if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            // prevent division by zero, simply return 0.
            return 0;
        }
        return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
    }

    private float normalizedToScreen(double normalizedCoord) {

        return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
        // return (float) (normalizedCoord * getWidth());
    }

    private double screenToNormalized(float screenCoord) {
        int width = getWidth();
        if (width <= 2 * padding) {
            return 0;
        } else {
            double result = (screenCoord - padding) / (width - 2 * padding);
            return Math.min(1d, Math.max(0, result));
        }
    }

    public interface OnRangeSeekBarChangeListener<T> {
        public void onRangeSeekBarValuesChanged(DoubleSeekBar<?> bar, T minValue, T maxValue);
    }

    private static enum Thumb {
        MIN, MAX
    }

    private static enum NumberType {
        LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

        public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException {
            if (value instanceof Long) {
                return LONG;
            }
            if (value instanceof Double) {
                return DOUBLE;
            }
            if (value instanceof Integer) {
                return INTEGER;
            }
            if (value instanceof Float) {
                return FLOAT;
            }
            if (value instanceof Short) {
                return SHORT;
            }
            if (value instanceof Byte) {
                return BYTE;
            }
            if (value instanceof BigDecimal) {
                return BIG_DECIMAL;
            }
            throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
        }

        public Number toNumber(double value) {

            switch (this) {
                case LONG:
                    return new Long((long) value);
                case DOUBLE:
                    return value;
                case INTEGER:
                    return new Integer((int) value);
                case FLOAT:
                    return new Float(value);
                case SHORT:
                    return new Short((short) value);
                case BYTE:
                    return new Byte((byte) value);
                case BIG_DECIMAL:
                    return new BigDecimal(value);
            }
            throw new InstantiationError("can't convert " + this + " to a Number object");
        }
    }
}