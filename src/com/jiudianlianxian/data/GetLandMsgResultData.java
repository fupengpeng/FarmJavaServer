package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Land;


/**
 * 
 * @Title: GetLandMsgResultData
 * @Description: 获取土地信息返回实体数据
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月28日 下午5:00:49
 *
 */
public class GetLandMsgResultData {
	
	private List<Land> lands = new ArrayList<Land>();

	public List<Land> getLands() {
		return lands;
	}

	public void setLands(List<Land> lands) {
		this.lands = lands;
	}
	

}
