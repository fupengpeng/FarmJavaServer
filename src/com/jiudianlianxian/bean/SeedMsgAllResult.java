package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;
import com.jiudianlianxian.data.SeedMsgAllResultData;

/**
 * 
 * @Title: SeedMsgAllResult
 * @Description: 全部种子返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月27日 下午4:13:12
 *
 */
public class SeedMsgAllResult extends RequestResponseBase {

	private SeedMsgAllResultData seedMsgAllResultData = new SeedMsgAllResultData();

	public SeedMsgAllResultData getSeedMsgAllResultData() {
		return seedMsgAllResultData;
	}

	public void setSeedMsgAllResultData(
			SeedMsgAllResultData seedMsgAllResultData) {
		this.seedMsgAllResultData = seedMsgAllResultData;
	}

}
