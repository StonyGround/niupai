package com.xiuxiu.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.xiuxiu.R;
import com.xiuxiu.util.XConstant;

import java.io.InputStream;

// 图像/文字混合图层>>支持png/jpg/gif等格式
public class ImageLayer extends BaseLayer
{
	private Context context = null;

	// 图像
	private boolean isBmpVisible = false;
	private int bitmapType = 0, resId = 0, gifDuration = 0, playingTime = 0;
	private float mainTxtPosX = 0, mainTxtPosY = 0;
	private Rect bmpRect = null;
	private Bitmap bitmap = null;
	private BitmapFactory.Options options = null;
	private Movie gifMovie = null;
	private Bitmap cacheBitmap = null;
	private Canvas cacheCanvas = null;
	private Paint clearPaint = null;

	// 主文字
	private boolean isMainTxtVisible = false;
	private float lineSpacing = 0.9f;
	private int mainTextSize = 32, mainTextWidth = 0;
	private int mainTextColor = Color.WHITE;
	private Rect mainTxtRect = null;
	private StaticLayout staticLayout = null;
	private TextPaint mainTextPaint = null;
	private Paint mainTxtBorderPaint = null;

	// 副文字
	private float[] subTxtPosX = { 0.0f };
	private float[] subTxtPosY = { 0.0f };
	private boolean isSubTxtVisible = false;
	private int subTextSize = 20;
	private int subTextColor = Color.WHITE;
	private TextPaint subTextPaint = null;
	private String[] subTxtContent = { "" };

	private static final int BITMAP_TYPE_NOR = 0;
	private static final int BITMAP_TYPE_GIF = 1;
	private static final int FRAME_INTERVAL = 66;// 1秒15帧>>1帧约66毫秒;

	public ImageLayer(Context _context, int _layerId, boolean _isVisible)
	{
		super(_context, _layerId, _isVisible);

		context = _context;

		// 图像
		resId = R.drawable.icon_effects_null;
		bmpRect = new Rect();
		cacheCanvas = new Canvas();
		clearPaint = new Paint();
		options = new BitmapFactory.Options();
		options.inScaled = false;
		bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);

		// 文字
		mainTxtRect = new Rect();
		mainTextPaint = new TextPaint();
		mainTextPaint.setColor(mainTextColor);
		mainTextPaint.setTextSize(mainTextSize);
		mainTextPaint.setAntiAlias(true);
		mainTextPaint.setTypeface(Typeface.MONOSPACE);

		subTextPaint = new TextPaint();
		subTextPaint.setColor(subTextColor);
		subTextPaint.setTextSize(subTextSize);
		subTextPaint.setAntiAlias(true);
		subTextPaint.setTypeface(Typeface.MONOSPACE);

