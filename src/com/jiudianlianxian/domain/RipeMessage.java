package com.jiudianlianxian.domain;


/**
 * 
 * @Title: RipeMessage
 * @Description: ���ӳ���ʱ������Ϣ
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����2:09:54
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
