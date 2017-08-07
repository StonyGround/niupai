package com.xiuxiu.view;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import android.annotation.SuppressLint;
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
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xiuxiu.R;


/**
 * 贴纸 编辑核心类
 *
 * @author Aports
 */
public class CoreStickerView extends RelativeLayout {

    private GifImageView StickerBackground;

    private CoreCustomEditView StickerEdittext;

    private Context context;

    // 和内容的间距
    private int mContentPadding;

    // 画边框和按钮
    private Paint mBorderPaint;
    private Bitmap mBitmapClose = null;
    private Bitmap mBitmapZoom = null;
    private Bitmap mBitmapEdit = null;

    // 设置画布
    private PaintFlagsDrawFilter paintFilter = null;

    // 屏幕宽高
    private int screenWidth = 0;

    // 贴纸原大小
    private int IndexBoxWidth = 0;
    private int IndexBoxHeight = 0;

    // 贴纸大小
    private int boxWidth = 0;
    private int boxHeight = 0;

    // 是否存在编辑按钮
    private boolean isEditType = false;

    // 一些临时的数据
    private float clickX;
    private float clickY;
    private float startX;
    private float startY;
    private float moveX;
    private float moveY;
    private float centerX;
    private float centerY;
    private float newScale;
    private float lastScale = 0.0f;
    private float newRotation;
    private float lastRotation = 0.0f;

    private enum MODE {
        DRAG, ZOOM, CLOSE, EDIT
    }

    private MODE mode = MODE.DRAG;// 默认模式

    // 操作后的位置
    private float currLeft;
    private float currTop;

    // 操作后增加的缩放大小
    private float currScale;
    private float currRotation;

    // 操作后的贴纸位置
    private String currPath;

    private RectF currRect;

    // 默认比例
    private float indexScale;

    // 设置点击的事件
    private onEditClickListener listener;

    // 是否隐藏编辑框
    private boolean isEditBox = false;

    public CoreStickerView(Context context) {
        super(context);
        init(context);
    }

