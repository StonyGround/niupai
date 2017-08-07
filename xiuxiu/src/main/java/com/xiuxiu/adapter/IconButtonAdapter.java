package com.xiuxiu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.xiuxiu.R;
import com.xiuxiu.model.IconButtonInfo;
import com.xiuxiu.util.XConstant;

import java.util.List;

// icon图标按钮映射
public class IconButtonAdapter extends BaseAdapter
{
	private boolean isOriginalVoiceOff = false;
	private Context context;
	private List<IconButtonInfo> iconBtnList;
	
	public IconButtonAdapter(Context _context) 
	{
		context = _context;
	}
	
	public List<IconButtonInfo> getIconBtnList()
	{
		return iconBtnList;
	}

	public void setIconBtnList(List<IconButtonInfo> iconBtnList)
	{
		this.iconBtnList = iconBtnList;
	}

	@Override
	public int getCount() 
	{
		return (iconBtnList != null) ? iconBtnList.size() : 0;
	}

	@Override
	public Object getItem(int position) 
	{
		return (iconBtnList != null) ? iconBtnList.get(position) : null;
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
		TextView  btnText;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final IconButtonInfo btnInfo = (IconButtonInfo)getItem(position);
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
		viewHolder.btnIcon.setImageResource(btnInfo.getResId());
//		if (btnInfo.getResType() != XConstant.CLASS_ID_INCMUSIC)
//		{
//			viewHolder.btnIcon.setImageResource(btnInfo.getResId());
//		}
//		else
//		{
//			Bitmap bmp = null;
//			if (position == 0)
//			{
//				bmp = Util.loadImageFromSD(XConstant.RES_ICON_FILE_PATH + "icon_sound_off.png");
//				viewHolder.btnIcon.setImageBitmap(bmp);
//			}
//			else if (position == 1)
//			{
//				bmp = Util.loadImageFromSD(XConstant.RES_ICON_FILE_PATH + "icon_effects_null.png");
//				viewHolder.btnIcon.setImageBitmap(bmp);
//			}
//			else
//			{
//				String strIconFile = "";
//				String strFileName = btnInfo.getSongFilePath();
//				File file = new File(strFileName);
//				if (!file.exists())
//				{
//					strIconFile = XConstant.RES_ICON_FILE_PATH + "default.png";
//				}
//				else
//				{
//					String strIconName = strFileName.substring(strFileName.lastIndexOf("/")+1);
//					strIconName = strIconName.substring(0, strIconName.lastIndexOf(".")) + ".png";
//					strIconFile = XConstant.RES_ICON_FILE_PATH + strIconName;
//					File iconFile = new File(strIconFile);
//					if (!iconFile.exists())
//					{
//						strIconFile = XConstant.RES_ICON_FILE_PATH + "default.png";
//					}
//				}
//				bmp = Util.loadImageFromSD(strIconFile);
//				viewHolder.btnIcon.setImageBitmap(bmp);
//			}
//		}
		viewHolder.btnText.setText(btnInfo.getText());
		
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
		int resType = iconBtnList.get(index).getResType();
		if (resType == XConstant.CLASS_ID_INCMUSIC)
		{
			if (index == 0)
			{
				// 配乐第一项为[关闭原音],需单独控制其选中状态
				isOriginalVoiceOff = !isOriginalVoiceOff;
				iconBtnList.get(0).setSelected(isOriginalVoiceOff);
			}
			else
			{
				// 更新选中状态
				for (int i = 1; i < iconBtnList.size(); i++)
		        {  
		        	iconBtnList.get(i).setSelected(false);
		        }
		        
		        iconBtnList.get(index).setSelected(true);
			}
		}
		else
		{
			// 更新选中状态
			for (IconButtonInfo iconBtn : iconBtnList)
			{
				iconBtn.setSelected(false);
			}
	        iconBtnList.get(index).setSelected(true);
		}
    }
}
