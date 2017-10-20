package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;


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
public class Land {
	//����id������
	private Long landId;
	//��������
	private Long landName; 
	/**
	 * ����״̬
	 *     1��δ����
	 *     2��δ��ֲ���ѿ���
	 *     3��������ֲ��landSeed��ֵ��Ϊ��ֲ�����Ӷ���
	 *     4���������ӳ���
	 */
	private Long landState;
	
	private User landUser ;
	//������ֲʱ��������ֲ������,��set���ϣ�ʵ��ֻ��һ��
//	private Seed landSeed ;
	private Set<Seed> landSeeds = new HashSet<Seed>();
	
	
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
	public User getLandUser() {
		return landUser;
	}
	public void setLandUser(User landUser) {
		this.landUser = landUser;
	}
	public Set<Seed> getLandSeeds() {
		return landSeeds;
	}
	public void setLandSeeds(Set<Seed> landSeeds) {
		this.landSeeds = landSeeds;
	}
	
	
	
	

	
	

}
