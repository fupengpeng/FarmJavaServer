package com.jiudianlianxian.data;

import com.jiudianlianxian.domain.Fruit;

/**
 * 
 * @Title: SellFruitResultData
 * @Description: 出售果实返回实体数据
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月19日 上午10:27:03
 *
 */
public class SellFruitResultData {

	private Fruit fruit;
	private Long userId;
	private Long userGold;

	public Fruit getFruit() {
		return fruit;
	}

	public void setFruit(Fruit fruit) {
		this.fruit = fruit;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserGold() {
		return userGold;
	}

	public void setUserGold(Long userGold) {
		this.userGold = userGold;
	}

}
