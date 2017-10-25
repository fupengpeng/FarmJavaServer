package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;


/**
 * 
 * @Title: PlantTimeResult
 * @Description: 获取种子种植时间返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月25日 下午4:35:23
 *
 */
public class PlantTimeResult extends RequestResponseBase {
	private Long plantTime;

	public Long getPlantTime() {
		return plantTime;
	}

	public void setPlantTime(Long plantTime) {
		this.plantTime = plantTime;
	}
	
}
