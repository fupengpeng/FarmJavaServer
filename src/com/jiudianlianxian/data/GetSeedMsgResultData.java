package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Seed;

/**
 * 
 * @Title: GetSeedMsgResultData
 * @Description: ��ȡ�û�������Ϣ����ʵ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��28�� ����5:27:04
 *
 */
public class GetSeedMsgResultData {

	private List<Seed> seeds = new ArrayList<Seed>();

	public List<Seed> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<Seed> seeds) {
		this.seeds = seeds;
	}

}
