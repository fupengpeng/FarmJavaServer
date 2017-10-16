package com.jiudianlianxian.utils;

import com.jiudianlianxian.domain.User;



public interface HttpCallBackListener {

	void onFinish(String response);

	void onError(Exception e);

}
