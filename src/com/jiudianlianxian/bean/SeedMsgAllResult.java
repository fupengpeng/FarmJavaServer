package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.SeedMsgAllResultData;

/**
 * 
 * @Title: SeedMsgAllResult
 * @Description: ȫ�����ӷ���ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��27�� ����4:13:12
 *
 */
public class SeedMsgAllResult extends RequestResponseBase {

	private SeedMsgAllResultData seedMsgAllResultData = new SeedMsgAllResultData();

	public SeedMsgAllResultData getSeedMsgAllResultData() {
		return seedMsgAllResultData;
	}

	public void setSeedMsgAllResultData(
			SeedMsgAllResultData seedMsgAllResultData) {
		this.seedMsgAllResultData = seedMsgAllResultData;
	}

}
