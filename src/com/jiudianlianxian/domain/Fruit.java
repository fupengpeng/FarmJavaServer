package com.jiudianlianxian.domain;

/**
 * 
 * @Title: Fruit
 * @Description: 给此类一个描述
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017年10月9日 上午11:40:28
 *
 */
public class Fruit {

	//id 主键
	private Long fruitId;
	//名称
	private String fruitName;
	//数量
	private Long fruitNumber;
	//出售价格
	private Long fruitSellingPrice;
	//图片
	private String fruitImage;
	private User fruitUser ;
	
	
	public Long getFruitId() {
		return fruitId;
	}
	public void setFruitId(Long fruitId) {
		this.fruitId = fruitId;
	}
	public String getFruitName() {
		return fruitName;
	}
	public void setFruitName(String fruitName) {
		this.fruitName = fruitName;
	}
	public Long getFruitNumber() {
		return fruitNumber;
	}
	public void setFruitNumber(Long fruitNumber) {
		this.fruitNumber = fruitNumber;
	}
	public Long getFruitSellingPrice() {
		return fruitSellingPrice;
	}
	public void setFruitSellingPrice(Long fruitSellingPrice) {
		this.fruitSellingPrice = fruitSellingPrice;
	}
	public String getFruitImage() {
		return fruitImage;
	}
	public void setFruitImage(String fruitImage) {
		this.fruitImage = fruitImage;
	}
	public User getFruitUser() {
		return fruitUser;
	}
	public void setFruitUser(User fruitUser) {
		this.fruitUser = fruitUser;
	}
	
	
	 
}
