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
	private String landState;
	
	
	public String getLandState() {
		return landState;
	}
	public void setLandState(String landState) {
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
	

}
