package com.xiuxiu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionManager
{
	private static Context context = null;
	
	public static void setContext(Context _context)
	{
		context = _context;
	}

	// 获取应用当前版本名
	public static String getAppVersionName()
	{
		try
		{
			PackageManager pm = context.getPackageManager();
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
	        return pi.versionName;
		} 
		catch (NameNotFoundException e)
		{
			return "";
		}
	}
	

}
