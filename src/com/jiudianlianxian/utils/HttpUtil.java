package com.jiudianlianxian.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jiudianlianxian.domain.User;

public class HttpUtil {
	private static User user;
	
	 public static User requestData(final String urlStr, final HttpCallBackListener listener) {
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                HttpURLConnection connection = null;
	                try {
	                    URL url = new URL(urlStr);
	                    connection = (HttpURLConnection) url.openConnection();
	                    
	                    connection.setRequestMethod("GET");
	                    connection.setConnectTimeout(8000);
	                    connection.setReadTimeout(8000);
	                    connection.setDoInput(true);
	                    connection.setDoOutput(true);
	                    InputStream in = connection.getInputStream();
	                    BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
	                    StringBuilder sb = new StringBuilder();
	                    String line;
	                    while ((line = br.readLine()) != null) {
	                    	
	                    	System.out.println("line  = " + line  );
	                        sb.append(line);
	                    }
	                    
	                    if (listener != null) {
	                        //回调onFinish方法
	                        listener.onFinish(sb.toString());
	                    }
	                } catch (Exception e) {
	                    if (listener != null) {
	                        //回调onError方法
	                        listener.onError(e);
	                    }
	                } finally {
	                    if (connection != null) {
	                        connection.disconnect();
	                    }
	                }
	            }
	        }).start();
			return user;
	    }
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		HttpUtil.user = user;
	}
	 

	 

}
