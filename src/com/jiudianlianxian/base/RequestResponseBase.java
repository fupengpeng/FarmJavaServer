package com.jiudianlianxian.base;


/**
 * 
 * @Title: RequestResponseBase
 * @Description: ������Ӧ��ʵ�����
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��27�� ����2:31:09
 *
 */
public class RequestResponseBase {
	
	/**
	 * ������Ӧ��Ϣͷ
	 */
	private String info;
	
	/**
	 * �����Ƿ�ɹ�
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
