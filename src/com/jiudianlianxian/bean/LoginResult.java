package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.LoginResultData;


/**
 * 
 * @Title: LoginResponse
 * @Description: ������һ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��27�� ����2:34:07
 *
 */
public class LoginResult extends RequestResponseBase {
	
	private LoginResultData loginResponseData = new LoginResultData();

	public LoginResultData getLoginResponseData() {
		return loginResponseData;
	}

	public void setLoginResponseData(LoginResultData loginResponseData) {
		this.loginResponseData = loginResponseData;
	}
	

}
