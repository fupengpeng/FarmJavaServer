package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.BuySeedResultData;


/**
 * 
 * @Title: BuySeedResult
 * @Description:  �������ӷ���ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��28�� ����4:02:21
 *
 */
public class BuySeedResult extends RequestResponseBase {
	
	private BuySeedResultData buySeedResultData = new BuySeedResultData();

	public BuySeedResultData getBuySeedResultData() {
		return buySeedResultData;
	}

	public void setBuySeedResultData(BuySeedResultData buySeedResultData) {
		this.buySeedResultData = buySeedResultData;
	}

	
	

}
