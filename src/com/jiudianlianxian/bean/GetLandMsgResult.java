package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.GetLandMsgResultData;


/**
 * 
 * @Title: GetLandMsgResult
 * @Description: ��ȡ������Ϣ����ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��28�� ����5:03:51
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
