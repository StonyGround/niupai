package com.xiuxiu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.xiuxiu.activity.RecordActivity;
import com.xiuxiu.util.AccSongInfoParse.LyricsLine;
import com.xiuxiu.util.AccSongInfoParse.LyricsWord;

import java.util.List;

// 伴奏歌曲歌词绘制页面
public class AccSongLyricsView extends View {
    private static final String TAG = "AccSongLyricsView";

    private int wordWidth = 100, curLineTotalWidth = 0;
    private int textSize = 20;
    private int lineSpacing = 0;
    private int lineOffset = 0;
    private int curLinePosX = 0, curLinePosY = 0, offsetX = 0;
    private int offsetCount = 0;
    private int curLineIndex = 0, preLineIndex = -999;
    private int viewWidth = 0, viewHeight = 0;
    private int txtTemplateIndex = 0;
    private double percent = 1.0;

    Paint currTextPaint = null, prevTextPaint = null, nextTextPaint = null;
    Paint clipTextPaint = null, outlinePaint = null;

    String currLine = ""; // 当前行
    String nextLine = ""; // 下一行
    String prevLine = ""; // 前一行
    String pprevLine = "";// 前两行

    List<LyricsLine> listLyricsLine = null;
    Rect lineRect = new Rect();
    Rect lineClipRect = new Rect();
    Rect lyricsRect = new Rect();

    public AccSongLyricsView(Context context) {
        super(context);
    }

    public AccSongLyricsView(Context context, int _viewWidth, int _viewHeight) {
        super(context);
        viewWidth = _viewWidth;
        viewHeight = _viewHeight;
        textSize = (int) (viewHeight * 0.25);
        lineSpacing = (int) (textSize * 1.4);
        lineOffset = lineSpacing;

        // 当前行歌词
        currTextPaint = new Paint();
        currTextPaint.setColor(Color.parseColor("#ffffff"));
        currTextPaint.setTextSize(textSize);
        currTextPaint.setAntiAlias(true);
        currTextPaint.setTypeface(Typeface.MONOSPACE);

        // 前一行歌词
        prevTextPaint = new Paint();
        prevTextPaint.setColor(Color.parseColor("#ffffff"));
        prevTextPaint.setTextSize(textSize);
        prevTextPaint.setAntiAlias(true);
        prevTextPaint.setAlpha(178);
        prevTextPaint.setTypeface(Typeface.MONOSPACE);

        // 下一行歌词
        nextTextPaint = new Paint();
        nextTextPaint.setColor(Color.parseColor("#ffffff"));
        nextTextPaint.setTextSize(textSize);
        nextTextPaint.setAntiAlias(true);
        nextTextPaint.setAlpha(178);
        nextTextPaint.setTypeface(Typeface.MONOSPACE);

        // 已唱完歌词
        clipTextPaint = new Paint();
        clipTextPaint.setColor(Color.parseColor("#33ff0b"));
        clipTextPaint.setTextSize(textSize);
        clipTextPaint.setAntiAlias(true);
        clipTextPaint.setTypeface(Typeface.MONOSPACE);

        // 歌词轮廓描边
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.parseColor("#666666"));
        outlinePaint.setTextSize(textSize);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(2);
        outlinePaint.setAntiAlias(true);
        outlinePaint.setTypeface(Typeface.MONOSPACE);

        // 计算歌词剪辑区域
        lyricsRect.top = 0;
        lyricsRect.bottom = viewHeight;
        lyricsRect.left = curLinePosX;
        lyricsRect.right = lyricsRect.left + viewWidth;

        this.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d(TAG, "onDraw: ");
        curLinePosX = 0;
        curLinePosY = textSize + lineSpacing;

        // 设置歌词剪辑区域
        // canvas.drawRect(lyricsRect, outlinePaint);
        canvas.clipRect(lyricsRect);

        // 绘制前二行/前一行歌词及下一行歌词
        prevTextPaint.getTextBounds(pprevLine, 0, pprevLine.length(), lineRect);
        offsetX = (viewWidth - lineRect.width()) >> 1;
        drawText(canvas, pprevLine, curLinePosX + offsetX, curLinePosY + lineOffset - 2 * lineSpacing, prevTextPaint, outlinePaint);

        prevTextPaint.getTextBounds(prevLine, 0, prevLine.length(), lineRect);
        offsetX = (viewWidth - lineRect.width()) >> 1;
        drawText(canvas, prevLine, curLinePosX + offsetX, curLinePosY + lineOffset - lineSpacing, prevTextPaint, outlinePaint);

        nextTextPaint.getTextBounds(nextLine, 0, nextLine.length(), lineRect);
        offsetX = (viewWidth - lineRect.width()) >> 1;
        drawText(canvas, nextLine, curLinePosX + offsetX, curLinePosY + lineOffset + lineSpacing, nextTextPaint, outlinePaint);

