package com.xiuxiu.phttprequest;

public interface PHttpDownloadFileCallback {
	
	public void requestStart(String localkey, long filesize, String downloadFilePath);
	public void requestReceiveBytes(PHttpRequest request, long num);
	public void reqeustProgress(long filesize, float progress);
	public void requestReadInputStreamFailed(String errorCode, String downloadFilePath);
	public void requestReadInputStreamFinish(boolean isCancel, String downloadFilePath);
	
}
