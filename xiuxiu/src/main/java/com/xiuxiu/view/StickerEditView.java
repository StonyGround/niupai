package com.xiuxiu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/**
 * 贴纸编辑显示背景图片类
 * 
 * @author Aports
 * 
 */
public class StickerEditView extends RelativeLayout {

	private Context context;

	private CoreImageView ImageBackground;

	private CoreStickerView Sticker;

	public StickerEditView(Context context) {
		super(context);
		init(context);
	}

	public StickerEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StickerEditView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		initView();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("ClickableViewAccessibility")
	private void initView() {
		ImageBackground = new CoreImageView(context);
		ImageBackground.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(ImageBackground, 0);

		Sticker = new CoreStickerView(context);
		addView(Sticker, 1);

		ImageBackground.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Sticker.setStickerEditBox(true);
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	/**
	 * 设置背景图
	 * 
	 * @param uri
	 */
	public void setBackgroundURI(Uri uri) {
		if (ImageBackground != null)
			ImageBackground.setImageURI(uri);
	}

	/**
	 * 设置背景图
	 * 
	 * @param drawable
	 */
	public void setBackgroundDrawable(Drawable drawable) {
		if (ImageBackground != null)
			ImageBackground.setImageDrawable(drawable);
	}

	/**
	 * 设置背景图
	 * 
	 * @param bm
	 */
	public void setBackgroundBitmap(Bitmap bm) {
		if (ImageBackground != null)
			ImageBackground.setImageBitmap(bm);
	}

	/**
	 * 设置背景图
	 * 
	 * @param resId
	 */
	public void setBackgroundResource(int resId) {
		if (ImageBackground != null)
			ImageBackground.setImageResource(resId);
	}

	/**
	 * 设置裁剪模式
	 * 
	 * @param scaleType
	 */
	public void setScaleType(ScaleType scaleType) {
		if (ImageBackground != null)
			ImageBackground.setScaleType(scaleType);
	}

	/**
	 * 返回贴纸对象
	 * 
	 * @return
	 */
	public CoreStickerView getStickerView() {
		return Sticker;
	}

	/**
	 * 返回贴纸背景图片对象
	 * 
	 * @return
	 */
	public CoreImageView getBackgroundView() {
		return ImageBackground;
	}
}
