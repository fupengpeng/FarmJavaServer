package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;



/**
 * 
 * @Title: Seed
 * @Description: 给此类一个描述
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmWebServer
 * @author fupengpeng
 * @date 2017年9月27日 下午3:01:37
 *
 */
public class Seed {
	
	private Long seedId;
	
	private String seedName;
	/**
	 * 种子状态
	 *     1：商店
	 *     2：用户背包
	 *     3：种植在土地
	 */
	private String seedState;
	//种子生长时长，单位秒
	private Long seedGrowthTime;
	//种子售出价格，单位：金币
	private int seedSellingPrice;
	private String seedImage;
	/**
	 * 种子数量
	 *     在商店展示时，为0
	 *     在土地中种植时，为0
	 */
	private int seedNumber;
	
	//种子种植在那个土地上。当种子状态为3时，才有值，否则为空
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
