package com.jiudianlianxian.data;

import com.jiudianlianxian.domain.Fruit;

/**
 * 
 * @Title: SellFruitResultData
 * @Description: ���۹�ʵ����ʵ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��19�� ����10:27:03
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
