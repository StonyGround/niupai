package com.xiuxiu.phttprequest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xiuxiu.phttprequest.PMultiPartEntity.PostProgressListener;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicHeader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PHttpRequest extends PHttpUrlUtil 
{

	public enum RequestMethodType 
	{
		RequestMethodTypeGet, 
		RequestMethodTypeDownload, 
		RequestMethodTypePost, 
		RequestMethodTypeMultipart
	}

	public static final int BUFFER_SIZE = 8 * 1024;
	// 使用线程池，来重复利用线程，优化内存
	private static final int DEFAULT_THREAD_POOL_SIZE = 10;
	//普通请求小数据
	private static ThreadPoolExecutor normalExecutor = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
	//图片等大数据
	private static ThreadPoolExecutor specialExecutor = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

	// 请求类型get，post
	private RequestMethodType requestMethodType = RequestMethodType.RequestMethodTypeGet;
	// url
	private String mUrl;
	// url参数
	private TreeMap<String, String> mParams;

	/**
	 * 断点下载
	 */
	// header
	private Header mHeader;
	// 下载文件存放路径
	private String mDownloadFilePath = null;
	//
	private String id = null;
	private long localSize = 0;
	private long fileSize = 0;
	private long contentSize = 0;
	
	/**
	 * 上传文件
	 */
	// 上传文件
	private PMultiPartEntity mMultipartContent = null;
	// 文件大小
	private long mTotalSize = 0;

	// 请求是否被取消
	public boolean isCancel = false;

	/**
	 * 返回结果
	 */
	public String errorString = null;
	public String responseString = null;

	/**
	 * 接口
	 */
	private PHttpBaseCallback baseCallback = null;
	private PHttpStringCallback stringCallback = null;
	private PHttpBitmapCallback bitmapCallback = null;
	private PHttpUploadFileCallback uploadFileCallback = null;
	private PHttpDownloadFileCallback downloadFileCallback = null;
	
	public static PHttpRequest requestWithURL(String url, TreeMap<String, String> params)
	{
		PHttpRequest request = new PHttpRequest(url, params);
		return request;
	}

	public static PHttpRequest requestWithURL(String url)
	{
		PHttpRequest request = new PHttpRequest(url);
		return request;
	}

	public PHttpRequest(String url)
	{
		this.mUrl = url;
	}

	public PHttpRequest(String url, TreeMap<String, String> params)
	{
		this.mUrl = url;
		this.mParams = params;
	}

	protected HttpResponse requestHttp(RequestMethodType requestMethodType)
	{

		HttpResponse httpResponse = null;

		try {
			if (RequestMethodType.RequestMethodTypeGet == requestMethodType) {
				// get
				httpResponse = super.executeGet(mUrl, mParams);
			} else if (RequestMethodType.RequestMethodTypeDownload == requestMethodType) {
				// download			
				if(null == mDownloadFilePath){
					//MyLog.e("mDownloadFilePath is null...please check");
					return null;
				}
				
				mHeader = new BasicHeader("Range", "bytes=" + localSize + "-" + fileSize);
				httpResponse = super.executeGet(mUrl, mParams, mHeader);
			} else if (RequestMethodType.RequestMethodTypePost == requestMethodType) {
				// post
				httpResponse = super.executePost(mUrl, mParams);
			} else if (RequestMethodType.RequestMethodTypeMultipart == requestMethodType) {
				// multipart
				httpResponse = super.executeMultipartPost(mUrl, mParams, mMultipartContent);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return httpResponse;
	}
	
	/**
	 * 返回请求的长度
	 * @param requestMethodType
	 * @return
	 */
	public long requestHttpResponseContentLength(RequestMethodType requestMethodType){
		
		long responseContentLen = 0;
		
		HttpResponse httpResponse = requestHttp(requestMethodType);
		if(null == httpResponse){
			return responseContentLen;
		}
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		switch (statusCode) {
		case HttpStatus.SC_OK: {
			// 200
			responseContentLen = httpResponse.getEntity().getContentLength();
		}
		case HttpStatus.SC_PARTIAL_CONTENT:{
			//206
			responseContentLen = httpResponse.getEntity().getContentLength();
		}
		}
		
		return responseContentLen;
	}
	
	protected InputStream requestHttpResponse(RequestMethodType requestMethodType){
		
		//check network
		HttpResponse httpResponse = requestHttp(requestMethodType);
		if(null == httpResponse){
			return null;
		}
		
		try {
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			//MyLog.d("statusCode: " + statusCode);
			switch (statusCode) 
			{
			case HttpStatus.SC_OK:
			{
				// 200
				contentSize = httpResponse.getEntity().getContentLength();
				//MyLog.d("contentSize=" + contentSize);
				InputStream inputStream = httpResponse.getEntity().getContent();
				if(null != baseCallback){
					baseCallback.requestReceiveBytes(inputStream);
				}
				
				return new BufferedInputStream(inputStream);
			}
			case HttpStatus.SC_PARTIAL_CONTENT:{
				//206
				contentSize = httpResponse.getEntity().getContentLength();
				InputStream inputStream = httpResponse.getEntity().getContent();
				if(null != baseCallback){
					baseCallback.requestReceiveBytes(inputStream);
				}
				
				return new BufferedInputStream(inputStream);
			}
			case HttpStatus.SC_NOT_MODIFIED: {
				// 304
				break;
			}

			default:
				if(null != baseCallback){
					baseCallback.requestFialed(httpResponse, statusCode);
				}
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(null != baseCallback){
				baseCallback.requestFialed(httpResponse, -999);
			}
		}
		
		return null;
	}
	
	public void startSynchronous() 
	{
		InputStream is = requestHttpResponse( requestMethodType );
		if (null != is) 
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			try 
			{
				while ((len = is.read(buffer)) != -1) 
				{
					baos.write(buffer, 0, len);
				}
				is.close();
			} catch (Exception e) {
				errorString = "read InputStream error...";
				if(null != stringCallback){
					stringCallback.requestFialed(errorString);
				}
			}
			responseString = baos.toString();
			if(null != stringCallback)
			{
				stringCallback.requestFinished(responseString);
			}
		} else {
			errorString = "response InputStream error...";
			if(null != stringCallback){
				stringCallback.requestFialed(errorString);
			}
		}
	}

	public String startSyncRequestString()
	{
		startSynchronous();
		return responseString;
	}

	public void startSyncRequestDownload() {
		//start
		if(null != downloadFileCallback){
			downloadFileCallback.requestStart(id, fileSize, mDownloadFilePath);
		}
		
		InputStream is = requestHttpResponse(requestMethodType);
		if (null != is) {
			
			byte[] buffer = new byte[BUFFER_SIZE];
			long downloadfilesize = 0;
			int len = 0;
			try {
				//local file
				File tempFile = new File(mDownloadFilePath);
				if(!tempFile.exists()){
					tempFile.createNewFile();
				}
				RandomAccessFile fos = new RandomAccessFile(tempFile, "rw");
				fos.seek(localSize);
				
				while (!isCancel && ((len = is.read(buffer)) != -1)) {
					fos.write(buffer, 0, len);
					downloadfilesize += len;
					
					if(null != downloadFileCallback){
						downloadFileCallback.requestReceiveBytes(this, downloadfilesize);
						downloadFileCallback.reqeustProgress(fileSize, (downloadfilesize + localSize)/(float)fileSize);
					}else{
						//System.out.println("contentSize: " + contentSize + ", downloadfilesize: " + downloadfilesize + ", localSize: " + localSize + ", fileSize: " + fileSize);
					}
					Thread.sleep(100);
				}
				fos.close();
				is.close();
				
				//
				if(null != downloadFileCallback){
					downloadFileCallback.requestReadInputStreamFinish(isCancel, mDownloadFilePath);
				}
				
			} catch (Exception e) {
				if(null != downloadFileCallback){
					downloadFileCallback.requestReadInputStreamFailed("IOReadError", mDownloadFilePath);
				}
			}
		} else {
			if(null != downloadFileCallback){
				downloadFileCallback.requestReadInputStreamFailed("IOError", mDownloadFilePath);
			}
		}
	}

	public Bitmap startSyncRequestBitmap() {
		
		Bitmap bitmap = null;
		
		// Bitmap cache = getBitmapFromCache();
		// if (cache!=null) {
		// return cache;
		// }
		InputStream is = requestHttpResponse(requestMethodType);
		if (null != is) {
			bitmap = BitmapFactory.decodeStream(is);
			if(null != bitmapCallback){
				bitmapCallback.requestBitmap(bitmap);
			}
			return bitmap;
		}

		return bitmap;
	}

	public void startAsynRequestString() {
		normalExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startSynchronous();// 异步处理

			}
		});
	}
	
	public void startAsynRequestBitmap(){
		specialExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startSyncRequestBitmap();
			}
		});
	}
	
	public void startAsynRequestDownload(){
		
		specialExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startSyncRequestDownload();
			}
		});
		
		/*
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startSyncRequestDownload();
			}
			
		}.start();
		*/
		
	}
	
	public void startAsynRequestUpload(){
		specialExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startSyncRequestString();
			}
		});
	}
	
	/**
	 * 请求方式
	 * @return
	 */
	public RequestMethodType getRequestMethodType() {
		return requestMethodType;
	}

	public void setRequestMethodType(RequestMethodType requestMethodType) {
		this.requestMethodType = requestMethodType;
	}

	/**
	 * 上传文件
	 * @param file
	 * @param key
	 */
	public void setUploadFile(File file, String key) {
		// multipart
		mMultipartContent = new PMultiPartEntity(new PostProgressListener() {

			@Override
			public void transferred(long num) {
				if(null != uploadFileCallback){
					uploadFileCallback.requestSendBytes(mTotalSize, num);
					uploadFileCallback.reqeustProgress(num / (float)mTotalSize);
				}else{
					//System.out.println("totalSize: " + mTotalSize + ", transferred: " + num);
					
				}
				
			}

		});
		
		mMultipartContent.addPart(key, new FileBody(file));
		mTotalSize = mMultipartContent.getContentLength();

	}

	/**
	 * 设置上传文件的路径和http key值
	 * 
	 * @param filepath
	 * @param key
	 */
	public void setUploadFilePath(String filepath, String key) {
		if (null != filepath) {
			setUploadFile(new File(filepath), key);
		}
	}

	/**
	 * 下载文件路径
	 * @return
	 */
	public String getmDownloadFilePath() {
		return mDownloadFilePath;
	}

	public void setmDownloadFilePath(String mDownloadFilePath) {
		this.mDownloadFilePath = mDownloadFilePath;
	}

	public PHttpStringCallback getStringCallback() {
		return stringCallback;
	}

	public void setStringCallback(PHttpStringCallback stringCallback) {
		this.stringCallback = stringCallback;
	}

	public PHttpUploadFileCallback getUploadFileCallback() {
		return uploadFileCallback;
	}

	public void setUploadFileCallback(PHttpUploadFileCallback uploadFileCallback) {
		this.uploadFileCallback = uploadFileCallback;
	}

	public void setBaseCallback(PHttpBaseCallback baseCallback) {
		this.baseCallback = baseCallback;
	}

	public void setBitmapCallback(PHttpBitmapCallback bitmapCallback) {
		this.bitmapCallback = bitmapCallback;
	}

	public void setDownloadFileCallback(
			PHttpDownloadFileCallback downloadFileCallback) {
		this.downloadFileCallback = downloadFileCallback;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public long getLocalSize() {
		return localSize;
	}

	public void setLocalSize(long localSize) {
		this.localSize = localSize;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
