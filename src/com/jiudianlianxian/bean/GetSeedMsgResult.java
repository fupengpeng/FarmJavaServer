package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;


/**
 * 
 * @Title: GetSeedMsgResult
 * @Description:  ��ȡ�û�������Ϣ����ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��28�� ����5:26:06
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