		// 文字边框
		mainTxtBorderPaint = new Paint();
		mainTxtBorderPaint.setColor(Color.WHITE);
		mainTxtBorderPaint.setStyle(Paint.Style.STROKE);
		mainTxtBorderPaint.setStrokeWidth(1);
		mainTxtBorderPaint.setAntiAlias(true);
		PathEffect effects = new DashPathEffect(new float[]{3, 5, 3, 5}, 1);
		mainTxtBorderPaint.setPathEffect(effects);
	}

	@Override
	public void drawLayer(Bitmap parentBmp)
	{
		// 清理canvas
		clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		baseCanvas.drawPaint(clearPaint);
		clearPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		baseCanvas.setBitmap(parentBmp);

		// 图像
		if (isBmpVisible)
		{
			if (bitmapType == BITMAP_TYPE_NOR)
			{
				// png/jpg
				baseCanvas.drawBitmap(bitmap, null, bmpRect, null);
			}
			else if (bitmapType == BITMAP_TYPE_GIF)
			{
				// gif
				playingTime += FRAME_INTERVAL;
		    	if (playingTime > gifDuration)
				{
		    		playingTime = 0;
				}

		    	cacheBitmap.eraseColor(Color.parseColor("#00000000"));
		    	gifMovie.setTime(playingTime);
				gifMovie.draw(cacheCanvas, 0, 0);
				baseCanvas.drawBitmap(cacheBitmap, null, bmpRect, null);
			}
		}

		// 主文字
		if (isMainTxtVisible)
		{
			// 绘制边框
			// baseCanvas.drawRect(mainTxtRect, mainTxtBorderPaint);

			// 绘制文字
			baseCanvas.translate(mainTxtPosX, mainTxtPosY);
			staticLayout.draw(baseCanvas);
			baseCanvas.translate(-mainTxtPosX, -mainTxtPosY);
		}

		// 副文字
		if (isSubTxtVisible)
		{
			for (int i = 0; i < subTxtContent.length; i++)
			{
				baseCanvas.drawText(subTxtContent[i], subTxtPosX[i], subTxtPosY[i], subTextPaint);
			}
		}
	}

	// 加载图像资源
	public void loadImage(int _resId, int posX, int posY)
	{
		isBmpVisible = true;
		resId = _resId;

		int bmpWidth = 0;
		int bmpHeight = 0;
		// 加载资源
		InputStream is = context.getResources().openRawResource(resId);
		gifMovie = Movie.decodeStream(is);
		if (gifMovie != null)
		{
			// gif图
			bitmapType = BITMAP_TYPE_GIF;
			Bitmap tmpBmp = BitmapFactory.decodeStream(is);
			bmpWidth  = tmpBmp.getWidth();
			bmpHeight = tmpBmp.getHeight();
			tmpBmp.recycle();

			if (cacheBitmap != null)
			{
				cacheBitmap.recycle();
			}
			cacheBitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Config.ARGB_8888);
			cacheCanvas.setBitmap(cacheBitmap);

			gifMovie.setTime(0);
			gifDuration = gifMovie.duration();
		}
		else
		{
			// png/jpg图
			bitmapType = BITMAP_TYPE_NOR;
			if (bitmap != null)
			{
				bitmap.recycle();
			}
			bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
			bmpWidth = bitmap.getWidth();
			bmpHeight = bitmap.getHeight();
		}

		// 计算位置
		if (bmpWidth > XConstant.FRAME_DST_WIDTH || bmpHeight > XConstant.FRAME_DST_HEIGHT)
		{
			bmpRect.left = 0;
			bmpRect.right = XConstant.FRAME_DST_WIDTH;
			bmpRect.top = 0;
			bmpRect.bottom = XConstant.FRAME_DST_HEIGHT;
		}
		else
		{
			bmpRect.left = posX;
			bmpRect.right = posX + bmpWidth;
			bmpRect.top = posY;
			bmpRect.bottom = posY + bmpHeight;
		}
	}

	// 加载图像资源
	public void loadBitmap(Bitmap _bitmap, int posX, int posY, int bmpWidth, int bmpHeight)
	{
		isBmpVisible = true;

		// png/jpg图
		bitmapType = BITMAP_TYPE_NOR;
		bitmap = _bitmap;

		// 计算位置
		bmpRect.left = posX;
		bmpRect.right = posX + bmpWidth;
		bmpRect.top = posY;
		bmpRect.bottom = posY + bmpHeight;
	}

	// 设置待显示的主文字及偏移量
	public Rect setMainTextContent(String textContent, int _textSize, int _textWidth, float posX, float posY)
	{
		isMainTxtVisible = true;
		mainTxtPosX = posX;
		mainTxtPosY = posY;
		mainTextSize = _textSize;
		mainTextPaint.setTextSize(mainTextSize);

		mainTextWidth = _textWidth;
		staticLayout = new StaticLayout(textContent, mainTextPaint, mainTextWidth, Alignment.ALIGN_NORMAL, lineSpacing, 0.0f, true);

		// 计算文字所占区域
		mainTxtRect.left = (int)posX;
		mainTxtRect.top  = (int)posY;
		mainTxtRect.right  = mainTxtRect.left + staticLayout.getWidth();
		mainTxtRect.bottom = mainTxtRect.top  + staticLayout.getHeight();

		return mainTxtRect;
	}

	// 设置待显示的副文字及偏移量
	public void setSubTextContent(String[] textContent, int _textSize, float[] posX, float[] posY)
	{
		isSubTxtVisible = true;
		subTxtContent = textContent;
		subTxtPosX = posX;
		subTxtPosY = posY;
		subTextSize = _textSize;
		subTextPaint.setTextSize(subTextSize);
	}

	// 更新主文字内容
	public Rect updateMainTextContent(String textContent)
	{
		isMainTxtVisible = true;
		Rect lineRect = new Rect();
		mainTextPaint.getTextBounds(textContent, 0, textContent.length(), lineRect);
		int newTextWidth = lineRect.width() < mainTextWidth ? lineRect.width() : mainTextWidth;
		staticLayout = new StaticLayout(textContent, mainTextPaint, newTextWidth, Alignment.ALIGN_NORMAL, lineSpacing, 0.0f, true);
		mainTxtRect.right  = mainTxtRect.left + staticLayout.getWidth();
		mainTxtRect.bottom = mainTxtRect.top  + staticLayout.getHeight();

		return mainTxtRect;
	}

	// 设置字体参数
	public void setMainTxtFontSize(int _textSize)
	{
		mainTextSize = _textSize;
		mainTextPaint.setTextSize(mainTextSize);
	}

	public void setSubTxtFontSize(int _textSize)
	{
		subTextSize = _textSize;
		subTextPaint.setTextSize(subTextSize);
	}

	public void setMainTxtFontColor(int _textColor)
	{
		mainTextColor = _textColor;
		mainTextPaint.setColor(mainTextColor);
	}

	public void setSubTxtFontColor(int _textColor)
	{
		subTextColor = _textColor;
		subTextPaint.setColor(subTextColor);
	}

	// 重置参数
	public void resetParams()
	{
		isBmpVisible = false;
		isMainTxtVisible = false;
		isSubTxtVisible = false;
	}
}
