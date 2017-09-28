package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;


/**
 * 
 * @Title: GetSeedMsgResult
 * @Description:  获取用户种子信息返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月28日 下午5:26:06
 *
 */
public class GetSeedMsgResult extends RequestResponseBase{
	
	private GetSeedMsgResultData getSeedMsgResultData = new GetSeedMsgResultData();

	public GetSeedMsgResultData getGetSeedMsgResultData() {
		return getSeedMsgResultData;
	}

	public void setGetSeedMsgResultData(GetSeedMsgResultData getSeedMsgResultData) {
		this.getSeedMsgResultData = getSeedMsgResultData;
	}
	 

	
	
	
	
}
