package com.jiudianlianxian.data;



/**
 * 
 * @Title: Land
 * @Description: 给此类一个描述
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017年9月26日 上午10:36:51
 *
 */
public class LandData {
	//土地id，主键
	private Long landId;
	//土地名称
	private Long landName; 
	/**
	 * 土地状态
	 *     1：土地种植，landSeed有值，为种植的种子对象
	 *     2：未种植，已开垦
	 *     3：未开垦
	 */
	private Long landState;
	private String seedName;
	
	
	public Long getLandId() {
		return landId;
	}
	public void setLandId(Long landId) {
		this.landId = landId;
	}
	
	public Long getLandName() {
		return landName;
	}
	public void setLandName(Long landName) {
		this.landName = landName;
	}
	
	public Long getLandState() {
		return landState;
	}
	public void setLandState(Long landState) {
		this.landState = landState;
	}
	public String getSeedName() {
		return seedName;
	}
	public void setSeedName(String seedName) {
		this.seedName = seedName;
	}

	
	

}
