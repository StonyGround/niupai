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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xiuxiu.R;
import com.xiuxiu.model.BubblePropertyModel;


public class BubbleTextView extends ImageView implements Cloneable {
    private static final String TAG = "BubbleTextView";

    private Bitmap deleteBitmap;
    private Bitmap flipVBitmap;
    private Bitmap topBitmap;
    private Bitmap resizeBitmap;
    //    private Bitmap mBitmap;
    private Bitmap originBitmap;
    private Rect dst_delete;
    private Rect dst_resize;
    private Rect dst_flipV;
    private Rect dst_top;


    private int deleteBitmapWidth;
    private int deleteBitmapHeight;
    private int resizeBitmapWidth;
    private int resizeBitmapHeight;
    private int flipVBitmapWidth;
    private int flipVBitmapHeight;

    //置顶
    private int topBitmapWidth;
    private int topBitmapHeight;
    private Paint localPaint;
    private int mScreenwidth, mScreenHeight;
    private static final float BITMAP_SCALE = 1f;
    private PointF mid = new PointF();
    private OperationListener operationListener;
    private float lastRotateDegree;

    //是否是第二根手指放下
    private boolean isPointerDown = false;
    //手指移动距离必须超过这个数值
    private final float pointerLimitDis = 20f;
    private final float pointerZoomCoeff = 0.09f;

    private final float moveLimitDis = 0.5f;

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

    public Bitmap getResizeImage() {
        return resizeImage;
    }

    private Bitmap resizeImage;

    /**
     * 对角线的长度
     */
    private float lastLength;
    private boolean isInResize = false;

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    private Matrix matrix = new Matrix();
    /**
     * 是否在四条线内部
     */
    private boolean isInSide;

    private float lastX;

    public float getLastX() {
        return lastX;
    }

    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public void setLastY(float lastY) {
        this.lastY = lastY;
    }

    private float lastY;
    /**
     * 是否在编辑模式
     */
    private boolean isInEdit = true;

    private float MIN_SCALE = 0.5f;

    private float MAX_SCALE = 1.5f;

    private double halfDiagonalLength;

    private float oringinWidth = 0;

    private DisplayMetrics dm;

    /**
     * 文字部分
     */
    private final String defaultStr;
    //显示的字符串
    private String mStr = "";

    public void setmStr(String mStr) {
        this.mStr = mStr;
    }

    //字号默认16sp
    private final float mDefultSize = 12;
    private float mFontSize = 16;
    //最大最小字号
    private final float mMaxFontSize = 18;
    private final float mMinFontSize = 12;

    //字离旁边的距离
    private final float mDefaultMargin = 20;
    private float mMargin = 20;

    public TextPaint getmFontPaint() {
        return mFontPaint;
    }

    public float getmFontSize() {
        return mFontSize;
    }

    //绘制文字的画笔
    private TextPaint mFontPaint;

    private Paint.FontMetrics fm;
    //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
    private float baseline;

    boolean isInit = true;

    //双指缩放时的初始距离
    private float oldDis;

    //是否按下
    private boolean isDown = false;
    //是否移动
    private boolean isMove = false;
    //是否抬起手
    private boolean isUp = false;
    //是否在顶部
    private boolean isTop = true;

    private boolean isInBitmap;

    private int fontColor;

    private int bubbleId;

    private int deltaX,deltaY;//点击位置和图形边界的偏移量

    public int getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(int bubbleId) {
        this.bubbleId = bubbleId;
    }