    public CoreStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        this.context = context;
        this.screenWidth = getScreenWidth();
        this.boxWidth = 0;
        this.boxHeight = 0;
        initDraw();
        initView();
    }

    private void initDraw() {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setColor(Color.WHITE);

        mBorderPaint = new Paint(mPaint);
        mBorderPaint.setColor(Color.parseColor("#B2ffffff"));
        mBorderPaint.setStrokeWidth(pxTopxUtils(context, dipToPX(2.0f)));
        mBorderPaint.setShadowLayer(pxTopxUtils(context, dipToPX(2.0f)), 0, 0, Color.parseColor("#33000000"));

//        mBitmapClose = BitmapFactory.decodeResource(getResources(), R.drawable.edit_close);
//        mBitmapZoom = BitmapFactory.decodeResource(getResources(), R.drawable.rotate);
//        mBitmapEdit = BitmapFactory.decodeResource(getResources(), R.drawable.bianji1);

        // 画布参数
        paintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        currRect = new RectF(0, 0, 0, 0);
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        StickerBackground = new GifImageView(context);
        StickerBackground.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(StickerBackground, 0);

        StickerEdittext = new CoreCustomEditView(context);
        StickerEdittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
        StickerEdittext.setGravity(Gravity.LEFT);
        StickerEdittext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        StickerEdittext.setTextColor(Color.rgb(0, 0, 0));
        StickerEdittext.setBackgroundDrawable(null);
        StickerEdittext.setPadding(0, 0, 0, 0);
        StickerEdittext.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        StickerEdittext.setCursorVisible(false);
        addView(StickerEdittext, 1);

        setVisibility(View.GONE);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isEditBox) {
            // 获取画线条的间距，已按钮中心点开始;
            canvas.setDrawFilter(paintFilter);

            if (currScale != 0) {
                float LineScaleX = 1.0f / currScale;
                mBorderPaint.setStrokeWidth(pxTopxUtils(context, dipToPX(2.0f)) * LineScaleX);
            }
            // 画线条
            canvas.drawLine(mContentPadding, mContentPadding, boxWidth + mContentPadding, mContentPadding, mBorderPaint);
            canvas.drawLine(mContentPadding, mContentPadding, mContentPadding, boxHeight + mContentPadding, mBorderPaint);
            canvas.drawLine(boxWidth + mContentPadding, mContentPadding, boxWidth + mContentPadding, boxHeight + mContentPadding,
                    mBorderPaint);
            canvas.drawLine(mContentPadding, boxHeight + mContentPadding, boxWidth + mContentPadding, boxHeight + mContentPadding,
                    mBorderPaint);

            // 画关闭
            Matrix CloseMatrix = new Matrix();
            if (currScale != 0) {
                float CloseScaleX = 1.0f / currScale;
                float CloseScaleY = 1.0f / currScale;
                CloseMatrix.postScale(CloseScaleX, CloseScaleY);
            }
            CloseMatrix.postRotate(-currRotation);
            int closeWidth = mBitmapClose.getWidth();
            int closeHeight = mBitmapClose.getHeight();
            CloseMatrix.preTranslate(-(closeWidth / 2), -(closeHeight / 2));
            CloseMatrix.postTranslate(closeWidth / 2, closeHeight / 2);
            canvas.drawBitmap(mBitmapClose, CloseMatrix, null);

            // 画缩放按钮
            Matrix ZoomMatrix = new Matrix();
            if (currScale != 0) {
                float ZoomScaleX = 1.0f / currScale;
                float ZoomScaleY = 1.0f / currScale;
                ZoomMatrix.postScale(ZoomScaleX, ZoomScaleY);
            }
            ZoomMatrix.postRotate(-currRotation);
            int zoomWidth = mBitmapZoom.getWidth();
            int zoomHeight = mBitmapZoom.getHeight();
            ZoomMatrix.preTranslate(-(zoomWidth / 2), -(zoomHeight / 2));
            ZoomMatrix.postTranslate(boxWidth + zoomWidth / 2, boxHeight + zoomHeight / 2);
            canvas.drawBitmap(mBitmapZoom, ZoomMatrix, null);

            // 修改按钮
            if (isEditType) {
                Matrix EditMatrix = new Matrix();
                if (currScale != 0) {
                    float EditScaleX = 1.0f / currScale;
                    float EditScaleY = 1.0f / currScale;
                    EditMatrix.postScale(EditScaleX, EditScaleY);
                }
                int editWidth = mBitmapEdit.getWidth();
                int editHeight = mBitmapEdit.getHeight();
                EditMatrix.preTranslate(-(editWidth / 2), -(editHeight / 2));
                EditMatrix.postTranslate(editWidth / 2, boxHeight + editHeight / 2);
                canvas.drawBitmap(mBitmapEdit, EditMatrix, null);
                StickerEdittext.setVisibility(View.VISIBLE);
            } else {
                StickerEdittext.setVisibility(View.GONE);
            }
        } else {
            if (isEditType) {
                StickerEdittext.setVisibility(View.VISIBLE);
            } else {
                StickerEdittext.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "NewApi"})
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                // 显示编辑框
                setStickerEditBox(false);

                clickX = event.getX();
                clickY = event.getY();
                if (isClose(clickX, clickY)) {
                    mode = MODE.CLOSE;// TODO 点击关闭按钮
                } else if (isZoom(clickX, clickY)) {
                    startX = event.getRawX();
                    startY = event.getRawY();
                    mode = MODE.ZOOM;// TODO 点击放大缩小按钮
                } else {
                    if (isEditType) {
                        if (isEdit(clickX, clickY)) {
                            mode = MODE.EDIT;// TODO 点击修改按钮
                        } else {
                            startX = event.getRawX();
                            startY = event.getRawY();
                            mode = MODE.DRAG;// TODO 拖动内容
                        }
                    } else {
                        startX = event.getRawX();
                        startY = event.getRawY();
                        mode = MODE.DRAG; // TODO 拖动内容
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // TODO 拖动内容
                if (mode == MODE.DRAG) {
                    moveX = event.getRawX();
                    moveY = event.getRawY();

                    currLeft = moveX - startX;
                    currTop = moveY - startY;

                    resetInvalidate();

                    startX = moveX;
                    startY = moveY;
                }

                // TODO 点击放大缩小按钮
                if (mode == MODE.ZOOM) {

                    moveX = event.getRawX();
                    moveY = event.getRawY();
                    newScale = (getPointsDistance(new PointF(centerX, centerY), new PointF(moveX, moveY)) / getPointsDistance(new PointF(
                            centerX, centerY), new PointF(startX, startY)))
                            + lastScale;

                    // 缩放限制
                    if (newScale >= indexScale) {
                        setScaleX(newScale);
                        setScaleY(newScale);

                        currScale = newScale;

                        Log.d("xxz", "currScale:" + currScale);
                    }

                    // 如果为文字就不能支持旋转操作
                    if (!isEditType) {
                        newRotation = rotation(startX, startY, event) + lastRotation;
                        setRotation(newRotation);
                        currRotation = newRotation;
                    }

                    // 重绘
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                // TODO 点击关闭按钮
                if (mode == MODE.CLOSE) {
                    setVisibility(View.GONE);
                }

                // TODO 点击放大缩小
                if (mode == MODE.ZOOM) {
                    lastScale = newScale - 1.0f;
                    lastRotation = newRotation;
                }

                // TODO 点击编辑按钮
                if (mode == MODE.EDIT) {
                    if (listener != null)
                        listener.click();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private float rotation(float lastX, float lastY, MotionEvent event) {
        float originDegree = calculateDegree(lastX, lastY);
        float nowDegree = calculateDegree(event.getRawX(), event.getRawY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        double delta_x = x - centerX;
        double delta_y = y - centerY;
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 求两点间距离
    private float getPointsDistance(PointF p1, PointF p2) {
        float ret = (float) Math.sqrt(Math.abs((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
        return ret;
    }

    // 判断是否是关闭事件
    public boolean isClose(float x, float y) {
        if (x <= mBitmapClose.getWidth() && y <= mBitmapClose.getHeight()) {
            return true;
        }
        return false;
    }

    // 判断是否是放大缩小
    public boolean isZoom(float x, float y) {
        if (x >= boxWidth + mContentPadding * 2 - mBitmapZoom.getWidth() && y >= boxHeight + mContentPadding * 2 - mBitmapZoom.getHeight
                ()) {
            return true;
        }
        return false;
    }

    // 判断是否点击了修改文字按钮
    public boolean isEdit(float x, float y) {
        if (x <= mBitmapEdit.getWidth() && y >= boxHeight + mContentPadding * 2 - mBitmapEdit.getHeight()) {
            return true;
        }
        return false;
    }

    // 判断左边缘右边缘
    public boolean isRightEdge(float x) {
        if (x + (boxWidth + mContentPadding * 2) <= screenWidth) {
            return true;
        }
        return false;
    }

    // 判断上边缘下边缘
    public boolean isBottomEdge(float y) {
        if (y + (boxHeight + mContentPadding * 2) <= screenWidth) {
            return true;
        }
        return false;
    }

    // 默认设置界面视图
    @SuppressLint("NewApi")
    private void indexInvalidate() {

        // 获取间距
        mContentPadding = Math.max(Math.max(mBitmapClose.getWidth() / 2, mBitmapZoom.getWidth() / 2), mBitmapEdit.getWidth() / 2);

        // 设置默认放大比例为屏幕的3/1
        float currScaleX = (float) getScreenWidth() / 3.0f / (float) IndexBoxWidth;
        float currScaleY = (float) getScreenWidth() / 3.0f / (float) IndexBoxHeight;

        // 获取宽度高度
        boxWidth = (int) (IndexBoxWidth * currScaleX);
        boxHeight = (int) (IndexBoxHeight * currScaleY);

        // 设置默认角度
        currRotation = 0.0f;
        lastRotation = 0.0f;
        setRotation(0.01f);

        // 设置默认缩放
        newScale = 1.0f;
        currScale = 1.0f;
        indexScale = 1.0f;
        lastScale = 0.0f;
        setScaleX(1.0f);
        setScaleY(1.0f);

        // 设置图片大小
        LayoutParams StickerBackgroundParams = new LayoutParams(boxWidth, boxHeight);
        StickerBackground.setLayoutParams(StickerBackgroundParams);

        // 设置编辑框大小和位置
        LayoutParams StickerEditViewParams = new LayoutParams(boxWidth, boxHeight);
        if (isEditType) {
            float l = boxWidth * currRect.left;
            float t = boxHeight * currRect.top;
            float r = boxWidth * currRect.right;
            float b = boxHeight * currRect.bottom;

            // 预防设置未生效，重新设置
            StickerEditViewParams.leftMargin = (int) l;
            StickerEditViewParams.topMargin = (int) t;
            StickerEditViewParams.rightMargin = (int) r;
            StickerEditViewParams.bottomMargin = (int) b;
        }
        StickerEdittext.setLayoutParams(StickerEditViewParams);

        // 计算出增加了间距的和缩放比例总大小
        int tempWidth = (int) (boxWidth + mContentPadding * 2);
        int tempHeight = (int) (boxHeight + mContentPadding * 2);

        // 设置默认位置
        currLeft = (screenWidth - tempWidth) / 2;
        currTop = (screenWidth - tempHeight) / 2;
        LayoutParams StickerViewParams = new LayoutParams(tempWidth, tempHeight);
        StickerViewParams.topMargin = (int) currLeft;
        StickerViewParams.leftMargin = (int) currTop;

        // 设置大小
        setLayoutParams(StickerViewParams);

        // 设置间距
        setPadding(mContentPadding, mContentPadding, mContentPadding, mContentPadding);

        // 记录中心点
        LayoutParams getStickerViewParams = (LayoutParams) getLayoutParams();
        centerX = getStickerViewParams.leftMargin + tempWidth / 2;
        centerY = getStickerViewParams.topMargin + tempHeight / 2;

        // 显示编辑框并且重绘
        setStickerEditBox(false);

        // 重绘
        postInvalidate();
    }

    // 重绘界面视图
    @SuppressLint("WrongCall")
    public void resetInvalidate() {

        // 计算出增加了间距的总大小
        int tempWidth = (int) (boxWidth + mContentPadding * 2);
        int tempHeight = (int) (boxHeight + mContentPadding * 2);

        // 设置位置
        LayoutParams StickerViewParams = (LayoutParams) getLayoutParams();
        currTop = StickerViewParams.topMargin + currTop;
        currLeft = StickerViewParams.leftMargin + currLeft;
        if (isBottomEdge(currTop)) {
            StickerViewParams.topMargin = (int) currTop;
        } else {
            currTop = StickerViewParams.topMargin;
        }
        if (isRightEdge(currLeft)) {
            StickerViewParams.leftMargin = (int) currLeft;
        } else {
            currLeft = StickerViewParams.leftMargin;
        }
        setLayoutParams(StickerViewParams);

        // 记录中心点
        LayoutParams getStickerViewParams = (LayoutParams) getLayoutParams();
        centerX = getStickerViewParams.leftMargin + tempWidth / 2;
        centerY = getStickerViewParams.topMargin + tempHeight / 2;

        // 重绘
        postInvalidate();
    }

    // 判断是否是gif
    public boolean isGif(String url) {
        if (url.indexOf(".gif") > -1 || url.indexOf(".Gif") > -1 || url.indexOf(".GIF") > -1) {
            return true;
        }
        return false;
    }

    // 路径转换为Bitmap
    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 设置贴纸图片
     *
     * @param path
     */
    @SuppressWarnings("deprecation")
    public void setStickerImage(int resId) {

        // 记录贴纸本地地址

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

        if (bitmap != null) {

            IndexBoxWidth = bitmap.getWidth();
            IndexBoxHeight = bitmap.getHeight();
            setVisibility(View.VISIBLE);

            Matrix matrix = new Matrix();
            matrix.postTranslate(10, 10);
            StickerBackground.setImageMatrix(matrix);

            // 设置数据
            if (StickerBackground != null) {
                StickerBackground.setImageBitmap(bitmap);
                StickerBackground.setBackgroundDrawable(null);
                indexInvalidate();
            } else {
                setVisibility(View.GONE);
            }

        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * 是否是文字编辑贴纸
     *
     * @param edit
     */
    public void setEditType(boolean edit) {
        this.isEditType = edit;
    }

    /**
     * 设置字体间距
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setStickerPosition(float left, float top, float right, float bottom) {

        if (currRect != null)
            currRect.set(left, top, right, bottom);
        else
            currRect = new RectF(left, top, right, bottom);

        // 初始化没有成功前设置
        LayoutParams StickerEditViewParams = (LayoutParams) StickerEdittext.getLayoutParams();
        if (isEditType) {
            float l = boxWidth * currRect.left;
            float t = boxHeight * currRect.top;
            float r = boxWidth * currRect.right;
            float b = boxHeight * currRect.bottom;

            // 设置编辑框大小
            StickerEditViewParams.leftMargin = (int) l;
            StickerEditViewParams.topMargin = (int) t;
            StickerEditViewParams.rightMargin = (int) r;
            StickerEditViewParams.bottomMargin = (int) b;
        }
        StickerEdittext.setLayoutParams(StickerEditViewParams);
    }

    /**
     * 设置Hint文字
     *
     * @param str
     */
    public void setStickerHint(String str) {
        if (isEditType) {
            StickerEdittext.setHint(str);
        }
    }

    /**
     * 设置内容
     *
     * @param str
     */
    public void setStickerText(String str) {
        if (isEditType) {
            StickerEdittext.setText(str);
        }
    }

    /**
     * 是否隐藏编辑框
     *
     * @param isEditBox
     */
    public void setStickerEditBox(boolean isEditBox) {
        this.isEditBox = isEditBox;
        postInvalidate();
    }

    /**
     * 点击编辑按钮回调接口
     *
     * @author Aports
     */
    public interface onEditClickListener {
        public void click();
    }

    /**
     * 设置点击编辑回调事件
     *
     * @param listener
     */
    public void setOnEditClickListener(onEditClickListener listener) {
        this.listener = listener;
    }

    /**
     * 返回X坐标
     *
     * @return
     */
    public float getX() {
        return (currLeft + (mContentPadding * 2 + boxWidth) / 2) / (float) screenWidth * 100;
    }

    /**
     * 返回Y坐标
     *
     * @return
     */
    public float getY() {
        return (currTop + (mContentPadding * 2 + boxHeight) / 2) / (float) screenWidth * 100;
    }

    /**
     * 返回旋转角度
     *
     * @return
     */
    public float getRotation() {
        return currRotation;
    }

    /**
     * 获取放大比例
     *
     * @return
     */
    public float getScale() {
        // 计算比例
        return currScale * boxWidth / IndexBoxWidth;
    }

    /**
     * 获取图片显示的宽度缩放比例
     *
     * @return
     */
    public float getWidthRate() {
        return ((float) IndexBoxWidth) / screenWidth;
    }

    /**
     * 获取图片显示的高度缩放比例
     *
     * @return
     */
    public float getHeightRate() {
        return ((float) IndexBoxHeight) / screenWidth;
    }

    /**
     * 返回文字内容
     *
     * @return
     */
    public String getStickerText() {
        if (isEditType) {
            return StickerEdittext.getText().toString();
        } else {
            return "";
        }
    }

    /**
     * 返回文字内容的大小
     *
     * @return
     */
    public float getStickerTextSize() {
        if (isEditType) {
            return StickerEdittext.getEditSize();
        } else {
            return 0;
        }
    }

    /**
     * 返回字体颜色
     *
     * @return
     */
    public int getStickerTextColor() {
        if (isEditType) {
            return StickerEdittext.getTextColors().getDefaultColor();
        } else {
            return Color.BLACK;
        }
    }

    /**
     * 返回字体的间距
     *
     * @return
     */
    public RectF getStickerRectF() {
        return currRect;
    }

    /**
     * 返回字体线宽
     *
     * @return
     */
    public float getStickerStrokeWidth() {
        if (isEditType) {
            return StickerEdittext.getPaint().getStrokeWidth();
        } else {
            return 1.0f;
        }
    }

    /**
     * 返回字体间距宽度
     *
     * @return
     */
    public float getStickerSpacingMultiplier() {
        if (isEditType) {
            return StickerEdittext.getLayout().getSpacingMultiplier();
        } else {
            return 1.0f;
        }
    }

    /**
     * 返回贴纸路径
     *
     * @return
     */
    public String getStickerImage() {
        return currPath;
    }

    /**
     * 返回贴纸宽度 PX
     *
     * @return
     */
    public int getStickerWidth() {
        return IndexBoxWidth;
    }

    /**
     * 返回贴纸高度 PX
     *
     * @return
     */
    public int getStickerHeight() {
        return IndexBoxHeight;
    }

    /**
     * 返回是否关闭了贴纸
     *
     * @return
     */
    public boolean getIsClose() {
        if (getVisibility() == View.GONE) {
            return true;
        }
        return false;
    }

    /**
     * 返回贴纸贴纸是否是文字贴纸
     *
     * @return
     */
    public boolean getEditType() {
        return isEditType;
    }

    // 计算大小
    private int pxTopxUtils(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale) / 2;
    }

    // 获取屏幕宽度
    @SuppressWarnings("deprecation")
    public int getScreenWidth() {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    // 返回DIP适配不同手机
    public int dipToPX(float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }
}
