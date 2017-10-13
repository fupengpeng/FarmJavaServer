package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.FruitRipeResultData;


/**
 * 
 * @Title: FruitRipeResult
 * @Description: 果实成熟返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 下午1:49:54
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
