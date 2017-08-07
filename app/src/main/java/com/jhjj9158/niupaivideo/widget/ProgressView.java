package com.jhjj9158.niupaivideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {
    private Paint paint = new Paint(1);
    private SweepGradient sweepGradientDefault = new SweepGradient(0.0F, 0.0F, new int[]{526629119, 1063500031, 1600370943, 2137241855, -1620854529, -1083983617, -547112705, -10241793}, null);

    public ProgressView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.paint.setStyle(Style.STROKE);
    }

    public void setSweepGradient(int[] colors) {
        if (colors.length != 8) throw new IllegalArgumentException();
        sweepGradientDefault = new SweepGradient(0, 0, colors, null);
        this.invalidate();
    }

    protected void onDraw(Canvas paramCanvas) {
        int width = getWidth();
        int height = getHeight();

        float f1 = 0.21F * height;
        float f2 = 0.05F * height;

        paramCanvas.translate(width / 2, height / 2);

        this.paint.setStrokeWidth(f2);

        this.paint.setShader(sweepGradientDefault);

        paramCanvas.drawCircle(0.0F, 0.0F, f1, this.paint);

    }
}