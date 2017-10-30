package com.jiudianlianxian.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

public class HttpUtil {
	
	 public static void requestData(final String urlStr,final HttpCallBackListener listener) {
		
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
			

	    }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 public static void resquestData(final String url , final HttpCallBackListener listener){
		 


			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(3000).setConnectTimeout(3000).build();
			CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
					.setDefaultRequestConfig(requestConfig).build();

			try {
				httpclient.start();
				final HttpGet request = new HttpGet(url);
				final CountDownLatch latch = new CountDownLatch(1);
				httpclient.execute(request, new FutureCallback<HttpResponse>() {

					@Override
					public void failed(Exception e) {
						latch.countDown();
						System.out.println(request.getRequestLine() + "->" + e);
						System.out.println("请求失败");
						listener.onError(e);

					}

					@Override
					public void completed(HttpResponse response) {
						latch.countDown();
						System.out.println(request.getRequestLine() + "->"
								+ response.getStatusLine());
						listener.onFinish(response.toString());
					
					}

					@Override
					public void cancelled() {
						latch.countDown();
						System.out.println(request.getRequestLine() + " cancelled");
						System.out.println("取消请求");

					}
				});

				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		 
		 
	 }
}
