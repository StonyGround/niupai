package com.xiuxiu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.xiuxiu.R;

public class AutoFitSizeTextView extends android.support.v7.widget.AppCompatTextView {

    public AutoFitSizeTextView(Context context) {
        super(context);
    }

    public AutoFitSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs,
                R.styleable.MyTextView);
        int i = type.getInteger(R.styleable.MyTextView_textSizePx, 25);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSize(i));
        type.recycle();
    }

    @Override
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSize(Float.valueOf(size).intValue()));
    }

    private int getFontSize(int textSize) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        return (int) (textSize * (float) screenHeight / 1280);
    }
}
