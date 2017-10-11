package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @Title: Seed
 * @Description: ������Ʒ��ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017��9��26�� ����10:15:44
 *
 */
public class Seed {
	//����id�� ����
	private Long seedId;
	//��������
	private String seedName;
	/**
	 * ����״̬
	 *     1���̵�
	 *     2���û�����
	 *     3����ֲ������
	 */
	private String seedState;
	//��������ʱ��
	private Long seedGrowthTime;
	//���ӹ���۸�
	private Long seedBuyPrice;
	//���ӳ��ۼ۸�
	private Long seedSellingPrice;
	//���ӳ��ɻ�ȡ����
	private Long seedExperience;
	//���Ӳ���
	private Long seedYield;
	//���ӳ��ɹ�ʵ�ۼ�
	private Long seedFruitSellingPrice;
	//��������  ����������
	private Long seedType;
	//����ͼƬ
	private String seedImage;
	// ����������������̵꣬��Ϊ0
	private int seedNumber;
	
	private User seedUser ;
	
	private Set<Land> seedLands = new HashSet<Land>();

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

	public Long getSeedBuyPrice() {
		return seedBuyPrice;
	}

	public void setSeedBuyPrice(Long seedBuyPrice) {
		this.seedBuyPrice = seedBuyPrice;
	}

	public Long getSeedSellingPrice() {
		return seedSellingPrice;
	}

	public void setSeedSellingPrice(Long seedSellingPrice) {
		this.seedSellingPrice = seedSellingPrice;
	}

	public Long getSeedExperience() {
		return seedExperience;
	}

	public void setSeedExperience(Long seedExperience) {
		this.seedExperience = seedExperience;
	}

	public Long getSeedYield() {
		return seedYield;
	}

	public void setSeedYield(Long seedYield) {
		this.seedYield = seedYield;
	}

	public Long getSeedFruitSellingPrice() {
		return seedFruitSellingPrice;
	}

	public void setSeedFruitSellingPrice(Long seedFruitSellingPrice) {
		this.seedFruitSellingPrice = seedFruitSellingPrice;
	}

	public Long getSeedType() {
		return seedType;
	}

	public void setSeedType(Long seedType) {
		this.seedType = seedType;
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

	public User getSeedUser() {
		return seedUser;
	}

	public void setSeedUser(User seedUser) {
		this.seedUser = seedUser;
	}

	public Set<Land> getSeedLands() {
		return seedLands;
	}

	public void setSeedLands(Set<Land> seedLands) {
		this.seedLands = seedLands;
	}
	
	
	

	
	

}
