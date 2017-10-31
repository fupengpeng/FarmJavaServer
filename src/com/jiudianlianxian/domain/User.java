package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @Title: User
 * @Description: �û�ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017��9��26�� ����10:10:51
 *
 */
public class User {
	// �û�id������
	private Long userId;
	// �û���
	private String userNickName;
	// �û�ͷ��
	private String userImage;
	// �û����
	private Long userGold;
	// �û�����
	private Long userExperience;
	// openid ΢���û�Ψһ��ʶ
	private String openid;
	// �û��Ա�
	private int sex;
	// �û�ʡ��
	private String province;
	// �û�����
	private String city;
	// �û�ͳһ��ʶ�����һ��΢�ſ���ƽ̨�ʺ��µ�Ӧ�ã�ͬһ�û���unionid��Ψһ�ġ�
	private String unionid;
	// �û���Ȩ��Ϣ��json���飬��΢���ֿ��û�Ϊ��chinaunicom��
	private String[] privilege;
	// ���ң����й�ΪCN
	private String country;
	// �û����Ӽ���
	private Set<Seed> userSeeds = new HashSet<Seed>();
	// �û����ؼ���
	private Set<Land> userLands = new HashSet<Land>();
	// �û���ʵ����
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
