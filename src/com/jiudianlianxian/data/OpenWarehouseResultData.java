package com.jiudianlianxian.data;

import java.util.ArrayList;
import java.util.List;

import com.jiudianlianxian.domain.Fruit;

/**
 * 
 * @Title: OpenWarehouseResultData
 * @Description: 打开仓库返回实体数据
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 下午4:34:45
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
