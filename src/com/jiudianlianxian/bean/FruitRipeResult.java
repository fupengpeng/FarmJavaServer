package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.FruitRipeResultData;


/**
 * 
 * @Title: FruitRipeResult
 * @Description: ��ʵ���췵��ʵ��
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����1:49:54
 *
 */
public class FruitRipeResult extends RequestResponseBase {
	
	private FruitRipeResultData fruitRipeResultData = new FruitRipeResultData();

	public FruitRipeResultData getFruitRipeResultData() {
		return fruitRipeResultData;
	}

	public void setFruitRipeResultData(FruitRipeResultData fruitRipeResultData) {
		this.fruitRipeResultData = fruitRipeResultData;
	}
	
	
	
}
