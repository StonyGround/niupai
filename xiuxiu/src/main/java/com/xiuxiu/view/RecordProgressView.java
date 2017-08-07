package com.xiuxiu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.xiuxiu.activity.RecordActivity;
import com.xiuxiu.activity.RecordActivity.GuideResourceIndex;
import com.xiuxiu.util.XConstant;

import java.util.ArrayList;
import java.util.List;

// 录制进度条
public class RecordProgressView extends View {
    private static final String TAG = "RecordProgressView";

    private int progressVal = 0, thresholdPos = 0;
    private int viewWidth = 0, viewHeight = 0;
    private int totalRecordTime = 0, curRecordTime = 0, maxRecordTime = 0;// 总时长/已录制时长/最大录制时长
    private long refreshTime = 0, twinkleTime = 0;// 刷新时间/进度提示时间
    private float pixelsMappingVal = 0.0f;// 时间与像素映射
    private boolean isDrawingTwinkle = true, isAVRecording = false, isUpdateRecBtnStatus = false;
    private RecordActivity recordCtrl = null;
    // 画笔
    private Paint progressPaint = null, twinklePaint = null, thresholdPaint = null, breakPaint = null;
    private List<Integer> listBreakPoints = null, recordTimeList = null;
    private GuideResourceIndex gRIndex = GuideResourceIndex.INITIALIZES_GUIDE;//初始化新手引导索引页

    public RecordProgressView(Context context) {
        super(context);
    }

    public RecordProgressView(Context context, RecordActivity _recordCtrl, int _viewWidth, int _viewHeight) {
        super(context);

        viewWidth = _viewWidth;
        viewHeight = _viewHeight;
        listBreakPoints = new ArrayList<>();
        recordTimeList = new ArrayList<>();
        twinkleTime = SystemClock.uptimeMillis();
        recordCtrl = _recordCtrl;

        // 进度条主色调
        progressPaint = new Paint();
        progressPaint.setColor(Color.parseColor("#FF465E"));//19e3cf
        // 闪烁颜色
        twinklePaint = new Paint();
        twinklePaint.setColor(Color.parseColor("#ffcc42"));
        // 阈值处颜色
        thresholdPaint = new Paint();
        thresholdPaint.setColor(Color.parseColor("#3aa5da"));//1e90ff
        // 连接处颜色
        breakPaint = new Paint();
        breakPaint.setColor(Color.parseColor("#ffffff"));

        // 背景
        setBackgroundColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 计算录制进度
        if (isAVRecording) {
            curRecordTime += SystemClock.uptimeMillis() - refreshTime;
            progressVal = (int) (pixelsMappingVal * curRecordTime);
            refreshTime = SystemClock.uptimeMillis();
        }

        // 阈值
        canvas.drawRect(thresholdPos, 0, thresholdPos + 4, viewHeight, thresholdPaint);
        // 主进度条
//        Log.d(TAG, "onDraw: " + progressVal + "---" + totalRecordTime);
        canvas.drawRect(0, 0, progressVal, viewHeight, progressPaint);
        // 分段录制中的断点
        for (Integer val : listBreakPoints) {
            canvas.drawRect(val, 0, val + 2, viewHeight, breakPaint);
        }

        // 绘制闪烁
        if (SystemClock.uptimeMillis() - twinkleTime > 400) {
            isDrawingTwinkle = !isDrawingTwinkle;
            twinkleTime = SystemClock.uptimeMillis();
        }

        if (isAVRecording) {
            canvas.drawRect(progressVal, 0, progressVal + 5, viewHeight, twinklePaint);
        } else {
            if (isDrawingTwinkle) {
                canvas.drawRect(progressVal, 0, progressVal + 5, viewHeight, twinklePaint);
            }
        }

        // 检测录制最小时长
        if (!isUpdateRecBtnStatus && (curRecordTime > XConstant.RECORD_NOR_MIN_TIME)) {
            isUpdateRecBtnStatus = false;
            recordCtrl.updateRecordNextBtn(true);
        }else{
            isUpdateRecBtnStatus = false;
            recordCtrl.updateRecordNextBtn(false);
        }

        //新手引导操作
//        if (recordCtrl.getGuideState()) {
//            //录制时长>3s,则发送stop消息强制停止录制
//            if (gRIndex == GuideResourceIndex.INITIALIZES_GUIDE && curRecordTime > XConstant.RECORD_NOR_MIN_TIME) {
//                gRIndex = GuideResourceIndex.SECOND_GUIDE;
//                recordCtrl.sendRecordMessage(XConstant.RECORD_STATUS_STOP);
//                recordCtrl.updateRecordNextBtn(false);//更改由检测最小时长函数刷新的"下一步"按钮状态为不可用状态
//            } else if (gRIndex == GuideResourceIndex.SECOND_GUIDE && curRecordTime >= 5000) {
//                //录制时长>5s,则切换新手引导标语提示
//                recordCtrl.setGuideImageViewResource(GuideResourceIndex.FIFTH_GUIDE);
//                recordCtrl.updateRecordNextBtn(true);//"下一步"按钮为可用状态
//            }
//        }

        // 检测是否录制完成
        if (curRecordTime > totalRecordTime || curRecordTime > maxRecordTime) {
            recordCtrl.sendRecordMessage(XConstant.RECORD_STATUS_STOP);
        } else {
            invalidate();
        }
    }

    // 更新刷新时间
    public void updateRefreshTime() {
        refreshTime = SystemClock.uptimeMillis();
    }

    // 设置进度参数
    public void setProgressParams(int _totalRecordTime, int _maxRecordTime) {
        maxRecordTime = _maxRecordTime;
        totalRecordTime = _totalRecordTime;
        if (_totalRecordTime > XConstant.RECORD_NOR_MAX_TIME) {
            _totalRecordTime = XConstant.RECORD_NOR_MAX_TIME;
        }
        pixelsMappingVal = (float) viewWidth / _totalRecordTime;
        thresholdPos = (int) (XConstant.RECORD_NOR_MIN_TIME * pixelsMappingVal);

        // 重置参数
        progressVal = 0;
        curRecordTime = 0;
        isUpdateRecBtnStatus = false;
        listBreakPoints.clear();
        recordTimeList.clear();
    }

    // 添加断点
    public void addBreakPoints() {
        listBreakPoints.add(progressVal);
        recordTimeList.add(curRecordTime);
    }

    // 删除断点
    public void deleteBreakPoints() {

        if (listBreakPoints.size() == 1) {
            progressVal = 0;
            curRecordTime = 0;
            listBreakPoints.clear();
            recordTimeList.clear();
        } else if (listBreakPoints.size() > 1) {
            listBreakPoints.remove(listBreakPoints.size() - 1);
            recordTimeList.remove(recordTimeList.size() - 1);
            progressVal = listBreakPoints.get(listBreakPoints.size() - 1);
            curRecordTime = recordTimeList.get(recordTimeList.size() - 1);
        }

        invalidate();
    }

    // 当前录制状态
    public void setAVRecording(boolean isAVRecording) {
        this.isAVRecording = isAVRecording;
    }

    // 获取录制时长
    public int getCurRecordTime() {
        return curRecordTime;
    }
}
