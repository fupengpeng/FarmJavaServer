package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Land;


/**
 * 
 * @Title: GetLandMsgResultData
 * @Description: ��ȡ������Ϣ����ʵ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��28�� ����5:00:49
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
