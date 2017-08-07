package com.xiuxiu.model;

import com.xiuxiu.model.ResourceInfo;

// 封装icon按钮信息
public class IconButtonInfo extends ResourceInfo
{
	private int resId;  // 资源id
	private String text;// 按钮文字
	private boolean isSelected;// 是否被选中
	// ICON文件路径
	private String iconFilePath;
	// ICON文件下载URL
	private String iconLoadUrl;
	// 歌曲文件路径
	private String songFilePath;
	// 歌曲文件下载URL
	private String songLoadUrl;
	
	public IconButtonInfo(int resType)
	{
		super(resType);
	}
	
	public int getResId()
	{
		return resId;
	}
	
	public void setResId(int resId)
	{
		this.resId = resId;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	public String getIconFilePath()
	{
		return iconFilePath;
	}

	public void setIconFilePath(String iconFilePath)
	{
		this.iconFilePath = iconFilePath;
	}

	public String getIconLoadUrl()
	{
		return iconLoadUrl;
	}

	public void setIconLoadUrl(String iconLoadUrl)
	{
		this.iconLoadUrl = iconLoadUrl;
	}

	public String getSongFilePath()
	{
		return songFilePath;
	}

	public void setSongFilePath(String songFilePath)
	{
		this.songFilePath = songFilePath;
	}

	public String getSongLoadUrl()
	{
		return songLoadUrl;
	}

	public void setSongLoadUrl(String songLoadUrl)
	{
		this.songLoadUrl = songLoadUrl;
	}
}
