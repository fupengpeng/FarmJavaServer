package com.jiudianlianxian.bean;

import com.jiudianlianxian.base.RequestResponseBase;


/**
 * 
 * @Title: PlantTimeResult
 * @Description: 获取种子种植时间返回实体
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月25日 下午4:35:23
 *
 */
public class ResidueTimeResult extends RequestResponseBase {
	private Long residueTime;

	public Long getResidueTime() {
		return residueTime;
	}

	public void setResidueTime(Long residueTime) {
		this.residueTime = residueTime;
	}


}
