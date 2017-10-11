package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @Title: Seed
 * @Description: 种子商品的实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017年9月26日 上午10:15:44
 *
 */
public class Seed {
	//种子id， 主键
	private Long seedId;
	//种子名称
	private String seedName;
	/**
	 * 种子状态
	 *     1：商店
	 *     2：用户背包
	 *     3：种植在土地
	 */
	private String seedState;
	//种子生长时长
	private Long seedGrowthTime;
	//种子购买价格
	private Long seedBuyPrice;
	//种子出售价格
	private Long seedSellingPrice;
	//种子长成获取经验
	private Long seedExperience;
	//种子产量
	private Long seedYield;
	//种子长成果实售价
	private Long seedFruitSellingPrice;
	//种子类型  共生产几季
	private Long seedType;
	//种子图片
	private String seedImage;
	// 种子数量，如果在商店，则为0
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
