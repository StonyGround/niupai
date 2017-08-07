package com.xiuxiu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiuxiu.R;


/**
 * Created by sam on 14-8-14.
 */
public class StickerView extends View {

//    private float mScaleSize;

    public static final float MAX_SCALE_SIZE = 10;//3.2f;
    public static final float MIN_SCALE_SIZE = 0;//0.6f;

    //显示的字符串
    private final String mStr = "";
    private boolean hasTxt;
    private float[] mOriginPoints;
    private float[] mPoints;
    private RectF mOriginContentRect;
    private RectF mContentRect;
    private RectF mViewRect;
    //    private RectF mTextRect;
    private RectF mOriginTextRect;
    private PointF mid = new PointF();

    private float mLastPointX;

    public float getmLastPointX() {
        return mLastPointX;
    }


    public float getmLastPointY() {
        return mLastPointY;
    }


    private float mLastPointY;

    //Matrix左上坐标
    private float lastLeftX;
    private float lastLeftY;

    public float getLastLeftX() {
        return lastLeftX;
    }

    public void setLastLeftX(float lastLeftX) {
        this.lastLeftX = lastLeftX;
    }

    public float getLastLeftY() {
        return lastLeftY;
    }

    public void setLastLeftY(float lastLeftY) {
        this.lastLeftY = lastLeftY;
    }


    public Bitmap getmBitmap() {
        return mBitmap;
    }

    private Bitmap mBitmap;

    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    private Bitmap originBitmap;
    private Canvas canvasText;


    private Bitmap mControllerBitmap, mDeleteBitmap, mCopyBitmap;
    private Matrix mMatrix;
    private Paint mPaint, mBorderPaint;
    private float mControllerWidth, mControllerHeight, mDeleteWidth, mDeleteHeight, mCopyWidth, mCopyHeight;
    private boolean mInController, mInMove;

    private boolean mDrawController = true;
    //private boolean mCanTouch;
    private float mStickerScaleSize = 1.0f;
    private SizeAdjustingTextView sizeTextView;
    private CustomEditView customEditView;
    //    private LinearLayout linearLayout;
//    private TextPaint textPaint;
    private int fontColor;
    private int fontSize = 10;
    //    private Paint.FontMetrics fm;
    private DisplayMetrics dm;
    //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
//    private float baseline;
    private boolean isMove;
    private final long doubleClickTimeLimit = 200;
    private long preClicktime;

    private OnStickerTouchListener mOnStickerTouchListener;

    public StickerView(Context context, boolean hasTxt) {
        this(context, null, hasTxt);
    }

    public StickerView(Context context, AttributeSet attrs, boolean hasTxt) {
        this(context, attrs, 0, hasTxt);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle, boolean hasTxt) {
        super(context, attrs, defStyle);
        init(hasTxt);
    }

