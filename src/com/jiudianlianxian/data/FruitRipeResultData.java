package com.jiudianlianxian.data;

/**
 * 
 * @Title: FruitRipeData
 * @Description: 果实成熟返回实体数据
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年10月13日 下午1:42:48
 *
 */
public class FruitRipeResultData {

	private Long userId;
	private Long landId;
	private String landName;
	private Long landState;

	public Long getLandState() {
		return landState;
	}

	public void setLandState(Long landState) {
		this.landState = landState;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getLandId() {
		return landId;
	}

	public void setLandId(Long landId) {
		this.landId = landId;
	}

	public String getLandName() {
		return landName;
	}

	public void setLandName(String landName) {
		this.landName = landName;
	}

}
