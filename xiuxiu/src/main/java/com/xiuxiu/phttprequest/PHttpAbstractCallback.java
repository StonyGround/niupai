package com.xiuxiu.phttprequest;

import org.apache.http.HttpResponse;

import java.io.InputStream;

public interface PHttpAbstractCallback {

	public void requestFialed(HttpResponse httpResponse, int statusCode);
	public void requestReceiveBytes(InputStream inputStream);
	
}
