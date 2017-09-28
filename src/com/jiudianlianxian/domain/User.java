package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;

public class User {
	
	private Long userId;
	
	private String userNickName;
	private String userImage;
	private Long userGold;
	private Long userExperience;
	private Long userGrade;
	
	private Set<Land> userLands = new HashSet<Land>();
	private Set<Seed> userSeeds = new HashSet<Seed>();
	
	
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
	public Long getUserGrade() {
		return userGrade;
	}
	public void setUserGrade(Long userGrade) {
		this.userGrade = userGrade;
	}
	public Set<Land> getUserLands() {
		return userLands;
	}
	public void setUserLands(Set<Land> userLands) {
		this.userLands = userLands;
	}
	public Set<Seed> getUserSeeds() {
		return userSeeds;
	}
	public void setUserSeeds(Set<Seed> userSeeds) {
		this.userSeeds = userSeeds;
	}
	
	
	

}
