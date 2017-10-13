package com.jiudianlianxian.data;

import com.jiudianlianxian.domain.Land;


/**
 * 
 * @Title: PlantResultData
 * @Description: 种植返回实体数据
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 上午10:13:26
 *
 */
public class PlantResultData {
	
	private Land land;
	private int seedNumber;
	public Land getLand() {
		return land;
	}
	public void setLand(Land land) {
		this.land = land;
	}
	public int getSeedNumber() {
		return seedNumber;
	}
	public void setSeedNumber(int seedNumber) {
		this.seedNumber = seedNumber;
	}
	
	
	
}
