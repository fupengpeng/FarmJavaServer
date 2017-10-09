package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.User;

public class LoginResultData {
	
	private User user = new User();
	private List<Land> lands = new ArrayList<Land>();
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Land> getLands() {
		return lands;
	}
	public void setLands(List<Land> lands) {
		this.lands = lands;
	}
	

}
