package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Fruit;

/**
 * 
 * @Title: OpenWarehouseResultData
 * @Description: �򿪲ֿⷵ��ʵ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��10��13�� ����4:34:45
 *
 */
public class OpenWarehouseResultData {

	List<Fruit> fruits = new ArrayList<Fruit>();

	public List<Fruit> getFruits() {
		return fruits;
	}

	public void setFruits(List<Fruit> fruits) {
		this.fruits = fruits;
	}

}
