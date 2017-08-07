package com.xiuxiu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 正方形图片
 * 
 * @author Aports
 * 
 */
public class CoreImageView extends ImageView {

	public CoreImageView(Context context) {
		super(context);
	}

	public CoreImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CoreImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

}
