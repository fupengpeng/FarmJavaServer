package com.jiudianlianxian.data;



/**
 * 
 * @Title: Land
 * @Description: ������һ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017��9��26�� ����10:36:51
 *
 */
public class LandData {
	//����id������
	private Long landId;
	//��������
	private Long landName; 
	/**
	 * ����״̬
	 *     1��������ֲ��landSeed��ֵ��Ϊ��ֲ�����Ӷ���
	 *     2��δ��ֲ���ѿ���
	 *     3��δ����
	 */
	private Long landState;
	private String seedName;
	
	
	public Long getLandId() {
		return landId;
	}
	public void setLandId(Long landId) {
		this.landId = landId;
	}
	
	public Long getLandName() {
		return landName;
	}
	public void setLandName(Long landName) {
		this.landName = landName;
	}
	
	public Long getLandState() {
		return landState;
	}
	public void setLandState(Long landState) {
		this.landState = landState;
	}
	public String getSeedName() {
		return seedName;
	}
	public void setSeedName(String seedName) {
		this.seedName = seedName;
	}

	
	

}
