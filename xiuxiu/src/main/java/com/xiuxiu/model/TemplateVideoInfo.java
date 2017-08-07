package com.xiuxiu.model;


import com.xiuxiu.util.XConstant;

// 模板
public class TemplateVideoInfo extends ResourceInfo
{
	// 歌词文件路径
	private String lyricsFilePath;
	// 视频文件路径
	private String videoFilePath;
	// 视频文件下载URL
	private String videoLoadUrl;
	// ICON文件路径
	private String iconFilePath;
	// ICON文件下载URL
	private String iconLoadUrl;
	// ICON文本
	private String iconText;
	// 视频文件名称
	private String videoName;
	//是否选中
	private boolean isSelected;
	// ICON ID
	private int iconId;//本地图标
	
	public TemplateVideoInfo()
	{
		super(XConstant.CLASS_ID_TEMPLATE);
	}

	public String getLyricsFilePath()
	{
		return lyricsFilePath;
	}

	public void setLyricsFilePath(String lyricsFilePath)
	{
		this.lyricsFilePath = lyricsFilePath;
	}

	public boolean isSelected()
	{
		return isSelected;
	}

	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	public String getVideoFilePath()
	{
		return videoFilePath;
	}

	public void setVideoFilePath(String videoFilePath)
	{
		this.videoFilePath = videoFilePath;
	}

	public String getVideoLoadUrl()
	{
		return videoLoadUrl;
	}

	public void setVideoLoadUrl(String videoLoadUrl)
	{
		this.videoLoadUrl = videoLoadUrl;
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

	public String getIconText()
	{
		return iconText;
	}

	public void setIconText(String iconText)
	{
		this.iconText = iconText;
	}

	public String getVideoName()
	{
		return videoName;
	}

	public void setVideoName(String videoName)
	{
		this.videoName = videoName;
	}

	public int getIconId()
	{
		return iconId;
	}

	public void setIconId(int iconId)
	{
		this.iconId = iconId;
	}
}
