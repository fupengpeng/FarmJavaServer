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
	private String landName; 
	/**
	 * ����״̬
	 *     1��������ֲ��landSeed��ֵ��Ϊ��ֲ�����Ӷ���
	 *     2��δ��ֲ���ѿ���
	 *     3��δ����
	 */
	private String landState;
	
	private User landUser ;
	//������ֲʱ��������ֲ������,��set���ϣ�ʵ��ֻ��һ��
	private Set<Seed> landSeeds = new HashSet<Seed>() ;
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
