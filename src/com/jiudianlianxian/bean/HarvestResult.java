package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.HarvestResultData;

/**
 * 
 * @Title: HarvestResult
 * @Description: 收获返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 上午11:14:02
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
