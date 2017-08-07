package com.xiuxiu.model;


import com.xiuxiu.util.XConstant;

// 伴奏歌曲信息
public class AccSongInfo extends ResourceInfo
{
	// 歌曲名
	private String songName;
	// 歌手名
	private String singerName;
	// 歌曲文件路径
	private String songFilePath;
	// 歌词文件路径
	private String lyricsFilePath;
	private boolean isSelected;//是否选中
	// 歌曲文件下载URL
	private String songLoadUrl;
	// 歌曲列表索引
	private int index = -1;
	
	public AccSongInfo()
	{
		super(XConstant.CLASS_ID_ACCSONG);
	}
	
	public String getSongName()
	{
		return songName;
	}
	
	public void setSongName(String songName)
	{
		this.songName = songName;
	}
	
	public String getSingerName()
	{
		return singerName;
	}
	
	public void setSingerName(String singerName)
	{
		this.singerName = singerName;
	}

	public String getSongFilePath()
	{
		return songFilePath;
	}

	public void setSongFilePath(String songFilePath)
	{
		this.songFilePath = songFilePath;
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

	public String getSongLoadUrl()
	{
		return songLoadUrl;
	}

	public void setSongLoadUrl(String songLoadUrl)
	{
		this.songLoadUrl = songLoadUrl;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
}
