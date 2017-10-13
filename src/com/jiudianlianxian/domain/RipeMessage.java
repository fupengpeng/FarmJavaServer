package com.jiudianlianxian.domain;


/**
 * 
 * @Title: RipeMessage
 * @Description: 种子成熟时所需信息
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 下午2:09:54
 *
 */
public class RipeMessage {
	
	private Long seedId;
	private Long userId;
	private Long seedGrowthTime;
	private Long seedPlantTime;
	public Long getSeedId() {
		return seedId;
	}
	public void setSeedId(Long seedId) {
		this.seedId = seedId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getSeedGrowthTime() {
		return seedGrowthTime;
	}
	public void setSeedGrowthTime(Long seedGrowthTime) {
		this.seedGrowthTime = seedGrowthTime;
	}
	public Long getSeedPlantTime() {
		return seedPlantTime;
	}
	public void setSeedPlantTime(Long seedPlantTime) {
		this.seedPlantTime = seedPlantTime;
	}
	

}