    private int bitmapWidth;
    private int bitmapHeight;

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.WHITE;
        bubbleId = 0;
        init();
    }

    public BubbleTextView(Context context) {
        super(context);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.WHITE;
        bubbleId = 0;
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.WHITE;
        bubbleId = 0;
        init();
    }

    /**
     * @param context
     * @param fontColor
     * @param bubbleId  some fuck id
     */
    public BubbleTextView(Context context, int fontColor, int bubbleId) {
        super(context);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = fontColor;
        this.bubbleId = bubbleId;
        init();
    }

    public BubbleTextView(Context context, int bubbleId) {
        super(context);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.WHITE;
        this.bubbleId = bubbleId;
        init();
    }


    private void init() {
        dm = getResources().getDisplayMetrics();
        dst_delete = new Rect();
        dst_resize = new Rect();
        dst_flipV = new Rect();
        dst_top = new Rect();
        localPaint = new Paint();
//        localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        localPaint.setColor(getResources().getColor(R.color.red_e73a3d));
        localPaint.setAntiAlias(true);
        localPaint.setDither(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0f);
        mScreenwidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mFontSize = mDefultSize;
        mFontPaint = new TextPaint();
        mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, dm));
        mFontPaint.setColor(fontColor);
        mFontPaint.setTextAlign(Paint.Align.CENTER);
        mFontPaint.setAntiAlias(true);
        fm = mFontPaint.getFontMetrics();

        topBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.copy);
        deleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_close);
//        flipVBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_flip);
        resizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rotate);

        baseline = fm.descent - fm.ascent;
        isInit = true;
        mStr = defaultStr;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (originBitmap != null) {
//            localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            Bitmap mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap mBitmapDelete = deleteBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap mBitmapCopy = topBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap mBitmapResize = resizeBitmap.copy(Bitmap.Config.ARGB_8888, true);

            float[] arrayOfFloat = new float[9];
            matrix.getValues(arrayOfFloat);
            float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f3 = arrayOfFloat[0] * bitmapWidth + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f4 = arrayOfFloat[3] * bitmapWidth + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * bitmapHeight + arrayOfFloat[2];
            float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * bitmapHeight + arrayOfFloat[5];
            float f7 = arrayOfFloat[0] * bitmapWidth + arrayOfFloat[1] * bitmapHeight + arrayOfFloat[2];
            float f8 = arrayOfFloat[3] * bitmapWidth + arrayOfFloat[4] * bitmapHeight + arrayOfFloat[5];

            canvas.save();

            //先往文字上绘图
            Canvas canvasText = new Canvas(mBitmap);
            canvasText.setBitmap(mBitmap);
            canvasText.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            float left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, dm);
            float scalex = arrayOfFloat[Matrix.MSCALE_X];
            float skewy = arrayOfFloat[Matrix.MSKEW_Y];
            float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);

            float size = rScale * 0.75f * mDefultSize;
            if (size > mMaxFontSize) {
                mFontSize = mMaxFontSize;
            } else if (size < mMinFontSize) {
                mFontSize = mMinFontSize;
            } else {
                mFontSize = size;
            }
            mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, dm));
            String[] texts = autoSplit(mStr, mFontPaint, mBitmap.getWidth() - left * 3);
            float height = (texts.length * (baseline + fm.leading) + baseline);
            float top = (mBitmap.getHeight() - height) / 2;
            //基于底线开始画的
            top += baseline;
            //绘制文字
            for (String text : texts) {
                if (TextUtils.isEmpty(text)) {
                    continue;
                }
                canvasText.drawText(text, mBitmap.getWidth() / 2, top, mFontPaint);  //坐标以控件左上角为原点
                top += baseline + fm.leading; //添加字体行间距
            }
            lastLeftX = f1;
            lastLeftY = f2;

            float[] a = new float[9];
            matrix.getValues(a);
//            Log.d("matrix", a[Matrix.MSKEW_X] + "**ondraw**" + a[Matrix.MSKEW_Y]);
            canvas.drawBitmap(mBitmap, matrix, null);
            bitmapWidth = mBitmap.getWidth();
            bitmapHeight = mBitmap.getHeight();

            //置顶在左上角
            dst_top.left = (int) (f3 - topBitmapWidth / 2);
            dst_top.right = (int) (f3 + topBitmapWidth / 2);
            dst_top.top = (int) (f4 - topBitmapHeight / 2);
            dst_top.bottom = (int) (f4 + topBitmapHeight / 2);
            //拉伸等操作在右下角
            dst_resize.left = (int) (f7 - resizeBitmapWidth / 2);
            dst_resize.right = (int) (f7 + resizeBitmapWidth / 2);
            dst_resize.top = (int) (f8 - resizeBitmapHeight / 2);
            dst_resize.bottom = (int) (f8 + resizeBitmapHeight / 2);
            //删除在右上角
            dst_delete.left = (int) (f1 - deleteBitmapWidth / 2);
            dst_delete.right = (int) (f1 + deleteBitmapWidth / 2);
            dst_delete.top = (int) (f2 - deleteBitmapHeight / 2);
            dst_delete.bottom = (int) (f2 + deleteBitmapHeight / 2);
            //水平镜像在右下角
