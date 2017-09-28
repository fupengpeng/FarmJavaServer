package com.jiudianlianxian.domain;


/**
 * 
 * @Title: Land
 * @Description: ������һ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmWebServer
 * @author fupengpeng
 * @date 2017��9��27�� ����2:59:58
 *
 */
public class Land {
	
	private Long landId;
	private String landName; 
	/**
	 * ����״̬
	 *     1��������ֲ��landSeed��ֵ��Ϊ��ֲ�����Ӷ���
	 *     2��δ��ֲ���ѿ���
	 *     3��δ����
	 */
	private String landState;
	
	/**
	 * ���Ӷ���
	 *     ������״̬Ϊ1ʱ���������Ӷ��󣬷���Ϊ��
	 */
	private Seed landSeed;
	private User landUser;
	
	
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
	public String getLandState() {
		return landState;
	}
	public void setLandState(String landState) {
		this.landState = landState;
	}
	public Seed getLandSeed() {
		return landSeed;
	}
	public void setLandSeed(Seed landSeed) {
		this.landSeed = landSeed;
	}
	public User getLandUser() {
		return landUser;
	}
	public void setLandUser(User landUser) {
		this.landUser = landUser;
	}

	

	
	

}
