package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Seed;

/**
 * 
 * @Title: SeedMsgAllResultData
 * @Description: 全部种子信息返回实体数据
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月27日 下午4:12:37
 *
 */
public class SeedMsgAllResultData {
	
	private List<Seed> seeds = new ArrayList<Seed>();

	public List<Seed> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<Seed> seeds) {
		this.seeds = seeds;
	}

	
	
	
	

}
