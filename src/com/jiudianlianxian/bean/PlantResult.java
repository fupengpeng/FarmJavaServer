package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.PlantResultData;


/**
 * 
 * @Title: PlantResult
 * @Description: ��ֲ����ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����10:12:19
 *
 */
public class PlantResult extends RequestResponseBase {
	
	private PlantResultData plantResultData = new PlantResultData();

	public PlantResultData getPlantResultData() {
		return plantResultData;
	}

	public void setPlantResultData(PlantResultData plantResultData) {
		this.plantResultData = plantResultData;
	}
	

}
