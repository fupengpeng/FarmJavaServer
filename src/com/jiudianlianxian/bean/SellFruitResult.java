package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.SellFruitResultData;


/**
 * 
 * @Title: SellFruitResult
 * @Description: 出售果实返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月19日 上午10:34:15
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
