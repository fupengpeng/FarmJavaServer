package com.jiudianlianxian.domain;

import java.util.HashSet;
import java.util.Set;


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
public class Land {
	//土地id，主键
	private Long landId;
	//土地名称
	private Long landName; 
	/**
	 * 土地状态
	 *     1：未开垦
	 *     2：未种植，已开垦
	 *     3：土地种植，landSeed有值，为种植的种子对象
	 *     4：土地种子成熟
	 */
	private Long landState;
	
	private User landUser ;
	//土地种植时，地里种植的种子,用set集合，实际只有一个
//	private Seed landSeed ;
	private Set<Seed> landSeeds = new HashSet<Seed>();
	
	
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
	public User getLandUser() {
		return landUser;
	}
	public void setLandUser(User landUser) {
		this.landUser = landUser;
	}
	public Set<Seed> getLandSeeds() {
		return landSeeds;
	}
	public void setLandSeeds(Set<Seed> landSeeds) {
		this.landSeeds = landSeeds;
	}
	
	
	
	

	
	

}
