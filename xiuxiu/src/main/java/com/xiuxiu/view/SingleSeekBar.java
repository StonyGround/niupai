package com.xiuxiu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.xiuxiu.R;

import java.math.BigDecimal;

/**
 * Created by hzdykj on 2017/7/6.
 */

public class SingleSeekBar<T extends Number> extends ImageView {
    private static final String TAG = SingleSeekBar.class.getSimpleName();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbValuePaint = getThumbValuePaint();
//    private final Bitmap thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.thumb_normal);

    private final float thumbWidth = 12;
    private final float thumbHalfWidth = 0.5f * thumbWidth;
    private final float thumbHalfHeight;
    private final float padding = thumbHalfWidth;
    private final T absoluteMinValue, absoluteMaxValue;
    private final int seekbarHeight;
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

    private RectF seekbarRectF;


    // 创建一个画笔
    private Paint mPaint = new Paint();

    public SingleSeekBar(T absoluteMinValue, T absoluteMaxValue, int seekbarHeight, Context context) throws IllegalArgumentException {
        super(context);
        this.absoluteMinValue = absoluteMinValue;
        this.absoluteMaxValue = absoluteMaxValue;
        this.seekbarHeight = seekbarHeight;
        absoluteMinValuePrim = absoluteMinValue.doubleValue();
        absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
        numberType = NumberType.fromNumber(absoluteMinValue);
        thumbHalfHeight = seekbarHeight >> 1;

        setFocusable(true);
        setFocusableInTouchMode(true);
        init();
        seekbarRectF = new RectF();
    }

    private final void init() {
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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
            setNormalizedMaxValue(1d);
        } else {
            setNormalizedMaxValue(valueToNormalized(value));
        }
    }

    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled())
            return false;

        int pointerIndex;

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownMotionX = event.getX(pointerIndex);
                pressedThumb = evalPressedThumb(mDownMotionX);
                if (pressedThumb == null)
                    return super.onTouchEvent(event);
                setPressed(true);
                invalidate();
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag();
                Log.e(TAG, "mActivePointerId-2 = " + mActivePointerId + "--" + "pointerIndex = " +
                        pointerIndex + "--" + "mDownMotionX = " + mDownMotionX + "--" + "pressedThumb =" + pressedThumb);
                break;
            case MotionEvent.ACTION_MOVE:
                if (pressedThumb != null) {
                    if (mIsDragging) {
                        trackTouchEvent(event);
                    } else {
                        pointerIndex = event.findPointerIndex(mActivePointerId);
                        final float x = event.getX(pointerIndex);
                        if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
                            setPressed(true);
                            Log.e(TAG, "---------");
                            invalidate();
                            onStartTrackingTouch();
                            trackTouchEvent(event);
                            attemptClaimDrag();
                        }
                    }
                    if (notifyWhileDragging && listener != null) {
                        listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
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
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = event.getPointerCount() - 1;
                // final int index = ev.getActionIndex();
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
            case MotionEvent.ACTION_OUTSIDE:
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
            // screenToNormalized(x)-->
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
        int height = seekbarHeight - 18;
////        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
////            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec)) + (int) getFontHeight(thumbValuePaint)
////                    * 3;
////        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawThumbMinValue(normalizedToScreen(normalizedMinValue), "", canvas);

        drawThumb(normalizedToScreen(normalizedMinValue), canvas);

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

    /**
     * 画矩形
     */
    private void initPaints(Canvas canvas, RectF rectF, int color, float width, int alpha) {
//        pATG
        mPaint.setColor(color);           //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);//设置画笔模式为填充
        mPaint.setStrokeWidth(width);     //设置画笔宽度为
        mPaint.setAlpha(alpha);           //设置画笔透明度
        mPaint.setAntiAlias(true);        //设置画笔的锯齿效果
//        canvas.drawRect(rectF, mPaint);   //绘制矩形
        canvas.drawRoundRect(rectF, 30, 35, mPaint);//画圆角矩形
//        canvas.clipPath()
    }

    private void drawThumb(float screenCoord, Canvas canvas) {


        float bottom = seekbarHeight;
//        seekbarRectF = new RectF(screenCoord - thumbHalfWidth, 2, screenCoord + thumbHalfWidth, bottom);
        seekbarRectF.left = screenCoord - thumbHalfWidth;
        seekbarRectF.top = 2;
        seekbarRectF.right = screenCoord + thumbHalfWidth;
        seekbarRectF.bottom = bottom;

//        Log.i(TAG, "thumbHalfWidth = " + thumbHalfWidth + "-----" + screenCoord);
        initPaints(canvas, seekbarRectF, Color.WHITE, 10f, 255);
//        canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord - thumbHalfWidth,
//                (float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
    }

    private void drawThumbMinValue(float screenCoord, String text, Canvas canvas) {

        float maxThumbleft = getWidth();

        float textRight = screenCoord - thumbHalfWidth + getFontlength(thumbValuePaint, text);

        if (textRight >= maxThumbleft) {
            if (pressedThumb == Thumb.MIN) {
                canvas.drawText(text, maxThumbleft - getFontlength(thumbValuePaint, text) - 3,
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
            } else {
                canvas.drawText(text, textRight - getFontlength(thumbValuePaint, text) - 3,
                        (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3, thumbValuePaint);
            }
            Log.e(TAG, "textRight>= maxThumbleft----textRight=" + textRight + "maxThumbleft=" + maxThumbleft);
        } else {
            canvas.drawText(text, screenCoord - thumbHalfWidth - 3, (float) ((0.4f * getHeight()) - thumbHalfHeight) - 3,
                    thumbValuePaint);
            Log.i(TAG, "textRight < maxThumbleft----textRight= " + textRight + "maxThumbleft = " + maxThumbleft);
        }
    }

    private Paint getThumbValuePaint() {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
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

    private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth + 20;
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

    public void setNormalizedMinValue(double value) {
        normalizedMinValue = Math.max(0, Math.min(1, Math.min(value, normalizedMaxValue)));
        invalidate();
    }

    public void setNormalizedMaxValue(double value) {
        normalizedMaxValue = Math.max(0, Math.min(1, Math.max(value, normalizedMinValue)));
        invalidate();
    }

    @SuppressWarnings("unchecked")
    private T normalizedToValue(double normalized) {
        return (T) numberType.toNumber(absoluteMinValuePrim + normalized
                * (absoluteMaxValuePrim - absoluteMinValuePrim));
    }

    private double valueToNormalized(T value) {
        if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            return 0;
        }
        return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
    }

    private float normalizedToScreen(double normalizedCoord) {
        return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
    }

    private double screenToNormalized(float screenCoord) {
        int width = getWidth();
        if (width <= 2 * padding) {
            return 0d;
        } else {
            double result = (screenCoord - padding) / (width - 2 * padding);
            return Math.min(1d, Math.max(0d, result));
        }
    }

    public interface OnRangeSeekBarChangeListener<T> {
        public void onRangeSeekBarValuesChanged(SingleSeekBar<?> bar, T minValue, T maxValue);
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
