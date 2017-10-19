package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.BuySeedResultData;


/**
 * 
 * @Title: BuySeedResult
 * @Description:  购买种子返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月28日 下午4:02:21
 *
 */
public class BuySeedResult extends RequestResponseBase {
	
	private BuySeedResultData buySeedResultData = new BuySeedResultData();

	public BuySeedResultData getBuySeedResultData() {
		return buySeedResultData;
	}

	public void setBuySeedResultData(BuySeedResultData buySeedResultData) {
		this.buySeedResultData = buySeedResultData;
	}

	
	

}
