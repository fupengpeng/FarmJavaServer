package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.GetLandMsgResultData;


/**
 * 
 * @Title: GetLandMsgResult
 * @Description: 获取土地信息返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月28日 下午5:03:51
 *
 */
public class GetLandMsgResult extends RequestResponseBase{
	
	private GetLandMsgResultData getLandMsgResultData = new GetLandMsgResultData();

	public GetLandMsgResultData getGetLandMsgResultData() {
		return getLandMsgResultData;
	}

	public void setGetLandMsgResultData(GetLandMsgResultData getLandMsgResultData) {
		this.getLandMsgResultData = getLandMsgResultData;
	}

	
	
	
	
}
