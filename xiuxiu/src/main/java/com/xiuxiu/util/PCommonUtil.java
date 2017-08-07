package com.xiuxiu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class PCommonUtil 
{
	public static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	public static String AES_Encode(String str, String key)
	{
		byte[] textBytes;
		try {
			textBytes = str.getBytes("UTF-8");
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		     SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		     Cipher cipher = null;
		     cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		     cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
		
		     //android base64 after 2.1
//		     return Base64.encodeToString(cipher.doFinal(textBytes), 0);
		     byte[] encodeBytes = cipher.doFinal(textBytes);
		     if(null != encodeBytes){
//				return Base64Encoder.encode(encodeBytes);
		    	 return AndroidBase64.encodeToString(encodeBytes, 0);
		     }
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("str: " + str);
	//		PDataCache.getInstance().setString("encodeerrorlog", str);
		}
		
		return null;
	}

	public static String AES_Decode(String str, String key)
	{
		try {
//			byte[] textBytes =Base64.decode(str,0);
			if(null == str){
				return null;
			}
			byte[] textBytes = AndroidBase64.decode(str,0);
//			byte[] textBytes =Base64Decoder.decodeToBytes(str);
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
			return new String(cipher.doFinal(textBytes), "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("str: " + str);
	//		PDataCache.getInstance().setString("decodeerrorlog", str);
		}
		
		return null;
	}
	
	
	/**
	 * 获取当前的网络信息
	 * @param context
	 * @return
	 */
	public static String getAPNType(Context context){
		String netType = "unknown";
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
		
		if(null == networkInfo){
			return netType;
		}
		int nType = networkInfo.getType();
		if(nType == ConnectivityManager.TYPE_MOBILE){
			netType = networkInfo.getExtraInfo();
		} else if(nType == ConnectivityManager.TYPE_WIFI){
			netType = "WiFi";
		}
		return netType;
	}
	
	/**
	 * 获取当前app的版本号
	 * @param context
	 * @param packageName app的包名
	 * @return
	 */
	public static String getAppVersionName(Context context, String packageName){
		String versionName = "";
		
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			versionName = (null != packageInfo) ? packageInfo.versionName : "";
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return versionName;
	}
	
	/**
	 * 对带有空格等特殊字符的url地址做编码
	 * 编码后http://ktv.9158.com/key=value中的部分字符同时被编码，
	 * 需要做替换处理，恢复正常状态.
	 * @param srcurl
	 * @return
	 */
	public static String formatUrlForDownload(String srcurl){
		String dsturl = "" + srcurl;
		if(null != srcurl){
			dsturl = URLEncoder.encode(srcurl)
					.replace("%3A", ":").replace("%2F", "/")
					.replace("%3D", "=").replace("+", "%20");
		}
		return dsturl;
	}
	
	public final static int IMAGE_SIZE_ORIGINAL = 3;
	public final static int IMAGE_SIZE_BIG = 2;
	public final static int IMAGE_SIZE_MIDDLE = 1;
	public final static int IMAGE_SIZE_SMALL = 0;
	
	public final static int START_COVER_ISO_2 = 2;
	public final static int START_COVER_ISO_1 = 1;
	public final static int START_COVER_ANDROID = 0;
	
	/**
	 * 转换图片地址，获取不同大小的图片
	 * 1.先将url地址做一次恢复处理，转成原图地址url
	 * 2.再将原图地址处理成指定的缩放图片地址url
	 * @param srcurl
	 * @param scaletype : IMAGE_SIZE_BIG, IMAGE_SIZE_MIDDLE, IMAGE_SIZE_SMALL
	 * @return
	 */
	public static String getUrlByScaleType(String srcurl, int scaletype){
		String dsturl = null;
		if(null == srcurl || srcurl.trim().length() <= 0){
			return dsturl;
		}
		
		String[] imageType = { "_100_100.jpg", "_200_200.jpg", "_320_320.jpg", "_640_640.jpg" };
		//先格式化
		for(int i=0; i<imageType.length; i++){
			srcurl = srcurl.replace(imageType[i], ".jpg");
		}
		dsturl = srcurl.replace(".jpg", imageType[scaletype]);
		//MyLog.d("dsturl: " + dsturl);
		
		return dsturl;
	}
	
	public static String getStartPageCoverUrlScaleType(String srcurl, int scaletype){
		String dsturl = null;
		if(TextUtils.isEmpty(srcurl)){
			return dsturl;
		}
		
		String[] imageType = { "_480_800.jpg", "_640_960.jpg", "_640_1136.jpg" };
		//先格式化
		for(int i=0; i<imageType.length; i++){
			srcurl = srcurl.replace(imageType[i], ".jpg");
		}
		dsturl = srcurl.replace(".jpg", imageType[scaletype]);
		//MyLog.d("dsturl: " + dsturl);
		
		return dsturl;
	}
	
	/**
	 * 错误码转换
	 * @param context
	 * @param nErrorCode
	 * @return
	 */
	public static String parserStringWithErrorCode(Context context, int nErrorCode){
		
		String errorMessage ="123";// context.getString(R.string.error_code_system_message1);
		switch(nErrorCode){
		case GlobalDef.ErrorCodeSystem:{
//			errorMessage = context.getString(R.string.error_code_system_message1);
			break;
		}
		case GlobalDef.ErrorCodeParamPostError:{
//			errorMessage = context.getString(R.string.error_code_param_post_error);
			break;
		}
		case GlobalDef.ErrorCodeSecretError:{
//			errorMessage = context.getString(R.string.error_code_secret_error);
			break;
		}
		case GlobalDef.ErrorCodeServerBusy:{
//			errorMessage = context.getString(R.string.error_code_server_busy);
			break;
		}
		case GlobalDef.ErrorCodeTimeOut:{
//			errorMessage = context.getString(R.string.error_code_server_busy);
			break;
		}
		case GlobalDef.ErrorCodeInvalidUser:{
//			errorMessage = context.getString(R.string.error_code_invalid_user);
			break;
		}
		case GlobalDef.ErrorCodeDataBaseError:{
//			errorMessage = context.getString(R.string.error_code_database_error);
			break;
		}
		case GlobalDef.ErrorCodeDataNotFound:{
//			errorMessage = context.getString(R.string.error_code_data_not_found);
			break;
		}
		case GlobalDef.ErrorCodeNoPermission:{
//			errorMessage = context.getString(R.string.error_code_no_permission);
			break;
		}
		case GlobalDef.ErrorCodeFrequentComment:{
//			errorMessage = context.getString(R.string.error_code_frequent_comment);
			break;
		}
		case GlobalDef.ErrorCodeDuplicateComment:{
//			errorMessage = context.getString(R.string.error_code_duplicate_comment);
			break;
		}
		case GlobalDef.ErrorCodeSendFlowerUpLimit:{
//			errorMessage = context.getString(R.string.error_code_send_flower_up_limit);
			break;
		}
		case GlobalDef.ErrorCodeNickNameRepeat:{
//			errorMessage = context.getString(R.string.error_code_nickName_repeat);
			break;
		}
			
		}
		
		return errorMessage;
	}
	
	/**
	 * 做解码，可选是否做aes解码
	 * @param encodeStr
	 * @return
	 */
	public static JSONObject parserString2JsonObject(String encodeStr)
	{	
		if(null == encodeStr)
		{
			return null;
		}
		String key = "192c96beaec59d367329c70016e7a50f";//common.GetSecret();
		String resultData = AES_Decode(encodeStr, key);
		JSONObject jsonObject = null;
		try 
		{
			jsonObject = (null == resultData) ? null : new JSONObject(resultData);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	/**
	 * 根据固定url和参数，生成api全地址
	 * @param baseUrl
	 * @param param
	 * @return
	 */
	public static String generateAPIStringWithSecret(Context context,String baseUrl, String param)
	{
		try 
		{
			String srcUrl = baseUrl + param;
			//链接地址md5值加密
			String secret = generateSecretString( srcUrl );
			//获取手机品牌并通过URL加密
			String strPhoneBrand =  Util.getPhoneBrand();
			strPhoneBrand = URLEncoder.encode(strPhoneBrand,"UTF-8");
			//获取手机型号并通过URL加密
			String strPhoneModel =  Util.getPhoneModel();
			strPhoneModel = URLEncoder.encode(strPhoneModel,"UTF-8");
			//获取手机唯一标识码imei并通过URL加密
//			String strImei =  Util.getImei();
//			strImei = encodeStringOfUrlEncodeAndBase64(strImei);
			// 获取当前版本名称
			String versionName = Util.getAppVersionName(context);
			//拼接地址
			String encodeUrl = srcUrl + "&secret=" + secret +"&versioncode="+ versionName +"&client="+strPhoneModel+"&unique=";
			// MyLog.v("encode url: " + encodeUrl );
			return encodeUrl;
		} 
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 结合密钥，将指定的字符串做md5编码，生成服务端校验密钥
	 * @param str
	 * @return
	 */
	public static String generateSecretString(String str)
	{
//		CommonDef common = new CommonDef();
//		String key = common.GetSecretHead();
		String tempStr = "(%!*" + str + "*!%)";
//		String tempStr = key + str + key;
		return md5Encode(tempStr);
	}
	
	/**
	 * 先对字符做url编码，然后再做base64编码
	 * @param str
	 * @return
	 */
	public static String encodeStringOfUrlEncodeAndBase64(String str){
		if(null != str){
			return encodeBase64(URLEncoder.encode(str));
		}
		return null;
	}
	
	/**
	 * 先对字符做base64解码，然后再做url解码
	 * @param str
	 * @return
	 */
	public static String decodeStringOfBase64AndUrlEncode(String str){
		String tempStr = decodeBase64(str);
		if(null != tempStr){
			return URLDecoder.decode(tempStr);
		}
		return null;
	}
	
	/**
	 * 以utf8的形式，对字符串做base64编码
	 * @param str
	 * @return
	 */
	public static String encodeBase64(String str)
	{
		if(null != str)
		{
			try 
			{
				byte[] textBytes = str.getBytes("UTF-8");
				return AndroidBase64.encodeToString(textBytes, AndroidBase64.NO_WRAP);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		return "";
	}
	
	/**
	 * 做字符做base64解码，并转成utf8格式的字符串
	 * @param str
	 * @return
	 */
	public static String decodeBase64(String str)
	{
		if(null != str)
		{
			try 
			{
				byte[] textBytes = AndroidBase64.decode(str, AndroidBase64.NO_WRAP);
				return new String(textBytes, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 对字符做md5编码
	 * @param str
	 * @return
	 */
	public static String md5Encode(String str)
	{
		// 返回字符串
		String md5Str = null;
		if(null == str)
		{
			return md5Str;
		}
		try 
		{
			// 操作字符串
			StringBuffer buf = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			// 添加要进行计算摘要的信息,使用 plainText 的 byte 数组更新摘要。
			md.update(str.getBytes());
			// 计算出摘要,完成哈希计算。
			byte b[] = md.digest();
			int i;

			for (int offset = 0; offset < b.length; offset++) 
			{
				i = b[offset];
				if (i < 0) 
				{
					i += 256;
				}
				if (i < 16) 
				{
					buf.append("0");
				}
				// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
				buf.append(Integer.toHexString(i));
			}
			// 32位的加密
			md5Str = buf.toString();
			// 16位的加密
			// md5Str = buf.toString().md5Strstring(8,24);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return md5Str;
	}

}
