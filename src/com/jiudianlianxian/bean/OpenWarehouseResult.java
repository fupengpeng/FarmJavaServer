package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.OpenWarehouseResultData;


/**
 * 
 * @Title: OpenWarehouseResult
 * @Description: 打开仓库返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 下午4:36:39
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
