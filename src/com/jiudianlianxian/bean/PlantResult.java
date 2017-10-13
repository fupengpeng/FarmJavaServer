package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.PlantResultData;


/**
 * 
 * @Title: PlantResult
 * @Description: 种植返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 上午10:12:19
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
