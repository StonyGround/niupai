package com.xiuxiu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xiuxiu.R;
import com.xiuxiu.model.ThemeInfo;
import com.xiuxiu.model.ThemeTypeInfo;
import com.xiuxiu.util.ToolUtils;

import static android.R.attr.bitmap;


/**
 * Created by hzdykj on 2017/7/10.
 */

public class ThemeView extends ImageView implements Cloneable {
    private static final String TAG = "themeView";

    private Bitmap deleteBitmap;
    private Bitmap flipVBitmap;
    private Bitmap topBitmap;
    private Bitmap resizeBitmap;
    private Bitmap mBitmap;
    private Bitmap originBitmap;
    private Rect dst_delete;
    private Rect dst_resize;
    private Rect dst_flipV;
    private Rect dst_top;

    private ThemeTypeInfo themeTypeInfo;

    public ThemeTypeInfo getThemeTypeInfo() {
        return themeTypeInfo;
    }

    public void setThemeTypeInfo(ThemeTypeInfo themeTypeInfo) {
        this.themeTypeInfo = themeTypeInfo;
    }

    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    public void setOriginBitmap(Bitmap originBitmap) {
        this.originBitmap = originBitmap;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;
    private int frame;
    private int maxFrame;
    private int minFrame;

    public int getMinFrame() {
        return minFrame;
    }

    public void setMinFrame(int minFrame) {
        this.minFrame = minFrame;
    }

    public int getMaxFrame() {
        return maxFrame;
    }

    public void setMaxFrame(int maxFrame) {
        this.maxFrame = maxFrame;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;

    }

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


    private String nullPath = null;

    private String path;

    private DoubleSeekBar doubleSeekBar;

    public DoubleSeekBar getDoubleSeekBar() {
        return doubleSeekBar;
    }

    public void setDoubleSeekBar(DoubleSeekBar doubleSeekBar) {
        this.doubleSeekBar = doubleSeekBar;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public ThemeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThemeView(Context context, ThemeTypeInfo themeTypeInfo, int frame,int maxFrame,DoubleSeekBar doubleSeekBar) {
        super(context);
        init();
        this.themeTypeInfo = themeTypeInfo;
        this.frame = frame;
        this.maxFrame = maxFrame;
        this.minFrame = frame;
        this.doubleSeekBar=doubleSeekBar;
    }

    public ThemeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        dm = getResources().getDisplayMetrics();
        dst_delete = new Rect();
        dst_resize = new Rect();
        dst_flipV = new Rect();
        dst_top = new Rect();
        localPaint = new Paint();
        localPaint.setColor(getResources().getColor(R.color.red_e73a3d));
        localPaint.setAntiAlias(true);
        localPaint.setDither(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0f);
        mScreenwidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null && originBitmap != null) {

            float[] arrayOfFloat = new float[9];
            matrix.getValues(arrayOfFloat);
            float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
            float f7 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f8 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];


            canvas.save();

            lastLeftX = f1;
            lastLeftY = f2;

            float[] a = new float[9];
            matrix.getValues(a);
            canvas.drawBitmap(mBitmap, matrix, null);

            Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

            //置顶在左上角
//            dst_top.left = (int) (f3 - topBitmapWidth / 2);
//            dst_top.right = (int) (f3 + topBitmapWidth / 2);
//            dst_top.top = (int) (f4 - topBitmapHeight / 2);
//            dst_top.bottom = (int) (f4 + topBitmapHeight / 2);
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
            if (isInEdit) {

                canvas.drawLine(f1, f2, f3, f4, localPaint);
                canvas.drawLine(f3, f4, f7, f8, localPaint);
                canvas.drawLine(f5, f6, f7, f8, localPaint);
                canvas.drawLine(f5, f6, f1, f2, localPaint);

                canvas.drawBitmap(deleteBitmap, null, dst_delete, null);
                canvas.drawBitmap(resizeBitmap, null, dst_resize, null);
            }

            canvas.restore();
        }
    }

    public void setText(String text) {
        invalidate();
    }

    public void setThemeImage(String path) {
        matrix.reset();
        //使用拷贝 不然会对资源文件进行引用而修改
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setBitmap(bitmap);
    }

    public void changeThemeImage(String path) {
        //使用拷贝 不然会对资源文件进行引用而修改
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            originBitmap = bitmap;
            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        invalidate();
    }

    //
//
    public void setBitmap(Bitmap bitmap) {
        try {
            originBitmap = bitmap;
            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
            setDiagonalLength();
            initBitmaps();
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            oringinWidth = w;
            //Y坐标为 （顶部操作栏+正方形图）/2
            matrix.postTranslate(mScreenwidth / 2 - w / 2, mScreenHeight / 2 - h / 2);
            lastLeftX = mScreenwidth / 2 - w / 2;
            lastLeftY = mScreenHeight / 2 - h / 2;
            preLeftX = mScreenwidth / 2 - w / 2;
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDiagonalLength() {
        halfDiagonalLength = Math.hypot(mBitmap.getWidth(), mBitmap.getHeight()) / 2;
    }

    private void initBitmaps() {

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
        deleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.edit_close);
        resizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rotate);

        deleteBitmapWidth = (int) (deleteBitmap.getWidth() * BITMAP_SCALE);
        deleteBitmapHeight = (int) (deleteBitmap.getHeight() * BITMAP_SCALE);

        resizeBitmapWidth = (int) (resizeBitmap.getWidth() * BITMAP_SCALE);
        resizeBitmapHeight = (int) (resizeBitmap.getHeight() * BITMAP_SCALE);

    }

    private long preClicktime;

    private final long doubleClickTimeLimit = 200;

    private float preLeftX;
    private float preLeftY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        isInBitmap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isInButton(event, dst_delete)) {
                    if (operationListener != null) {
                        setThemeImage("");
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
                invalidate();
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
                invalidate();
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
                    invalidate();
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

                    invalidate();
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
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                /*lastX = event.getX(0);
                lastY = event.getY(0);
                isPointerDown = false;
                isMove = false;
                isInEdit = false;
                isInResize = false;
                setPressed(false);
                break;*/
            case MotionEvent.ACTION_UP:
                /*isInResize = false;
                isInSide = false;
                isPointerDown = false;
                isUp = true;
                break;*/
                isInResize = false;
                isInSide = false;
                isPointerDown = false;
                invalidate();
                break;

        }
        if (handled && operationListener != null) {
            operationListener.onEdit(this);
        }
        return handled;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        Log.d("getBitmap", bitmap.getWidth() + "----" + bitmap.getHeight());
        return bitmap;
    }

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
        float f3 = arrayOfFloat1[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f4 = arrayOfFloat1[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //左下角
        float f5 = 0.0F * arrayOfFloat1[0] + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f6 = 0.0F * arrayOfFloat1[3] + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];
        //右下角
        float f7 = arrayOfFloat1[0] * this.mBitmap.getWidth() + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f8 = arrayOfFloat1[3] * this.mBitmap.getWidth() + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];

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
        float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
        float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
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
        float diagonalLength = (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
        return diagonalLength;
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
        void onDeleteClick(ThemeView themeView);

        void onEdit(ThemeView themeView);

        void onClick(ThemeView themeView);

        void onTop(ThemeView themeView);
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public void setInEdit(boolean isInEdit) {
        this.isInEdit = isInEdit;
        invalidate();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
