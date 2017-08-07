package com.xiuxiu.dialog;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.xiuxiu.R;
import com.xiuxiu.activity.VideoSubmitActivity;
import com.xiuxiu.adapter.ExpressionViewPagerAdapter;

//表情选择弹出框
public class ExpressionPopupWindow extends PopupWindow
{
	private View contentView = null;
	private Context context = null;	
	private ViewPager viewPager = null;
	private GridView gridView = null;
	private ExpressionViewPagerAdapter exAdapter = null;
	private ImageView mPage0 = null;//分页圆点显示
	private ImageView mPage1 = null;
	private ImageView mPage2 = null;
	private ImageView mPage3 = null;
	private int nPageIndex = 0;
	
	public ExpressionPopupWindow(Context _context)
	{
		super(_context);
		this.context = _context;
		
		//setWindowLayoutMode(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		setWidth(screenWidth);
		setHeight((int)(screenWidth * 0.6));
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.rounded_corners_pop));

		contentView = LayoutInflater.from(context).inflate(R.layout.popupwindow_layout, null);
		setContentView(contentView);
		
		exAdapter = new ExpressionViewPagerAdapter(context);
		viewPager = (ViewPager)contentView.findViewById(R.id.viewPager);
		viewPager.setAdapter(exAdapter);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		if(exAdapter.gridViews != null)
			gridView = exAdapter.gridViews.get(0);
		gridView.setOnItemClickListener(setExClickListener);
		
		//分页圆点
		mPage0 = (ImageView)contentView.findViewById(R.id.page0);
	    mPage1 = (ImageView)contentView.findViewById(R.id.page1);
	    mPage2 = (ImageView)contentView.findViewById(R.id.page2);
	    mPage3 = (ImageView)contentView.findViewById(R.id.page3);
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener
	{
		@Override
		public void onPageSelected(int arg0)
		{
			switch (arg0) 
			{
			case 0:		
				mPage0.setImageDrawable(contentView.getResources().getDrawable(R.drawable.current));
				mPage1.setImageDrawable(contentView.getResources().getDrawable(R.drawable.notcurrent));
				break;
			case 1:
				mPage1.setImageDrawable(contentView.getResources().getDrawable(R.drawable.current));
				mPage0.setImageDrawable(contentView.getResources().getDrawable(R.drawable.notcurrent));
				mPage2.setImageDrawable(contentView.getResources().getDrawable(R.drawable.notcurrent));
				break;
			case 2:
				mPage2.setImageDrawable(contentView.getResources().getDrawable(R.drawable.current));
				mPage1.setImageDrawable(contentView.getResources().getDrawable(R.drawable.notcurrent));
				mPage3.setImageDrawable(contentView.getResources().getDrawable(R.drawable.notcurrent));
				break;
			case 3:	
				mPage3.setImageDrawable(contentView.getResources().getDrawable(R.drawable.current));
				mPage2.setImageDrawable(contentView.getResources().getDrawable(R.drawable.notcurrent));
				break;
			default:
				return;
			}
			
			//获取分页gridView
			if(exAdapter.gridViews != null)
				gridView = exAdapter.gridViews.get(arg0);
			nPageIndex = arg0;//设置页面索引
			gridView.setOnItemClickListener(setExClickListener);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) 
		{
			
		}

		@Override
		public void onPageScrollStateChanged(int arg0) 
		{
			
		}
	}
	
	private GridView.OnItemClickListener setExClickListener = new GridView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			if(arg2 == 27)
			{
				((VideoSubmitActivity)context).deleteExpression();//删除表情
			}
			else
			{
				//添加表情
				((VideoSubmitActivity)context).addExpressionToEdit(arg2+nPageIndex*27, exAdapter.expressionIds);
			}
		}
	};
}