//                dst_flipV.left = (int) (f5 - topBitmapWidth / 2);
//                dst_flipV.right = (int) (f5 + topBitmapWidth / 2);
//                dst_flipV.top = (int) (f6 - topBitmapHeight / 2);
//                dst_flipV.bottom = (int) (f6 + topBitmapHeight / 2);

            if (isInEdit) {

                canvas.drawLine(f1, f2, f3, f4, localPaint);
                canvas.drawLine(f3, f4, f7, f8, localPaint);
                canvas.drawLine(f5, f6, f7, f8, localPaint);
                canvas.drawLine(f5, f6, f1, f2, localPaint);

                canvas.drawBitmap(deleteBitmap, null, dst_delete, null);
                canvas.drawBitmap(resizeBitmap, null, dst_resize, null);
//                canvas.drawBitmap(flipVBitmap, null, dst_flipV, null);
                canvas.drawBitmap(topBitmap, null, dst_top, null);
            }

            canvas.restore();
        }
    }

    public void setText(String text) {
        mStr = text;
        invalidate();
    }

    @Override
    public void setImageResource(int resId) {
        matrix.reset();
        //使用拷贝 不然会对资源文件进行引用而修改
        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    public void setImageResource(BubbleTextView bubbleTextView, Bitmap bitmap) {
        matrix.reset();
        //使用拷贝 不然会对资源文件进行引用而修改
        setBitmap(bubbleTextView, bitmap);
    }

    public void setBitmap(BubbleTextView bubbleTextView, Bitmap bitmap) {
        mFontSize = bubbleTextView.getmFontSize();
//        BitmapFactory.Options options=new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
//        originBitmap = BitmapFactory.decodeResource(getResources(), bubbleTextView.getBubbleId(),options);
        originBitmap = bitmap;
        bitmapWidth = originBitmap.getWidth();
        bitmapHeight = originBitmap.getHeight();
//        mBitmap = originBitmap.copy(Bitmap.Config.ARGB_4444, true);
        matrix.set(bubbleTextView.getMatrix());
        setDiagonalLength(originBitmap);
        initBitmaps(originBitmap);
        oringinWidth = originBitmap.getWidth();

        mStr = bubbleTextView.getmStr();
        invalidate();
    }

    //
//
    public void setBitmap(Bitmap bitmap) {
        try {
            mFontSize = mDefultSize;
            originBitmap = bitmap;
//            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_4444, true);
            setDiagonalLength(originBitmap);
            initBitmaps(originBitmap);
            bitmapWidth = originBitmap.getWidth();
            bitmapHeight = originBitmap.getHeight();
            oringinWidth = bitmapWidth;
            //Y坐标为 （顶部操作栏+正方形图）/2
            matrix.postTranslate(mScreenwidth / 2 - bitmapWidth / 2, mScreenHeight / 2 - bitmapHeight / 2);
            lastLeftX = mScreenwidth / 2 - bitmapWidth / 2;
            lastLeftY = mScreenHeight / 2 - bitmapHeight / 2;
            preLeftX = mScreenwidth / 2 - bitmapWidth / 2;
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDiagonalLength(Bitmap mBitmap) {
        halfDiagonalLength = Math.hypot(mBitmap.getWidth(), mBitmap.getHeight()) / 2;
    }

    private void initBitmaps(Bitmap mBitmap) {

        float minWidth = mScreenwidth / 8;
        if (mBitmap.getWidth() < minWidth) {
            MIN_SCALE = 1f;
        } else {
            MIN_SCALE = 1.0f * minWidth / mBitmap.getWidth();
        }

        if (mBitmap.getWidth() > mScreenwidth) {
            MAX_SCALE = 1;
        } else {
            MAX_SCALE = 1.0f * mScreenwidth / mBitmap.getWidth();
        }


        deleteBitmapWidth = (int) (deleteBitmap.getWidth() * BITMAP_SCALE);
        deleteBitmapHeight = (int) (deleteBitmap.getHeight() * BITMAP_SCALE);

        resizeBitmapWidth = (int) (resizeBitmap.getWidth() * BITMAP_SCALE);
        resizeBitmapHeight = (int) (resizeBitmap.getHeight() * BITMAP_SCALE);

//        flipVBitmapWidth = (int) (flipVBitmap.getWidth() * BITMAP_SCALE);
//        flipVBitmapHeight = (int) (flipVBitmap.getHeight() * BITMAP_SCALE);

        topBitmapWidth = (int) (topBitmap.getWidth() * BITMAP_SCALE);
        topBitmapHeight = (int) (topBitmap.getHeight() * BITMAP_SCALE);
    }

    private long preClicktime;

    private final long doubleClickTimeLimit = 200;

    public float getPreLeftX() {
        return preLeftX;
    }

    public void setPreLeftX(float preLeftX) {
        this.preLeftX = preLeftX;
    }

    public float getPreLeftY() {
        return preLeftY;
    }

    public void setPreLeftY(float preLeftY) {
        this.preLeftY = preLeftY;
    }

    private float preLeftX;
    private float preLeftY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        isInBitmap = false;
//        int x = (int) event.getX();
//        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: ");
                if (handled && operationListener != null) {
                    operationListener.onEdit(this);
                }

                if (isInButton(event, dst_delete)) {
                    if (operationListener != null) {
                        setImageResource(0);
                        operationListener.onDeleteClick(this);
                    }
                    isDown = false;
                } else if (isInResize(event)) {
                    isInResize = true;
                    midPointToStartPoint(event);
                    lastRotateDegree = rotationToStartPoint(event);
                    lastLength = diagonalLength(event);
                    isDown = false;
                } else if (isInButton(event, dst_flipV)) {
                    PointF localPointF = new PointF();
                    midDiagonalPoint(localPointF);
                    matrix.postScale(-1.0F, 1.0F, localPointF.x, localPointF.y);
                    isDown = false;
                    invalidate();
                } else if (isInButton(event, dst_top)) {
                    //置顶
                    bringToFront();
                    if (operationListener != null) {
                        operationListener.onTop(this);
                    }
                    isDown = false;
                } else if (isInBitmap(event)) {
                    isInSide = true;
                    lastX = event.getX(0);
                    lastY = event.getY(0);
                    isDown = true;
                    isMove = false;
                    isPointerDown = false;
                    isUp = false;
                    isInBitmap = true;

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - preClicktime > doubleClickTimeLimit) {
                        preClicktime = currentTime;
                    } else {
                        if (isInEdit && operationListener != null) {
                            operationListener.onClick(this);
                        }
                    }

                } else {
                    handled = false;
                }
//                deltaX = x - rect.left;
//                deltaY = y - rect.top;
//                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > pointerLimitDis) {
                    oldDis = spacing(event);
                    isPointerDown = true;
                    midPointToStartPoint(event);
                } else {
                    isPointerDown = false;
                }
                isInSide = false;
                isInResize = false;
//                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //双指缩放
                if (isPointerDown) {
                    float scale;
                    float disNew = spacing(event);
                    if (disNew == 0 || disNew < pointerLimitDis) {
                        scale = 1;
                    } else {
                        scale = disNew / oldDis;
                        //缩放缓慢
                        scale = (scale - 1) * pointerZoomCoeff + 1;
                    }
                    float scaleTemp = (scale * Math.abs(dst_flipV.left - dst_resize.left)) / oringinWidth;
                    if (((scaleTemp <= MIN_SCALE)) && scale < 1 ||
                            (scaleTemp >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                    } else {
                        lastLength = diagonalLength(event);
                    }
                    matrix.postScale(scale, scale, mid.x, mid.y);
                } else if (isInResize) {
                    matrix.postRotate((rotationToStartPoint(event) - lastRotateDegree) * 2, mid.x, mid.y);
//                    float[] arrayOfFloat = new float[9];
//                    matrix.getValues(arrayOfFloat);
//                    Log.d("matrix", arrayOfFloat[Matrix.MSKEW_X] + "isInResize---" + arrayOfFloat[Matrix.MSKEW_Y]);
                    lastRotateDegree = rotationToStartPoint(event);

                    float scale = diagonalLength(event) / lastLength;

                    if (((diagonalLength(event) / halfDiagonalLength <= MIN_SCALE)) && scale < 1 ||
                            (diagonalLength(event) / halfDiagonalLength >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                        if (!isInResize(event)) {
                            isInResize = false;
                        }
                    } else {
                        lastLength = diagonalLength(event);
                    }

                    matrix.postScale(scale, scale, mid.x, mid.y);
//                    Log.e("BubbleTextView", "------缩放----");
                } else if (isInSide) {
                    //TODO 移动区域判断 不能超出屏幕
                    float x = event.getX(0);
                    float y = event.getY(0);
                    //判断手指抖动距离 加上isMove判断 只要移动过 都是true
                    if (!isMove && Math.abs(x - lastX) < moveLimitDis
                            && Math.abs(y - lastY) < moveLimitDis) {
                        isMove = false;
                    } else {
                        isMove = true;
                    }
                    matrix.postTranslate(x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                    invalidate();
//                    Log.e("BubbleTextView", "------移动----");
//                    Rect old = new Rect(rect);
//                    //更新矩形的位置
//                    rect.left = x - deltaX;
//                    rect.top = y - deltaY;
//                    rect.right = rect.left + WIDTH;
//                    rect.bottom = rect.top + WIDTH;
//                    old.union(rect);//要刷新的区域，求新矩形区域与旧矩形区域的并集
//                    invalidate(old);//出于效率考虑，设定脏区域，只进行局部刷新，不是刷新整个view
                }
//                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                /*lastX = event.getX(0);
                lastY = event.getY(0);
                isPointerDown = false;
                isMove = false;
                isInEdit = false;
                isInResize = false;
                setPressed(false);*/
                break;
            case MotionEvent.ACTION_UP:
                isInResize = false;
                isInSide = false;
//                isMove = false;
                isPointerDown = false;
                isUp = true;
                mid = null;

                break;

        }
//        if (handled && operationListener != null) {
//            operationListener.onEdit(this);
//            Log.d(TAG, "onTouchEvent: ");
//        }
//        //判断是不是做了点击动作 必须在编辑状态 且在图片内 并且是双击
//        if (isDoubleClick && isDown && !isPointerDown && !isMove && isUp && isInBitmap && isInEdit && operationListener != null) {
//            operationListener.onClick(this);
//        }
        return handled;
    }

//    public Bitmap getBitmap() {
//        return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
//    }

    /**
     * 是否在四条线内部
     *
     * @return
     */
    private boolean isInBitmap(MotionEvent event) {
        float[] arrayOfFloat1 = new float[9];
        this.matrix.getValues(arrayOfFloat1);
        //左上角
        float f1 = 0.0F * arrayOfFloat1[0] + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f2 = 0.0F * arrayOfFloat1[3] + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //右上角
        float f3 = arrayOfFloat1[0] * bitmapWidth + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f4 = arrayOfFloat1[3] * bitmapWidth + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //左下角
        float f5 = 0.0F * arrayOfFloat1[0] + arrayOfFloat1[1] * bitmapHeight + arrayOfFloat1[2];
        float f6 = 0.0F * arrayOfFloat1[3] + arrayOfFloat1[4] * bitmapHeight + arrayOfFloat1[5];
        //右下角
        float f7 = arrayOfFloat1[0] * bitmapWidth + arrayOfFloat1[1] * bitmapHeight + arrayOfFloat1[2];
        float f8 = arrayOfFloat1[3] * bitmapWidth + arrayOfFloat1[4] * bitmapHeight + arrayOfFloat1[5];

        float[] arrayOfFloat2 = new float[4];
        float[] arrayOfFloat3 = new float[4];
        //确定X方向的范围
        arrayOfFloat2[0] = f1;//左上的左
        arrayOfFloat2[1] = f3;//右上的右
        arrayOfFloat2[2] = f7;//右下的右
        arrayOfFloat2[3] = f5;//左下的左
        //确定Y方向的范围
        arrayOfFloat3[0] = f2;//左上的上
        arrayOfFloat3[1] = f4;//右上的上
        arrayOfFloat3[2] = f8;
        arrayOfFloat3[3] = f6;
        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0));
    }

    /**
     * 判断点是否在一个矩形内部
     *
     * @param xRange
     * @param yRange
     * @param x
     * @param y
     * @return
     */
    private boolean pointInRect(float[] xRange, float[] yRange, float x, float y) {
        //四条边的长度
        double a1 = Math.hypot(xRange[0] - xRange[1], yRange[0] - yRange[1]);
        double a2 = Math.hypot(xRange[1] - xRange[2], yRange[1] - yRange[2]);
        double a3 = Math.hypot(xRange[3] - xRange[2], yRange[3] - yRange[2]);
        double a4 = Math.hypot(xRange[0] - xRange[3], yRange[0] - yRange[3]);
        //待检测点到四个点的距离
        double b1 = Math.hypot(x - xRange[0], y - yRange[0]);
        double b2 = Math.hypot(x - xRange[1], y - yRange[1]);
        double b3 = Math.hypot(x - xRange[2], y - yRange[2]);
        double b4 = Math.hypot(x - xRange[3], y - yRange[3]);

        double u1 = (a1 + b1 + b2) / 2;
        double u2 = (a2 + b2 + b3) / 2;
        double u3 = (a3 + b3 + b4) / 2;
        double u4 = (a4 + b4 + b1) / 2;

        //矩形的面积
        double s = a1 * a2;
        double ss = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));
        return Math.abs(s - ss) < 0.5;


    }


    private boolean isInButton(MotionEvent event, Rect rect) {
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    private boolean isInResize(MotionEvent event) {
        int left = -20 + this.dst_resize.left;
        int top = -20 + this.dst_resize.top;
        int right = 20 + this.dst_resize.right;
        int bottom = 20 + this.dst_resize.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    private void midPointToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + event.getX(0);
        float f4 = f2 + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

    private void midDiagonalPoint(PointF paramPointF) {
        float[] arrayOfFloat = new float[9];
        this.matrix.getValues(arrayOfFloat);
        float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = arrayOfFloat[0] * bitmapWidth + arrayOfFloat[1] * bitmapHeight + arrayOfFloat[2];
        float f4 = arrayOfFloat[3] * bitmapWidth + arrayOfFloat[4] * bitmapHeight + arrayOfFloat[5];
        float f5 = f1 + f3;
        float f6 = f2 + f4;
        paramPointF.set(f5 / 2.0F, f6 / 2.0F);
    }


    /**
     * 在滑动过车中X,Y是不会改变的，这里减Y，减X，其实是相当于把X,Y当做原点
     *
     * @param event
     * @return
     */
    private float rotationToStartPoint(MotionEvent event) {

        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float x = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float y = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        double arc = Math.atan2(event.getY(0) - y, event.getX(0) - x);
        return (float) Math.toDegrees(arc);
    }

    /**
     * 触摸点到矩形中点的距离
     *
     * @param event
     * @return
     */
    private float diagonalLength(MotionEvent event) {
        return (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    public interface OperationListener {
        void onDeleteClick(BubbleTextView bubbleTextView);

        void onEdit(BubbleTextView bubbleTextView);

        void onClick(BubbleTextView bubbleTextView);

        void onTop(BubbleTextView bubbleTextView);
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public void setInEdit(boolean isInEdit) {
        this.isInEdit = isInEdit;
        invalidate();
//        postInvalidate();
    }

    /**
     * 自动分割文本
     *
     * @param content 需要分割的文本
     * @param p       画笔，用来根据字体测量文本的宽度
     * @param width   指定的宽度
     * @return 一个字符串数组，保存每行的文本
     */
    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth <= width) {
            return new String[]{content};
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        while (start < length) {
            if (p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if (end == length) { //不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }

    public String getmStr() {
        return mStr;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