        // 计算当前行已唱部分所占矩形区域
        currTextPaint.getTextBounds(currLine, 0, currLine.length(), lineRect);
        offsetX = (viewWidth - lineRect.width()) >> 1;
        curLinePosX += offsetX;
        lineRect.top += curLinePosY;
        lineRect.bottom += curLinePosY;
        lineRect.left += curLinePosX;
        lineRect.right += curLinePosX;

        lineClipRect.top = lineRect.top + lineOffset;
        lineClipRect.bottom = lineRect.bottom + lineOffset;
        lineClipRect.left = lineRect.left;
        lineClipRect.right = (int) (lineRect.left + lineRect.width() * percent);

        // 绘制当前行歌词及已唱过歌词
        drawText(canvas, currLine, curLinePosX, curLinePosY + lineOffset, currTextPaint, outlinePaint);
        canvas.clipRect(lineClipRect);
        drawText(canvas, currLine, curLinePosX, curLinePosY + lineOffset, clipTextPaint, outlinePaint);

        super.onDraw(canvas);
    }

    // 绘制文字
    private void drawText(Canvas canvas, String content, int posX, int posY, Paint textPaint, Paint outerPaint) {
//        Log.d(TAG, "drawText: " + content);
        canvas.drawText(content, posX, posY, outerPaint);
        canvas.drawText(content, posX, posY, textPaint);
    }

    // 重置相关参数
    public void resetParams() {
//        Log.d(TAG, "resetParams: ");
        currLine = "";
        prevLine = "";
        pprevLine = "";
        nextLine = "";
        lineOffset = 0;
        offsetCount = 0;
        preLineIndex = -999;
        txtTemplateIndex = 0;
        percent = 1.0;

        invalidate(lyricsRect);
    }

    // 计算歌词演唱进度
    public void compLyrics(int currentTime) {
        if (listLyricsLine == null) {
            return;
        }

        int lineCount = listLyricsLine.size();
        for (int k = 0; k < lineCount; k++) {
            LyricsLine lyricsLine = listLyricsLine.get(k);
            if ((currentTime > lyricsLine.beginTime) && (currentTime < lyricsLine.endTime)) {
                // 记录当前行序号
                curLineIndex = k;
                // 当前行/前二行/前一行/下一行歌词
                currLine = lyricsLine.line;
                pprevLine = prevLine = (k > 1) ? listLyricsLine.get(k - 2).line : "";
                prevLine = (k > 0) ? listLyricsLine.get(k - 1).line : "";
                nextLine = (k < lineCount - 1) ? listLyricsLine.get(k + 1).line : "";

                // 当前行已演唱长度及总长度
                int lineWidth = 0;
                curLineTotalWidth = currLine.length() * wordWidth;
                for (int i = 0; i < lyricsLine.listLyricsWord.size(); i++) {
                    LyricsWord lyricsWord = lyricsLine.listLyricsWord.get(i);
                    if ((currentTime > lyricsWord.beginTime) && (currentTime < lyricsWord.endTime)) {
                        lineWidth = i * wordWidth;
                        percent = (double) (lineWidth + wordWidth * (double) (currentTime - lyricsWord.beginTime) / lyricsWord.duration)
                                / curLineTotalWidth;
                        // Log.d("dd_cc_dd", "percent：" + percent);
                        break;
                    }
                }

                // 计算歌词换句时垂直坐标偏移量
                if (preLineIndex != curLineIndex) {
                    lineOffset = lineSpacing - 3 * offsetCount;
                    offsetCount++;
                    if (lineOffset < 0) {
                        lineOffset = 0;
                        offsetCount = 0;
                        preLineIndex = curLineIndex;
                    }
                }

                break;
            }// if
        }// for

        // 更新ui
        invalidate(lyricsRect);
    }// void compLyrics()

    // 计算文字模板播放进度
    public void compTxtTemplate(RecordActivity recordCtrl, int currentTime) {
        if (listLyricsLine == null) {
            return;
        }

        int lineCount = listLyricsLine.size();
        for (int k = 0; k < lineCount; k++) {
            LyricsLine lyricsLine = listLyricsLine.get(k);
            if ((currentTime >= lyricsLine.beginTime) && (currentTime < lyricsLine.endTime)) {
                currLine = lyricsLine.line;

                invalidate(lyricsRect);
                break;
            }// if
        }// for
    }// void compLyrics()

    // 更新文字模板
    public void updateTxtTemplate(RecordActivity recordCtrl) {
        if (listLyricsLine == null) {
            return;
        }

        if (txtTemplateIndex < listLyricsLine.size()) {
            LyricsLine lyricsLine = listLyricsLine.get(txtTemplateIndex++);
            currLine = lyricsLine.line;

            invalidate(lyricsRect);
        }
    }

    // 设置伴奏歌曲歌词数据信息
    public void setListLyricsLine(List<LyricsLine> listLyricsLine) {
        this.listLyricsLine = listLyricsLine;
    }

    // 刷新
    public void refreshView() {
        invalidate(lyricsRect);
    }
}