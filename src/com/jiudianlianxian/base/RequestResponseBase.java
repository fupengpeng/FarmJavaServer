package com.jiudianlianxian.base;


/**
 * 
 * @Title: RequestResponseBase
 * @Description: 请求响应的实体基类
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月27日 下午2:31:09
 *
 */
public class RequestResponseBase {
	
	/**
	 * 请求响应信息头
	 */
	private String info;
	
	/**
	 * 请求是否成功
	 */
	private String code;
	
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	

}
