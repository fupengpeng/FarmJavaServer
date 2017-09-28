package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.LoginResultData;


/**
 * 
 * @Title: LoginResponse
 * @Description: 给此类一个描述
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月27日 下午2:34:07
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
