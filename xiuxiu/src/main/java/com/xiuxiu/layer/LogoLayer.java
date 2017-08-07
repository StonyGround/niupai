package com.xiuxiu.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.xiuxiu.R;
import com.xiuxiu.util.Util;
import com.xiuxiu.util.XConstant;

// logo图层
public class LogoLayer extends BaseLayer {
    private int logoAlpha = 0;
    private String userName = "";
    private Rect logoRect = null;
    private Bitmap logoBmp = null;
    private Paint logoBmpPaint = null;
    private Paint clearPaint = null;
    private int textPosX = 0, textPosY = 0;
    private TextPaint logoTextPaint = null;

    public LogoLayer(Context context, int _layerId, boolean _isVisible) {
        super(context, _layerId, _isVisible);

        logoBmpPaint = new Paint();
        logoTextPaint = new TextPaint();
        logoTextPaint.setColor(Color.WHITE);
        logoTextPaint.setTextSize(Util.px2sp(context, 50));
        logoTextPaint.setAntiAlias(true);
        logoTextPaint.setTypeface(Typeface.MONOSPACE);

        clearPaint = new Paint();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        logoBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.video_edit_logo, options);

        logoRect = new Rect();
        int logoW = logoBmp.getWidth() >> 1;
        int logoH = logoBmp.getHeight() >> 1;
        logoRect.left = (XConstant.FRAME_DST_WIDTH - logoW) >> 1;
        logoRect.right = logoRect.left + logoW;
        logoRect.top = (XConstant.FRAME_DST_HEIGHT - logoH) >> 1;
        logoRect.bottom = logoRect.top + logoH;
    }

    @Override
    public void drawLayer(Bitmap parentBmp) {
        // 清理canvas
        clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        baseCanvas.drawPaint(clearPaint);
        clearPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        baseCanvas.setBitmap(parentBmp);

        if (logoAlpha != 0) {
            // 绘制logo
            logoBmpPaint.setAlpha(logoAlpha);
            baseCanvas.drawBitmap(logoBmp, null, logoRect, logoBmpPaint);

            // 绘制文字
            logoTextPaint.setAlpha(logoAlpha);
            baseCanvas.drawText(userName, textPosX, textPosY, logoTextPaint);
        }
    }

    // 更新不透明度
    public void updateLogoAlpha(int frameIndex, int frameThreshold, int logoFrameCount) {
        if (frameIndex > frameThreshold) {
            double val = (double) (frameIndex - frameThreshold) / logoFrameCount;
            val = (1 - Math.cos(val * 3.1415926)) * 0.5;
            logoAlpha = (int) (255.0 * val);
        } else {
            logoAlpha = 0;
        }
    }

    // 设置用户名
    public void setUserName(String _userName) {
        userName =" 出品:" + _userName;
        Rect lineRect = new Rect();
        logoTextPaint.getTextBounds(userName, 0, userName.length(), lineRect);

        // 过长昵称,裁剪后只显示一部分
        while (lineRect.width() > (XConstant.FRAME_DST_WIDTH / 2)) {
            int len = _userName.length();
            _userName = _userName.substring(0, len - 1);
            userName = _userName + "出品:";
            logoTextPaint.getTextBounds(userName, 0, userName.length(), lineRect);
        }

        textPosX = logoRect.left + logoRect.width() / 3;
        int dy = (logoRect.height() / 2 - lineRect.height()) / 2;
        textPosY = logoRect.top + logoRect.height() / 2 + lineRect.height() + dy;
    }
}
