package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Seed;

/**
 * 
 * @Title: BuySeedResultData
 * @Description: 购买种子返回实体数据
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月28日 下午4:02:37
 *
 */
public class BuySeedResultData {

	private Long userGold;
	private List<Seed> userSeeds = new ArrayList<Seed>();

	public Long getUserGold() {
		return userGold;
	}

	public void setUserGold(Long userGold) {
		this.userGold = userGold;
	}

	public List<Seed> getUserSeeds() {
		return userSeeds;
	}

	public void setUserSeeds(List<Seed> userSeeds) {
		this.userSeeds = userSeeds;
	}

}
