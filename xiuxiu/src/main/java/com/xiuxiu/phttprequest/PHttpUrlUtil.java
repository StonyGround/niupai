package com.xiuxiu.phttprequest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class PHttpUrlUtil 
{

	private String requestEncode = HTTP.UTF_8;
	private int requestSocketTimeOut = 6 * 1000;
	
	public static Cookie cookie;

	public void setRequestEncode(String requestEncode)
	{
		this.requestEncode = requestEncode;
	}

	public void setRequestSocketTimeOut(int requestSocketTimeOut) 
	{
		this.requestSocketTimeOut = requestSocketTimeOut;
	}

	public HttpResponse executeMultipartPost(String url, TreeMap<String, String> params, PMultiPartEntity multipartContent)
	{
		
		if(null == url)
		{
			return null;
		}
		
		HttpClient client = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, requestSocketTimeOut);

		HttpPost postMethod = new HttpPost(url);
		ArrayList<BasicNameValuePair> nameValuePairs = serizlizeNameValuePairs(params);

		try {

			//param
			if(nameValuePairs.size() > 0){
				postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, requestEncode));
			}
			//file
			if(null != multipartContent){
				postMethod.setEntity(multipartContent);
			}
			
			HttpResponse httpResponse = client.execute(postMethod, httpContext);
			return httpResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public HttpResponse executePost(String url, TreeMap<String, String> params)
	{

		if(null == url)
		{
			return null;
		}
		
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, requestSocketTimeOut);

		HttpPost postMethod = new HttpPost(url);
		ArrayList<BasicNameValuePair> nameValuePairs = serizlizeNameValuePairs(params);

		try {

			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, requestEncode));
			HttpResponse httpResponse = client.execute(postMethod);
			//After Login  
			List<Cookie> cookies = ((DefaultHttpClient)client).getCookieStore().getCookies();
			if(!cookies.isEmpty()) {
				for(int i=0; i<cookies.size(); i++) {
					cookie = cookies.get(i);
				}
			}
			return httpResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public HttpResponse executeGet(String url, TreeMap<String, String> params, Header header)
	{
		
		if(null == url)
		{
			return null;
		}
		
		String getUrl = serizlizeURL(url, params);
		
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, requestSocketTimeOut);

		HttpGet getMethod = new HttpGet(getUrl);
		
		try {
			
			if(null != header){
				getMethod.addHeader(header);
			}
			
			HttpResponse httpResponse = client.execute(getMethod);
			return httpResponse;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public HttpResponse executeGet(String url, TreeMap<String, String> params)
	{
		
		if(null == url)
		{
			return null;
		}
		
		String getUrl = serizlizeURL(url, params);
		
		DefaultHttpClient client = new DefaultHttpClient(new BasicHttpParams());
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, requestSocketTimeOut);
		
		HttpGet getMethod = new HttpGet(getUrl);
//		HttpGet getMethod = new HttpGet("www.baidu.com");
		
		try 
		{
			HttpResponse httpResponse = client.execute(getMethod);
			return httpResponse;

		}
		catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// post
	public static ArrayList<BasicNameValuePair> serizlizeNameValuePairs(
			TreeMap<String, String> map) {

		ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(
				2);

		if (null == map || map.size() < 1) {
			return nameValuePairs;
		}

		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String paramKey = it.next();
			String paramValue = map.get(paramKey);
			nameValuePairs.add(new BasicNameValuePair(paramKey, paramValue));
		}

		return nameValuePairs;
	}

	// get
	public static String serizlizeURL(String baseURL, TreeMap<String, String> params)
	{
		String postUrl = "";
		if (null == params || params.size() <= 0) 
		{
			return baseURL;
		}

		int index = baseURL.indexOf("?");
		if (index > 0) 
		{
			postUrl = baseURL + "&" + stringFromMap(params);
		}
		else 
		{
			postUrl = baseURL + "?" + stringFromMap(params);
		}
		return postUrl;
	}

	public static String stringFromMap(TreeMap<String, String> map) {

		if (null == map || map.size() < 1) {
			return "";
		}

		StringBuffer queryUrl = new StringBuffer();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String paramKey = it.next();
			String paramValue = map.get(paramKey);
			queryUrl.append(paramKey).append("=").append(paramValue)
					.append("&");
		}

		String finalUrl = "";
		if (queryUrl.length() > 0) {
			finalUrl = queryUrl.substring(0, queryUrl.length() - 1);
		}

		return finalUrl;
	}
	
}
