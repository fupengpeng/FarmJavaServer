package com.jiudianlianxian.domain;

/**
 * 
 * @Title: Fruit
 * @Description: ������һ������
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: Farm
 * @author fupengpeng
 * @date 2017��10��9�� ����11:40:28
 *
 */
public class Fruit {

	//id ����
	private Long fruitId;
	//����
	private String fruitName;
	//����
	private Long fruitNumber;
	//���ۼ۸�
	private Long fruitSellingPrice;
	//ͼƬ
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
