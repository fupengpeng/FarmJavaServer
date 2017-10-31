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
	// 用户id，主键
	private Long userId;
	// 用户名
	private String userNickName;
	// 用户头像
	private String userImage;
	// 用户金币
	private Long userGold;
	// 用户经验
	private Long userExperience;
	// openid 微信用户唯一标识
	private String openid;
	// 用户性别
	private int sex;
	// 用户省份
	private String province;
	// 用户城市
	private String city;
	// 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
	private String unionid;
	// 用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
	private String[] privilege;
	// 国家，如中国为CN
	private String country;
	// 用户种子集合
	private Set<Seed> userSeeds = new HashSet<Seed>();
	// 用户土地集合
	private Set<Land> userLands = new HashSet<Land>();
	// 用户果实集合
	private Set<Fruit> userFruits = new HashSet<Fruit>();

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String[] getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String[] privilege) {
		this.privilege = privilege;
	}

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

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userNickName=" + userNickName
				+ ", userImage=" + userImage + ", userGold=" + userGold
				+ ", userExperience=" + userExperience + ", openid=" + openid
				+ ", userSeeds=" + userSeeds + ", userLands=" + userLands
				+ ", userFruits=" + userFruits + "]";
	}

}
