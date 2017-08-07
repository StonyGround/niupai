package com.xiuxiu.phttprequest;

public interface PHttpStringCallback {

	public void requestFialed(String errorCode);
	public void requestFinished(String result);

}
