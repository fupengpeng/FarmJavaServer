package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.SellFruitResultData;


/**
 * 
 * @Title: SellFruitResult
 * @Description: ���۹�ʵ����ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��19�� ����10:34:15
 *
 */
public class SellFruitResult extends RequestResponseBase {
	
	private SellFruitResultData sellFruitResultData;

	public SellFruitResultData getSellFruitResultData() {
		return sellFruitResultData;
	}

	public void setSellFruitResultData(SellFruitResultData sellFruitResultData) {
		this.sellFruitResultData = sellFruitResultData;
	}
	

}
