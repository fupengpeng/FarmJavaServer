package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @Title: User
 * @Description: 用户实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017年9月26日 上午10:10:51
 *
 */
public class User {
	//用户id，主键
	private Long userId;
	//用户名
	private String userNickName;
	//用户头像
	private String userImage;
	//用户金币
	private Long userGold;
	//用户经验
	private Long userExperience;
	//用户种子集合
	private Set<Seed> userSeeds = new HashSet<Seed>();
	//用户土地集合
	private Set<Land> userLands = new HashSet<Land>();
	//用户果实集合
	private Set<Fruit> userFruits = new HashSet<Fruit>();
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserNickName() {
		return userNickName;
	}
	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	public String getUserImage() {
		return userImage;
	}
	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
	public Long getUserGold() {
		return userGold;
	}
	public void setUserGold(Long userGold) {
		this.userGold = userGold;
	}
	public Long getUserExperience() {
		return userExperience;
	}
	public void setUserExperience(Long userExperience) {
		this.userExperience = userExperience;
	}
	public Set<Seed> getUserSeeds() {
		return userSeeds;
	}
	public void setUserSeeds(Set<Seed> userSeeds) {
		this.userSeeds = userSeeds;
	}
	public Set<Land> getUserLands() {
		return userLands;
	}
	public void setUserLands(Set<Land> userLands) {
		this.userLands = userLands;
	}
	public Set<Fruit> getUserFruits() {
		return userFruits;
	}
	public void setUserFruits(Set<Fruit> userFruits) {
		this.userFruits = userFruits;
	}
	
	
	
	

}
