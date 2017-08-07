package com.xiuxiu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiuxiu.R;

/**
 * @author zhangyu
 * 视频发布界面"分享到"按钮
 * 2014-7-3
 */
public class ShareItemListAdapter extends BaseAdapter
{
	private Context context;
	private int imgStrName[] =null;  //要显示的字符串数据
	private int imgIconId[] = null;  //未选中时的图标
	private int imgIconUpId[] = null;//选中后的图标
	private boolean bClickItem[] = {false,false,false,false,false,false};//选中状态  默认第一个为选中状态
	
	public ShareItemListAdapter(Context context)
	{
		this.context = context;
	}
	
	public ShareItemListAdapter(Context context, int[] strNameId, int[] imgIconId, int[] imgIconUpId)
	{
		super();  
        this.imgStrName = strNameId;  
        this.imgIconId = imgIconId;  
        this.imgIconUpId = imgIconUpId;
        this.context = context;  
	}
	
	@Override
	public int getCount()
	{
		return imgStrName.length;
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		HolderView holder = null;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.share_item, null);
			holder=new HolderView();
			holder.text=(TextView) convertView.findViewById(R.id.ItemText);
            holder.img=(ImageView) convertView.findViewById(R.id.ItemImage);
            convertView.setTag(holder); 
		}
		else
		{
			holder=(HolderView) convertView.getTag();  
		}
		
		 holder.text.setText(context.getString(imgStrName[position])); 
		 //item为选中状态时,显示选中状态的图标
		 if(bClickItem[position])
		 {
			 holder.img.setImageResource(imgIconUpId[position]);
		 }
		 else
		 {
			 holder.img.setImageResource(imgIconId[position]);
		 }
	     
	     return convertView;  
	}
	
	private class HolderView
	{      
        TextView text = null;
        ImageView img = null;
    }  
	
	//设置选中状态
	public boolean changeItem(int index)
	{
		boolean bClicked = false;
		for(int i = 0; i < bClickItem.length; i++)
		{
			if(index == i)
			{
				bClickItem[index] = !bClickItem[index];
				bClicked = bClickItem[index];
			}
			else
			{
				bClickItem[i] = false;
			}
		}
		
		return bClicked;
	}
}