    private void init(boolean hasTxt) {
        this.hasTxt = hasTxt;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setColor(Color.WHITE);
        dm = getResources().getDisplayMetrics();
        if (hasTxt) {
//            mTextRect = new RectF();
            mOriginTextRect = new RectF();
            canvasText = new Canvas();
//            linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.text_layout, null);
//            sizeTextView = (SizeAdjustingTextView) linearLayout.findViewById(R.id.content_layout_txt);
            sizeTextView = new SizeAdjustingTextView(getContext());
//            textView = new TextView(getContext());
//            textView.measure(100, 100);
//            textView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
//            textView.setText("dddddddd");
//            Rect rect = new Rect(56,59,236,170);
//            textView.setClipBounds(rect);
//            canvasText.translate(ptToPx(9), ptToPx(16));
//            sizeTextView.measure(200,200);
            sizeTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT));//ViewGroup.LayoutParams.MATCH_PARENT
//            sizeTextView.setWidth(100);
//            sizeTextView.setHeight(100);
            sizeTextView.setText(mStr, TextView.BufferType.NORMAL);
            fontColor = Color.WHITE;
            sizeTextView.setBackgroundColor(Color.parseColor("#00000000"));
            sizeTextView.setTextColor(fontColor);
//            linearLayout.addView(sizeTextView);
            sizeTextView.setTextSize(fontSize);
//            textPaint = new TextPaint();
//            textPaint.setAntiAlias(true);
//            textPaint.setColor(fontColor);
//            textPaint.setTextAlign(Paint.Align.CENTER);
//            textPaint.setTextSize(getTextSize(fontSize));
//            fm = textPaint.getFontMetrics();
//            baseline = fm.descent - fm.ascent;
            customEditView = new CustomEditView(getContext());
        }
        mBorderPaint = new Paint(mPaint);
        mBorderPaint.setColor(Color.parseColor("#ff7700"));//#B2ffffff
        mBorderPaint.setShadowLayer(dpToPx(2.0f), 0, 0, Color.parseColor("#33000000"));

        mControllerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_wz_bg_btn3);
        mControllerWidth = mControllerBitmap.getWidth();
        mControllerHeight = mControllerBitmap.getHeight();

        mCopyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_wz_bg_btn2);
        mCopyWidth = mCopyBitmap.getWidth();
        mCopyHeight = mCopyBitmap.getHeight();

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_wz_bg_btn1);
        mDeleteWidth = mDeleteBitmap.getWidth();
        mDeleteHeight = mDeleteBitmap.getHeight();

    }

    public void setWaterMark(@NonNull Bitmap bitmap,float x,float y) {
        if (hasTxt && bitmap != null) {
            originBitmap = bitmap;
            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvasText.setBitmap(mBitmap);
        } else {
            mBitmap = bitmap;
        }
        mStickerScaleSize = 0f;
        setFocusable(true);
        try {
            if (mBitmap != null) {
                float px = mBitmap.getWidth();
                float py = mBitmap.getHeight();
                //mOriginPoints = new float[]{px, py, px + bitmap.getWidth(), py, bitmap.getWidth() + px, bitmap.getHeight() + py, px, py
                // + bitmap.getHeight()};
                mOriginPoints = new float[]{0, 0, px, 0, px, py, 0, py, px / 2, py / 2};
                mOriginContentRect = new RectF(0, 0, px, py);
                mPoints = new float[10];
                mContentRect = new RectF();

                mMatrix = new Matrix();

                mMatrix.postTranslate(x, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        postInvalidate();

    }

    public void setWaterMark(@NonNull Bitmap bitmap) {
        if (hasTxt && bitmap != null) {
            originBitmap = bitmap;
            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvasText.setBitmap(mBitmap);
        } else {
            mBitmap = bitmap;
        }
        mStickerScaleSize = 0f;
        setFocusable(true);
        try {
            if (mBitmap != null) {
                float px = mBitmap.getWidth();
                float py = mBitmap.getHeight();
                //mOriginPoints = new float[]{px, py, px + bitmap.getWidth(), py, bitmap.getWidth() + px, bitmap.getHeight() + py, px, py
                // + bitmap.getHeight()};
                mOriginPoints = new float[]{0, 0, px, 0, px, py, 0, py, px / 2, py / 2};
                mOriginContentRect = new RectF(0, 0, px, py);
                mPoints = new float[10];
                mContentRect = new RectF();

                mMatrix = new Matrix();
                float transtLeft = ((float) dm.widthPixels - px) / 2;
                float transtTop = ((float) dm.heightPixels - py) / 2;

                mMatrix.postTranslate(transtLeft, transtTop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        postInvalidate();

    }

    public void setTextDraw(Bitmap bitmap, float left, float top, float right, float bottom) {
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//        lp.leftMargin = (int)dpToPx(x);
//        lp.topMargin = (int)dpToPx(y);
//        addView(linearLayout,lp);
//        int w = (int)dpToPx(width);
//        int h = (int) dpToPx(height);
//        PrintUtils.println("w:"+w);
//        PrintUtils.println("h:" + h);
//        sizeTextView.resizeText(w, h);

        float x = dpToPx(left);
        float y = dpToPx(top);
        float r = dpToPx(right);
        float b = dpToPx(bottom);
        mOriginTextRect.set(x, y, r, b);
        sizeTextView.setBounds(mOriginTextRect);
//        sizeTextView.setWidth(w);
//        sizeTextView.setHeight(h);
        if (canvasText != null) {
            canvasText.translate(x, y);
        }
        setWaterMark(bitmap);
    }

    public void resetText(String text) {
        if (hasTxt) {
            if (TextUtils.isEmpty(text)) {
                sizeTextView.setText(mStr, TextView.BufferType.NORMAL);
            } else {
                sizeTextView.setText(text, TextView.BufferType.NORMAL);
            }

        }
        invalidate();
    }

    public Matrix getMarkMatrix() {
        return mMatrix;
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null || mMatrix == null) {
            return;
        }

        mMatrix.mapPoints(mPoints, mOriginPoints);
        mMatrix.mapRect(mContentRect, mOriginContentRect);

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        if (hasTxt) {
//            float[] arrayOfFloat = new float[9];
//            mMatrix.getValues(arrayOfFloat);
//            float scalex = arrayOfFloat[Matrix.MSCALE_X];
//            float skewy = arrayOfFloat[Matrix.MSKEW_Y];
//            float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
//            float size = rScale * 0.75f * fontSize;
            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvasText.setBitmap(mBitmap);
            canvasText.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            sizeTextView.draw(canvasText);
        }

        lastLeftX = mPoints[0];
        lastLeftY = mPoints[1];
        Log.d("onDraw", mPoints[0] + "----" + mPoints[1]);

        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        if (mDrawController) {//&& isFocusable()
            canvas.drawLine(mPoints[0], mPoints[1], mPoints[2], mPoints[3], mBorderPaint);
            canvas.drawLine(mPoints[2], mPoints[3], mPoints[4], mPoints[5], mBorderPaint);
            canvas.drawLine(mPoints[4], mPoints[5], mPoints[6], mPoints[7], mBorderPaint);
            canvas.drawLine(mPoints[6], mPoints[7], mPoints[0], mPoints[1], mBorderPaint);
            canvas.drawBitmap(mControllerBitmap, mPoints[4] - mControllerWidth / 2, mPoints[5] - mControllerHeight / 2, mBorderPaint);
            canvas.drawBitmap(mCopyBitmap, mPoints[2] - mCopyWidth / 2, mPoints[3] - mCopyHeight / 2, mBorderPaint);
            canvas.drawBitmap(mDeleteBitmap, mPoints[0] - mDeleteWidth / 2, mPoints[1] - mDeleteHeight / 2, mBorderPaint);
        }


    }

    public SizeAdjustingTextView getSizeTextView() {
        return sizeTextView;
    }

    private float getTextSize(float size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
    }

    private float dpToPx(float pt) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pt, getResources().getDisplayMetrics());
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mDrawController = false;
        draw(canvas);
        mDrawController = true;
        canvas.save();
        return bitmap;
    }

    public void setShowDrawController(boolean show) {
        mDrawController = show;
        invalidate();
    }


    private boolean isInController(float x, float y) {
        int position = 4;
        //while (position < 8) {
        float rx = mPoints[position];
        float ry = mPoints[position + 1];
        RectF rectF = new RectF(rx - mControllerWidth / 2,
                ry - mControllerHeight / 2,
                rx + mControllerWidth / 2,
                ry + mControllerHeight / 2);
        if (rectF.contains(x, y)) {
            return true;
        }
        //   position += 2;
        //}
        return false;

    }

    private boolean isInDelete(float x, float y) {
        int position = 0;
        //while (position < 8) {
        float rx = mPoints[position];
        float ry = mPoints[position + 1];
        RectF rectF = new RectF(rx - mDeleteWidth / 2,
                ry - mDeleteHeight / 2,
                rx + mDeleteWidth / 2,
                ry + mDeleteHeight / 2);
        if (rectF.contains(x, y)) {
            return true;
        }
        //   position += 2;
        //}
        return false;

    }

    private boolean isInCopy(float x, float y) {
        int position = 2;
        float rx = mPoints[position];
        float ry = mPoints[position + 1];
        RectF rectF = new RectF(rx - mCopyWidth / 2,
                ry - mCopyHeight / 2,
                rx + mCopyWidth / 2,
                ry + mCopyHeight / 2);
        if (rectF.contains(x, y)) {
            return true;
        }
        return false;
    }

    private boolean mInDelete = false;

    public void clear() {
        mInDelete = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (!isFocusable()) {
        return super.dispatchTouchEvent(event);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
        }
        float x = event.getX();
        float y = event.getY();
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isInController(x, y)) {
//                    mInController = true;
                    mLastPointY = y;
                    mLastPointX = x;
//                    midPointToStartPoint(x, y);
                } else if (isInDelete(x, y)) {
                    mInDelete = true;
                    mOnStickerTouchListener.onDelete(this);
                } else if (isInCopy(x, y)) {
                    if (mOnStickerTouchListener != null) {
                        mOnStickerTouchListener.onCopy(this);
                    }
                } else if (mContentRect.contains(x, y)) {
                    setShowDrawController(true);
                    mLastPointY = y;
                    mLastPointX = x;
                    mInMove = true;
                    isMove = false;
                    if (mOnStickerTouchListener != null) {
//                        mOnStickerTouchListener.onMoveToHead(this);
                    }
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - preClicktime > doubleClickTimeLimit) {
                        preClicktime = currentTime;
                    } else {
                        if (mOnStickerTouchListener != null) {
                            mOnStickerTouchListener.onDoubleClick(this);
                        }
                    }
                } else {
                    setShowDrawController(false);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mInController = false;
                if (isInDelete(x, y) && mInDelete) {
                    mInDelete = false;
//                    doDeleteSticker();
                }
                mLastPointX = x;
                mLastPointY = y;
                if (mContentRect.contains(x, y)) {
                    if (mOnStickerTouchListener != null) {
                        mOnStickerTouchListener.onMoveToHead(this);
                    }
                }
            case MotionEvent.ACTION_CANCEL:
                mLastPointX = 0;
                mLastPointY = 0;
                mInController = false;
                mInMove = false;
                mInDelete = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInController) {
                    mMatrix.postRotate(rotation(event), mid.x, mid.y);//mPoints[8], mPoints[9]
                    float nowLenght = caculateLength(mPoints[4], mPoints[5]);
                    float touchLenght = caculateLength(event.getX(), event.getY());
                    if ((float) Math.sqrt((nowLenght - touchLenght) * (nowLenght - touchLenght)) > 0.0f) {
                        float scale = touchLenght / nowLenght;
                        float nowsc = mStickerScaleSize * scale;
                        if (nowsc >= MIN_SCALE_SIZE && nowsc <= MAX_SCALE_SIZE) {
                            mMatrix.postScale(scale, scale, mid.x, mid.y);
                            mStickerScaleSize = nowsc;
                        }
                    }
//                    if(hasTxt){
//                        mMatrix.mapRect(mTextRect,mOriginTextRect);
//                        sizeTextView.setBounds(mTextRect);
//                    }
                    invalidate();
                    mLastPointX = x;
                    mLastPointY = y;
                    break;
                }
                if (mInMove) { //拖动的操作
                    float cX = x - mLastPointX;
                    float cY = y - mLastPointY;
                    mInController = false;
                    //Log.i("MATRIX_OK", "ma_jiaodu:" + a(cX, cY));
//                    float x = event.getX(0);
//                    float y = event.getY(0);
                    //判断手指抖动距离 加上isMove判断 只要移动过 都是true
                    if (!isMove && Math.abs(cX) < 0.5f
                            && Math.abs(cY) < 0.5f) {
                        isMove = false;
                    } else {
                        isMove = true;
                    }
//                    if (FloatMath.sqrt(cX * cX + cY * cY) > 2.0f  && canStickerMove(cX, cY)) {
                    //Log.i("MATRIX_OK", "is true to move");
                    mMatrix.postTranslate(cX, cY);
                    postInvalidate();
                    mLastPointX = x;
                    mLastPointY = y;
//                    }
                    break;
                }
                return true;
        }
        return true;
    }

    public void doDeleteSticker() {
        setWaterMark(null);
        if (mOnStickerTouchListener != null) {
            mOnStickerTouchListener.onDelete(this);
        }
    }

    private boolean canStickerMove(float cx, float cy) {
        float px = cx + mPoints[8];
        float py = cy + mPoints[9];
        if (mViewRect.contains(px, py)) {
            return true;
        } else {
            return false;
        }
    }


    private float caculateLength(float x, float y) {
        float ex = x - mid.x;
        float ey = y - mid.y;
        return (float) Math.sqrt(ex * ex + ey * ey);
    }


    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        double delta_x = x - mid.x;
        double delta_y = y - mid.y;
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public interface OnStickerTouchListener {
        public void onCopy(StickerView stickerView);

        public void onDelete(StickerView stickerView);

        public void onMoveToHead(StickerView stickerView);

        public void onDoubleClick(StickerView stickerView);
    }

    /**
     * 触摸的位置和图片左上角位置的中点
     *
     * @param x
     * @param y
     */
    private void midPointToStartPoint(float x, float y) {
        float[] arrayOfFloat = new float[9];
        mMatrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + x;
        float f4 = f2 + y;
        mid.set(f3 / 2, f4 / 2);
    }

    /**
     * 是否在四条线内部
     * 图片旋转后 可能存在菱形状态 不能用4个点的坐标范围去判断点击区域是否在图片内
     *
     * @return
     */
