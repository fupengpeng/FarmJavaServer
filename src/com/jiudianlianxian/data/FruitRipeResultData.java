package com.jiudianlianxian.data;

/**
 * 
 * @Title: FruitRipeData
 * @Description: ��ʵ���췵��ʵ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����1:42:48
 *
 */
public class FruitRipeResultData {

	private Long userId;
	private Long landId;
	private String landName;
	private Long landState;

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

	public Long getLandId() {
		return landId;
	}

	public void setLandId(Long landId) {
		this.landId = landId;
	}

	public String getLandName() {
		return landName;
	}

	public void setLandName(String landName) {
		this.landName = landName;
	}

}
