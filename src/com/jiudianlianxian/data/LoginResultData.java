package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.User;

public class LoginResultData {

	private User user = new User();
	private List<LandData> LandDatas = new ArrayList<LandData>();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<LandData> getLandDatas() {
		return LandDatas;
	}

	public void setLandDatas(List<LandData> landDatas) {
		LandDatas = landDatas;
	}
}
