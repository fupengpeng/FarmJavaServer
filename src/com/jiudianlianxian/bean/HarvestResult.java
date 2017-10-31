package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.HarvestResultData;

/**
 * 
 * @Title: HarvestResult
 * @Description: �ջ񷵻�ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����11:14:02
 *
 */
public class HarvestResult extends RequestResponseBase {

	private HarvestResultData harvestResultData = new HarvestResultData();

	public HarvestResultData getHarvestResultData() {
		return harvestResultData;
	}

	public void setHarvestResultData(HarvestResultData harvestResultData) {
		this.harvestResultData = harvestResultData;
	}

}
