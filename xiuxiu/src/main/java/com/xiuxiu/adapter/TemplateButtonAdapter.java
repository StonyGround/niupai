package com.xiuxiu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.model.TemplateVideoInfo;

import java.util.List;

// 模板数据映射
public class TemplateButtonAdapter extends BaseAdapter
{
	private Context context;
	private List<TemplateVideoInfo> iconBtnList;
	
	public TemplateButtonAdapter(Context _context)
	{
		context = _context;
	}
	
	public List<TemplateVideoInfo> getIconBtnList()
	{
		return iconBtnList;
	}

	public void setIconBtnList(List<TemplateVideoInfo> iconBtnList)
	{
		this.iconBtnList = iconBtnList;
	}
	
	@Override
	public int getCount()
	{
		return (iconBtnList != null)? iconBtnList.size() : 0;
	}

	@Override
	public Object getItem(int position)
	{
		return (iconBtnList != null)? iconBtnList.get(position) : null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public class ViewHolder
	{
		ImageView btnIcon;
		ImageView btnSelected;
		TextView btnText;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final TemplateVideoInfo btnInfo = (TemplateVideoInfo)getItem(position);
		ViewHolder viewHolder = null;
		
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.icon_button, null);
			viewHolder = new ViewHolder();
			viewHolder.btnIcon = (ImageView)convertView.findViewById(R.id.btnIcon);
			viewHolder.btnSelected = (ImageView)convertView.findViewById(R.id.btnSelected);
			viewHolder.btnText = (TextView)convertView.findViewById(R.id.btnText);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		// 设置按钮图标及文字
		viewHolder.btnIcon.setImageResource(btnInfo.getIconId());
//		Bitmap bmp = null;
//		if (position <= 1)
//		{
//			bmp = Util.loadImageFromSD(XConstant.RES_ICON_FILE_PATH + "icon_effects_null.png");
//			viewHolder.btnIcon.setImageBitmap(bmp);
//		}
//		else
//		{
//			String strIconFile = "";
//			String strFileName = btnInfo.getVideoFilePath();
//			File file = new File(strFileName);
//			if (!file.exists())
//			{
//				strIconFile = XConstant.RES_ICON_FILE_PATH + "default.png";
//			}
//			else
//			{
//				String strIconName = btnInfo.getVideoName();
//				strIconName = strIconName.substring(0, strIconName.lastIndexOf(".")) + ".png";
//				strIconFile = XConstant.RES_ICON_FILE_PATH + strIconName;
//				File iconFile = new File(strIconFile);
//				if (!iconFile.exists())
//				{
//					strIconFile = XConstant.RES_ICON_FILE_PATH + "default.png";
//				}
//			}
//			bmp = Util.loadImageFromSD(strIconFile);
//			viewHolder.btnIcon.setImageBitmap(bmp);
//		}
		
		viewHolder.btnText.setText(btnInfo.getIconText());
		
		// 设置选中/未选中项状态
		if (iconBtnList.get(position).isSelected())
		{
			viewHolder.btnSelected.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.btnSelected.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	// 切换选中项
	public void changeItem(int index)
	{
		// 更新选中状态
		for (TemplateVideoInfo iconBtn : iconBtnList)
		{
			iconBtn.setSelected(false);
		}
        iconBtnList.get(index).setSelected(true);
    }
}
