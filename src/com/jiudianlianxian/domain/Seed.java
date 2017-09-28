package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;



/**
 * 
 * @Title: Seed
 * @Description: ������һ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmWebServer
 * @author fupengpeng
 * @date 2017��9��27�� ����3:01:37
 *
 */
public class Seed {
	
	private Long seedId;
	
	private String seedName;
	/**
	 * ����״̬
	 *     1���̵�
	 *     2���û�����
	 *     3����ֲ������
	 */
	private String seedState;
	//��������ʱ������λ��
	private Long seedGrowthTime;
	//�����۳��۸񣬵�λ�����
	private int seedSellingPrice;
	private String seedImage;
	/**
	 * ��������
	 *     ���̵�չʾʱ��Ϊ0
	 *     ����������ֲʱ��Ϊ0
	 */
	private int seedNumber;
	
	//������ֲ���Ǹ������ϡ�������״̬Ϊ3ʱ������ֵ������Ϊ��
	private Land seedLand;
	private Set<User> seedUsers = new HashSet<User>();
	public Long getSeedId() {
		return seedId;
	}
	public void setSeedId(Long seedId) {
		this.seedId = seedId;
	}
	public String getSeedName() {
		return seedName;
	}
	public void setSeedName(String seedName) {
		this.seedName = seedName;
	}
	public String getSeedState() {
		return seedState;
	}
	public void setSeedState(String seedState) {
		this.seedState = seedState;
	}
	public Long getSeedGrowthTime() {
		return seedGrowthTime;
	}
	public void setSeedGrowthTime(Long seedGrowthTime) {
		this.seedGrowthTime = seedGrowthTime;
	}
	public int getSeedSellingPrice() {
		return seedSellingPrice;
	}
	public void setSeedSellingPrice(int seedSellingPrice) {
		this.seedSellingPrice = seedSellingPrice;
	}
	public String getSeedImage() {
		return seedImage;
	}
	public void setSeedImage(String seedImage) {
		this.seedImage = seedImage;
	}
	public int getSeedNumber() {
		return seedNumber;
	}
	public void setSeedNumber(int seedNumber) {
		this.seedNumber = seedNumber;
	}
	public Land getSeedLand() {
		return seedLand;
	}
	public void setSeedLand(Land seedLand) {
		this.seedLand = seedLand;
	}
	public Set<User> getSeedUsers() {
		return seedUsers;
	}
	public void setSeedUsers(Set<User> seedUsers) {
		this.seedUsers = seedUsers;
	}

	
	
	

}
