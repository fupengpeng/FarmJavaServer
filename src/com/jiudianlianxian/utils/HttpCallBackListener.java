package com.jiudianlianxian.utils;


public interface HttpCallBackListener {

	void onFinish(String respose);

	void onError(Exception e);

}
