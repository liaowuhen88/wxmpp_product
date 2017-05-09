package com.baodanyun.websocket.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;


public class HttpRequestUtils {

	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	private static final String HTTPS = "https";
	
	public static HttpResponse httpPostWithJSON(String url, String json) throws Exception {
		return httpExecute(url, json);
	}
	
	private static boolean isHttpsRequest(String url){
		if(StringUtils.isBlank(url)){
			return false;
		}
		if(url.toLowerCase().startsWith(HTTPS)){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private static HttpResponse httpExecute(String url, String json)throws Exception {
		// 将JSON进行UTF-8编码,以便传输中文
		System.out.println("------------");
		// String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);
		DefaultHttpClient httpClient =null;
		if(isHttpsRequest(url)){
			httpClient=new SSLClient();
		}else{
			httpClient=new DefaultHttpClient();
		}
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
		StringEntity se = new StringEntity(json);
		se.setContentType(CONTENT_TYPE_TEXT_JSON);
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
		httpPost.setEntity(se);
		return httpClient.execute(httpPost);
	}

	public static String getResponseStr(HttpResponse response) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
}
