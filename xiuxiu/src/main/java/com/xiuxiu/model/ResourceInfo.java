package com.xiuxiu.model;


// 资源信息
public abstract class ResourceInfo
{
	private int resType;//资源类型
	private int resLoadStatus;//下载状态
	
	public ResourceInfo(int resType)
	{
		this.resType = resType;
	}
	
	public int getResType()
	{
		return resType;
	}
	
	public void setResType(int resType)
	{
		this.resType = resType;
	}
	
	public int getResLoadStatus()
	{
		return resLoadStatus;
	}
	
	public void setResLoadStatus(int resLoadStatus)
	{
		this.resLoadStatus = resLoadStatus;
	}
}