//    private boolean isInBitmap(MotionEvent event) {
//        float[] arrayOfFloat1 = new float[9];
//        this.mMatrix.getValues(arrayOfFloat1);
//        //左上角
//        float f1 = 0.0F * arrayOfFloat1[0] + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
//        float f2 = 0.0F * arrayOfFloat1[3] + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
//        //右上角
//        float f3 = arrayOfFloat1[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
//        float f4 = arrayOfFloat1[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
//        //左下角
//        float f5 = 0.0F * arrayOfFloat1[0] + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
//        float f6 = 0.0F * arrayOfFloat1[3] + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];
//        //右下角
//        float f7 = arrayOfFloat1[0] * this.mBitmap.getWidth() + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
//        float f8 = arrayOfFloat1[3] * this.mBitmap.getWidth() + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];
//
//        float[] arrayOfFloat2 = new float[4];
//        float[] arrayOfFloat3 = new float[4];
//        //确定X方向的范围
//        arrayOfFloat2[0] = f1;//左上的x
//        arrayOfFloat2[1] = f3;//右上的x
//        arrayOfFloat2[2] = f7;//右下的x
//        arrayOfFloat2[3] = f5;//左下的x
//        //确定Y方向的范围
//        arrayOfFloat3[0] = f2;//左上的y
//        arrayOfFloat3[1] = f4;//右上的y
//        arrayOfFloat3[2] = f8;//右下的y
//        arrayOfFloat3[3] = f6;//左下的y
//        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0));
//    }
//    /**
//     * 判断点是否在一个矩形内部
//     *
//     * @param xRange
//     * @param yRange
//     * @param x
//     * @param y
//     * @return
//     */
//    private boolean pointInRect(float[] xRange, float[] yRange, float x, float y) {
//        //四条边的长度
//        double a1 = Math.hypot(xRange[0] - xRange[1], yRange[0] - yRange[1]);
//        double a2 = Math.hypot(xRange[1] - xRange[2], yRange[1] - yRange[2]);
//        double a3 = Math.hypot(xRange[3] - xRange[2], yRange[3] - yRange[2]);
//        double a4 = Math.hypot(xRange[0] - xRange[3], yRange[0] - yRange[3]);
//        //待检测点到四个点的距离
//        double b1 = Math.hypot(x - xRange[0], y - yRange[0]);
//        double b2 = Math.hypot(x - xRange[1], y - yRange[1]);
//        double b3 = Math.hypot(x - xRange[2], y - yRange[2]);
//        double b4 = Math.hypot(x - xRange[3], y - yRange[3]);
//
//        double u1 = (a1 + b1 + b2) / 2;
//        double u2 = (a2 + b2 + b3) / 2;
//        double u3 = (a3 + b3 + b4) / 2;
//        double u4 = (a4 + b4 + b1) / 2;
//
//        //矩形的面积
//        double s = a1 * a2;
//        //海伦公式 计算4个三角形面积
//        double ss = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
//                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
//                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
//                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));
//        return Math.abs(s - ss) < 0.5;
//
//
//    }
    public void setOnStickerTouchListener(OnStickerTouchListener listener) {
        mOnStickerTouchListener = listener;
    }
}
