package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.OpenWarehouseResultData;


/**
 * 
 * @Title: OpenWarehouseResult
 * @Description: �򿪲ֿⷵ��ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����4:36:39
 *
 */
public class OpenWarehouseResult extends RequestResponseBase{
	
	OpenWarehouseResultData openWarehouseResultData = new OpenWarehouseResultData();

	public OpenWarehouseResultData getOpenWarehouseResultData() {
		return openWarehouseResultData;
	}

	public void setOpenWarehouseResultData(
			OpenWarehouseResultData openWarehouseResultData) {
		this.openWarehouseResultData = openWarehouseResultData;
	}
	
	
}
