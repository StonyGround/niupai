package com.xiuxiu.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.xiuxiu.R;
import com.xiuxiu.util.Expressions;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author zhangyu
 * 表情列表适配器
 * 2014-7-23
 */
public class ExpressionViewPagerAdapter extends PagerAdapter
{
	private Context context = null;
	public ArrayList<GridView> gridViews = null;
	private SimpleAdapter sa = null;
	public  int[] expressionIds = Expressions.expressionImgs;//表情资源
	
	public ExpressionViewPagerAdapter(Context _context)
	{
		context = _context;
		initGridView();
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0 == arg1;
	}

	@Override
	public int getCount() 
	{
		return gridViews.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object)
	{
		((ViewPager)container).removeView(gridViews.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position)
	{
		((ViewPager)container).addView(gridViews.get(position));
		return gridViews.get(position);
	}
	
	//初始化表情列表
	private void initGridView()
	{
		gridViews = new ArrayList<GridView>();
		for(int i = 0; i < 4; i++)
		{
			GridView view_page = new GridView(context);
			view_page.setNumColumns(7);//7列
			view_page.setHorizontalSpacing(1);//列间距
			view_page.setVerticalSpacing(15);//行间距
			view_page.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			//填充数据
			ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map = null;
			for (int j = 0; j < 27; j++)//每页27个表情 + 删除按钮
			{
				map = new HashMap<String, Object>();
				if((j+i*27) >= expressionIds.length-1) //最后一页表情未满27个 则用NULL填充
				{
					map.put("emotion", null);
				}
				else
				{
					map.put("emotion", expressionIds[j+i*27]);//插入表情数据
				}
				arrayList.add(map);
			}
			
			map = new HashMap<String,Object>(); //将最后一个删除按钮插入到map的末尾
			map.put("emotion",expressionIds[expressionIds.length-1]);
			arrayList.add(map);
			
			sa = new SimpleAdapter(context, arrayList, R.layout.expression_gridlist, new String[]{"emotion"}, new int[]{R.id.expressionIcon});
			view_page.setAdapter(sa);
			gridViews.add(view_page);
		}
	}
}
