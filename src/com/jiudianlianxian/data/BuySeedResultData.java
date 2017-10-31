package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Seed;

/**
 * 
 * @Title: BuySeedResultData
 * @Description: �������ӷ���ʵ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��28�� ����4:02:37
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
