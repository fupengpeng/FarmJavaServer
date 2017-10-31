package com.jiudianlianxian.data;

/**
 * 
 * @Title: HarvestResultData
 * @Description: �ջ񷵻�ʵ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����10:46:07
 *
 */
public class HarvestResultData {

	private Long landState;
	private Long userId;
	private Long userExperience;
	private Long seedExperience;
	private Long landId;
	private Long fruitNumber;

	public Long getLandState() {
		return landState;
	}

	public void setLandState(Long landState) {
		this.landState = landState;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserExperience() {
		return userExperience;
	}

	public void setUserExperience(Long userExperience) {
		this.userExperience = userExperience;
	}

	public Long getSeedExperience() {
		return seedExperience;
	}

	public void setSeedExperience(Long seedExperience) {
		this.seedExperience = seedExperience;
	}

	public Long getLandId() {
		return landId;
	}

	public void setLandId(Long landId) {
		this.landId = landId;
	}

	public Long getFruitNumber() {
		return fruitNumber;
	}

	public void setFruitNumber(Long fruitNumber) {
		this.fruitNumber = fruitNumber;
	}

}
