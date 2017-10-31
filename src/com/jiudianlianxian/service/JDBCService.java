package com.jiudianlianxian.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.HarvestResultData;
import com.jiudianlianxian.data.LandData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.OpenWarehouseResultData;
import com.jiudianlianxian.data.PlantResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.data.SellFruitResultData;
import com.jiudianlianxian.domain.Fruit;
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.util.JDBCUtil;

/**
 * 
 * Title: UsersService Description: User�����Ĕ�����̎��� Company: �����ŵ�������Ϣ�������޹�˾
 * ProjectName: UsersManager
 * 
 * @author fupengpeng
 * @date 2017��7��19�� ����9:17:44
 *
 */
public class JDBCService {
	Long seedState1 = 1L; // ����״̬----�̵�
	Long seedState2 = 2L; // ����״̬----����
	Long seedState3 = 3L; // ����״̬----��ֲ
	Long landState1 = 1L; // ����״̬----δ����
	Long landState2 = 2L; // ����״̬----δ��ֲ���ѿ���
	Long landState3 = 3L; // ����״̬----����ֲ����ʱ���س���һ�����Ӷ���
	Long landState4 = 4L; // ����״̬----��ʵ����

	JSONObject jsonObject;
	String access_token = null;
	int expires_in = 0;
	String refresh_token = null;
	String openid = null;
	String scope = null;

	int errcode = 0;
	String errmsg = null;

	String nickname = null;
	int sex = 0;
	String province = null;
	String city = null;
	String country = null;
	String headimgurl = null;
	JSONArray jsonArray = null;
	String[] privilege = null;
	String unionid = null;

	/**
	 * 
	 * @Description: ���۹�ʵ
	 * @param userId
	 * @param fruitId
	 * @param fruitNumber
	 * @return
	 */
	public SellFruitResultData sellFruit(Long userId, Long fruitId,
			Long fruitNumber) {
		SellFruitResultData sellFruitResultData = new SellFruitResultData(); // ���۹�ʵ��������
		Long userGold = null; // �û����۹�ʵ֮ǰ�Ľ������
		Fruit fruit = new Fruit(); // ��ѯ��ʵ��õ�������

		// ���ݴ����ʵid��ѯ��ʵʣ���������жϲ�ѯ���������Ƿ���ڴ�������������ڵĻ���ɾ�����ݿ��ʵ���ı��û����ý�ң����ظ��ͻ��˽�ң��ͳ��۹�ʵ��Ϣ
		// 1.���ݴ����fruitId��ѯfarm_fruit����ȡFruit���󣨰������ݣ�
		String sql1 = "select * from farm_fruit where fruitId=" + fruitId;
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				fruit.setFruitId(rs1.getLong(1));
				fruit.setFruitName(rs1.getString(2));
				fruit.setFruitNumber(rs1.getLong(3));
				fruit.setFruitSellingPrice(rs1.getLong(4));
				fruit.setFruitImage(rs1.getString(5));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.�жϲ�ѯ����fruit�����Ƿ�ȴ���Ĺ�ʵ������
		if (fruit.getFruitNumber() >= fruitNumber) {
			// ���ݿ��ʵ�������ں���ڴ��������
			// 3.��ȡ�û����۹�ʵ֮ǰ�Ľ������
			String sql2 = "select * from farm_user where userId=" + userId;
			ResultSet rs2 = JDBCUtil.executeQuery(sql2);
			try {
				while (rs2.next()) {
					userGold = rs2.getLong(4);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			System.out.println("fruitNumber = " + fruitNumber
					+ "     fruit.getFruitNumber() == "
					+ fruit.getFruitNumber());
			if ((fruit.getFruitNumber() - fruitNumber) == 0) { // �жϴ���Ĺ�ʵ���������ݿ��й�ʵ������ͬ��ֱ��ɾ���˹�ʵ
				// 4.ɾ�����ݿ��д˹�ʵ
				fruit.setFruitNumber(fruit.getFruitNumber());
				String updateFruitNumber = "delete from farm_fruit WHERE fruitId="
						+ fruitId;
				// 5.�ı��û����ֵ
				String updateUserGold = "update farm_user set userGold="
						+ (fruitNumber * fruit.getFruitSellingPrice() + userGold)
						+ " WHERE userId=" + userId;
				String[] sql = { updateFruitNumber, updateUserGold };
				try {
					JDBCUtil.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 4.�ı����ݿ��ʵ����
				fruit.setFruitNumber(fruit.getFruitNumber());
				String updateFruitNumber = "update farm_fruit set fruitNumber="
						+ (fruit.getFruitNumber() - fruitNumber)
						+ " WHERE fruitId=" + fruitId;
				// 5.�ı��û����ֵ
				String updateUserGold = "update farm_user set userGold="
						+ (fruitNumber * fruit.getFruitSellingPrice() + userGold)
						+ " WHERE userId=" + userId;
				String[] sql = { updateFruitNumber, updateUserGold };
				try {
					JDBCUtil.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 6.���÷������ݣ�������
			fruit.setFruitNumber(fruit.getFruitNumber() - fruitNumber); // ���ظ��û����ݿ��ʵ����
			sellFruitResultData.setFruit(fruit);
			sellFruitResultData.setUserGold(fruitNumber
					* fruit.getFruitSellingPrice() + userGold);

			return sellFruitResultData;
		} else {
			// ���ݿ��ʵ����С�괫�������

			sellFruitResultData.setFruit(fruit);

			return sellFruitResultData;
		}
	}

	/**
	 * 
	 * @Description: �򿪲ֿ�
	 * @param userId
	 * @return
	 */
	public OpenWarehouseResultData openWarehouse(Long userId) {
		OpenWarehouseResultData openWarehouseResultData = new OpenWarehouseResultData();
		// ����userId��ѯfarm_fruit����ȡ���ʵ��Ϣ�����
		List<Fruit> fruits = new ArrayList<Fruit>(); // ��ʵ�б�

		String sql2 = "select * from farm_fruit where userId=" + userId;
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				Fruit fruit = new Fruit();
				fruit.setFruitId(rs2.getLong(1));
				fruit.setFruitName(rs2.getString(2));
				fruit.setFruitNumber(rs2.getLong(3));
				fruit.setFruitSellingPrice(rs2.getLong(4));
				fruit.setFruitImage(rs2.getString(5));
				fruits.add(fruit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		openWarehouseResultData.setFruits(fruits);
		return openWarehouseResultData;
	}

	/**
	 * 
	 * @Description: �ջ�
	 * @param landId
	 * @param userId
	 * @return
	 */
	public HarvestResultData harvest(Long landId, Long userId) {
		HarvestResultData harvestResultData = new HarvestResultData();
		// 1.���ݴ�������id��ѯland_seed����ȡseedId
		Seed seed = new Seed(); // ���Ӷ��󣬽�����land_seed��ѯ����seedId
		String sql1 = "select * from land_seed where landId=" + landId;
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seed.setSeedId(rs1.getLong(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.����seedId��ѯfarm_seed����ȡseed����(��Ϣ)
		String sql2 = "select * from farm_seed where seedId="
				+ seed.getSeedId();
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				seed.setSeedName(rs2.getString(2));
				seed.setSeedGrowthTime(rs2.getLong(4));
				seed.setSeedExperience(rs2.getLong(7));
				seed.setSeedYield(rs2.getLong(8));
				seed.setSeedFruitSellingPrice(rs2.getLong(9));
				seed.setSeedImage(rs2.getString(11));
				seed.setSeedPlantTime(rs2.getLong(14));
				System.out.println("seed = " + seed);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 3.���ݴ��� userId��ѯfarm_user����ȡ�û�����ֵ
		Long userExperience = null; // �ջ�ǰ�û�����ֵ
		String sql3 = "select * from farm_user where userId=" + userId;
		ResultSet rs3 = JDBCUtil.executeQuery(sql3);
		try {
			while (rs3.next()) {
				userExperience = rs3.getLong(6);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs3, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 4.�жϹ�ʵ�Ƿ����
		if (new Date().getTime() >= (seed.getSeedPlantTime() + seed
				.getSeedGrowthTime())) {
			// ��ʵ�ѳ���
			// 4-1.ɾ��land_seed�������������ӹ�ϵ
			String deleteLandSeedRelation = "delete from land_seed WHERE landId="
					+ landId;
			try {
				JDBCUtil.executeUpdate(deleteLandSeedRelation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 4-2.ɾ�����ӱ�����ֲ�������ϵ�����
			String deleteSeedStateThree = "delete from farm_seed WHERE seedId="
					+ seed.getSeedId();
			// 4-3.�ı�����״̬
			String updateLandState = "update farm_land set landState="
					+ landState2 + " WHERE landId=" + landId;
			// 4-4.�ı��û�����ֵ
			String updateUserExperience = "update farm_user set userExperience="
					+ (userExperience + seed.getSeedExperience())
					+ " WHERE userId=" + userId;

			String[] sql = { deleteSeedStateThree, updateLandState,
					updateUserExperience };
			try {
				JDBCUtil.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 4-5.��ʵ�б������޸�
			// 4-5-1����ѯ��ʵ�б���ȡҪ�ջ�Ĺ�ʵ��Ϣ
			boolean isFruit = false;
			Long fruitId = null;
			Long fruitNumber = null;
			String sqlFruitId = "select * from farm_fruit where fruitName='"
					+ seed.getSeedName() + "' and userId=" + userId;
			ResultSet rsFruitId = JDBCUtil.executeQuery(sqlFruitId);
			try {
				while (rsFruitId.next()) {
					fruitId = rsFruitId.getLong(1);
					fruitNumber = rsFruitId.getLong(3);
					isFruit = true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rsFruitId, JDBCUtil.getPs(),
						JDBCUtil.getConnection());
			}
			// 4-5-2���ж��Ƿ��д˹�ʵ���еĻ��ı�������û�еĻ������µĹ�ʵ
			if (isFruit) {
				fruitNumber = fruitNumber + seed.getSeedYield();
				String updateFruitNumber = "update farm_fruit set fruitNumber="
						+ fruitNumber + " WHERE fruitId=" + fruitId;
				try {
					JDBCUtil.executeUpdate(updateFruitNumber);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				fruitNumber = seed.getSeedYield();
				String insertFruit = "insert into farm_fruit (fruitName,fruitNumber,fruitSellingPrice,fruitImage,userId) VALUES ('"
						+ seed.getSeedName()
						+ "',"
						+ fruitNumber
						+ ","
						+ seed.getSeedFruitSellingPrice()
						+ ",'"
						+ seed.getSeedImage() + "'," + userId + ")";
				try {
					JDBCUtil.executeUpdate(insertFruit);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// 5.���÷�������
			harvestResultData.setLandId(landId);
			harvestResultData.setLandState(landState2);
			harvestResultData.setSeedExperience(seed.getSeedExperience());
			harvestResultData.setUserId(userId);
			harvestResultData.setUserExperience(userExperience
					+ seed.getSeedExperience());
			harvestResultData.setFruitNumber(fruitNumber);

			return harvestResultData;

		} else {
			// ��ʵδ����
			return harvestResultData;
		}

	}

	/**
	 * 
	 * @Description: ��ֲ
	 * @param userId
	 * @param seedId
	 * @param landId
	 */
	public PlantResultData plant(Long userId, Long seedId, Long landId) {
		PlantResultData plantResultData = new PlantResultData();
		Long seedid = plantResultData.getSeedId();
		// 1.��ѯ�����б���ȡ��������Ϣ
		Seed seed = new Seed(); // ����seedId��������Ϣ
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seed.setSeedId(rs1.getLong(1));
				seed.setSeedName(rs1.getString(2));
				seed.setSeedState(rs1.getString(3));
				seed.setSeedGrowthTime(rs1.getLong(4));
				seed.setSeedBuyPrice(rs1.getLong(5));
				seed.setSeedSellingPrice(rs1.getLong(6));
				seed.setSeedExperience(rs1.getLong(7));
				seed.setSeedYield(rs1.getLong(8));
				seed.setSeedFruitSellingPrice(rs1.getLong(9));
				seed.setSeedType(rs1.getLong(10));
				seed.setSeedImage(rs1.getString(11));
				seed.setSeedNumber(rs1.getInt(12));
				seed.setSeedPlantTime(rs1.getLong(14));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 2.�жϴ����������Ƿ����0������ͻ���ʾ
		if (seed.getSeedNumber() <= 0) { // û�д����ӣ���Ҫ����
			// ���ؿյ����ݣ�û�д�������Ҫ����
			System.out.println("plantResultData----" + plantResultData);
			return plantResultData;
		} else {
			System.out.println("seedid    tt= " + seedid);
			// 3.��ѯ�����б���ȡ������״̬
			Land land = new Land(); // ����landId��������Ϣ
			String sql2 = "select * from farm_land where landId=" + landId;
			System.out.println("sql2 = " + sql2);
			ResultSet rs2 = JDBCUtil.executeQuery(sql2);
			try {
				while (rs2.next()) {
					land.setLandId(rs2.getLong(1));
					land.setLandName(rs2.getLong(2));
					land.setLandState(rs2.getLong(3));
					System.out.println("����landId������״̬    landState = "
							+ land.getLandState());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			System.out.println("sesdsdsdsdsdsdedid = " + seedid);
			// 4.�жϴ�����״̬�Ƿ�Ϊ2���ѿ��ѣ�δ��ֲ��������ͻ���ʾ
			if (landState1 == land.getLandState()
					|| landState3 == land.getLandState()) {
				// ����״̬Ϊ1����3������������ֲ����δ���ѣ���ʾ�û���������ֲ��
				System.out.println("seedid =----------------- " + seedid);
				System.out.println("plantResultData====" + plantResultData);
				return plantResultData;
			} else {

				// 5.��ֲ
				// 5-1.�ı䱳������������

				if ((seed.getSeedNumber() - 1) > 0) {
					// 5-1-1.���ٴ���seedId��������
					String sql4 = "UPDATE farm_seed SET " + "seedNumber="
							+ (seed.getSeedNumber() - 1) + " "
							+ "WHERE seedId=" + seedId + ";";
					System.out.println("�ı�idΪlandId������״̬        landState =  "
							+ land.getLandState());
					System.out.println("sql4 = " + sql4);
					try {
						JDBCUtil.executeUpdate(sql4);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// 5-1-1.���ٴ���seedId��������
					String sql4 = "delete from farm_seed WHERE seedId="
							+ seedId + ";";
					System.out.println("�ı�idΪlandId������״̬        landState =  "
							+ land.getLandState());
					System.out.println("sql4 = " + sql4);
					try {
						JDBCUtil.executeUpdate(sql4);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// 5-1-2.����û�Ϊ����userId��������ΪseedName������״̬Ϊ3�������Լ���ֲʱ�䣬����ȡ����seedId
				Long seedPlantTime = new Date().getTime();
				String sql5 = "INSERT INTO farm_seed("
						+ "seedName,seedState,seedGrowthTime,seedBuyPrice,seedSellingPrice,"
						+ "seedExperience,seedYield,seedFruitSellingPrice,seedType,seedImage,"
						+ "seedNumber,userId,seedPlantTime)" + "VALUES(" + "'"
						+ seed.getSeedName()
						+ "','"
						+ landState3
						+ "',"
						+ seed.getSeedGrowthTime()
						+ ","
						+ seed.getSeedBuyPrice()
						+ ","
						+ seed.getSeedSellingPrice()
						+ ","
						+ seed.getSeedExperience()
						+ ","
						+ seed.getSeedYield()
						+ ","
						+ seed.getSeedFruitSellingPrice()
						+ ","
						+ seed.getSeedType()
						+ ",'"
						+ seed.getSeedImage()
						+ "'," + 1 + "," + userId + "," + seedPlantTime + ") ";
				System.out.println("sql5====" + sql5);
				long lastid = 0;
				try {
					lastid = JDBCUtil.executeUpdateGetId(sql5);
					System.out.println("�ող������ݵ�id ---  11 = " + lastid);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-1-3.��land_seed������ӻ�ȡ����seedId���Ӻʹ����landId���ع�ϵ

				String sql = "INSERT INTO land_seed(" + "landId,seedId) "
						+ "VALUES(" + landId + "," + lastid + ") ;";
				System.out.println("sql====" + sql);
				try {
					JDBCUtil.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 5-2.�ı�����״̬
				String sql3 = "UPDATE farm_land SET " + "landState="
						+ landState3 + " WHERE landId=" + landId + ";";
				System.out.println("�ı�idΪlandId������״̬        landState =  "
						+ land.getLandState());
				System.out.println("sql4 = " + sql3);
				try {
					JDBCUtil.executeUpdate(sql3);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-3.�����������������ʵʱ���ͻ�������Ϣ��ÿ��1���Ӹ��ͻ��˷���һ����Ϣ
				// 5-4.���������ɹ�ʵ����ʾ�ͻ���ȡ��ʵ
				plantResultData.setSeedNumber(seed.getSeedNumber() - 1);
				plantResultData.setSeedName(seed.getSeedName());
				land.setLandState(seedState3);
				plantResultData.setLand(land);
				return plantResultData;
			}

		}

	}

	/**
	 * 
	 * @Description: �����û�id��ȡ�û�������Ϣ
	 * @param userId
	 * @return
	 */
	public GetLandMsgResultData getLandMsg(Long userId) {
		GetLandMsgResultData getLandMsgResultData = new GetLandMsgResultData();

		// ��ѯ������Ϣ���ظ��ͻ���
		String sql = "select * from farm_land where userId=" + userId;
		List<Land> lands = new ArrayList<Land>();
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		try {
			while (rs.next()) {
				Land land = new Land();
				land.setLandId(rs.getLong(1));
				land.setLandName(rs.getLong(2));
				land.setLandState(rs.getLong(3));
				lands.add(land);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		System.out.println("�û�������Ϣ    lands = " + lands);
		getLandMsgResultData.setLands(lands);
		return getLandMsgResultData;
	}

	/**
	 * 
	 * @Description: �������ӣ������ع�����û�����
	 * @param userId
	 * @param seedId
	 * @param seedNumber
	 * @return
	 */
	public BuySeedResultData getBuySeedResultData(Long userId, Long seedId,
			int seedNumber) {
		BuySeedResultData buySeedResultData = new BuySeedResultData();
		boolean b = true;
		// 1.��ѯ������Ϣ�����㹺��������
		int seedTotalPrices = 0; // �����ܼ�
		Seed seed = new Seed(); // ��Ҫ�����������Ϣ
		// ��ѯ���ӵ��ۣ����㹺������������������
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seedTotalPrices = seedNumber * (rs1.getInt(5));
				seed.setSeedName(rs1.getString(2));
				seed.setSeedGrowthTime(rs1.getLong(4));
				seed.setSeedBuyPrice(rs1.getLong(5));
				seed.setSeedSellingPrice(rs1.getLong(6));
				seed.setSeedExperience(rs1.getLong(7));
				seed.setSeedYield(rs1.getLong(8));
				seed.setSeedFruitSellingPrice(rs1.getLong(9));
				seed.setSeedType(rs1.getLong(10));
				seed.setSeedImage(rs1.getString(11));
				System.out.println("��������������    seedTotalPrices = "
						+ seedTotalPrices + "    SeedName  ==  "
						+ rs1.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.��ѯ�û������Ϣ��
		Long userGold = (long) 0; // �û����еĽ������
		String sql2 = "select * from farm_user where userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				userGold = rs2.getLong(4);
				System.out.println("�û������    price2 = " + userGold);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 3. �ж��û�ʣ�����Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
		if (userGold > seedTotalPrices) {
			// 3.��ҹ������޸��û���������Ϣ�ͽ����Ϣ
			// 3-1.�޸��û��Ľ������
			String sql4 = "UPDATE farm_user SET userGold="
					+ (userGold - seedTotalPrices) + " WHERE userId=" + userId
					+ ";";
			System.out.println("�����û����ʣ���  price2-price1 = "
					+ (userGold - seedTotalPrices));
			System.out.println("sql4 = " + sql4);
			try {
				JDBCUtil.executeUpdate(sql4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 3-2.��ѯ���ӱ��û��Ƿ��������ΪseedName������
			Long seedIdStateTwo = null; // �ѹ��������id
			Long seedNumberStateTwo = null; // �ѹ������������

			String sql3 = "select * from farm_seed where userId=" + userId
					+ " and seedState=" + seedState2 + " and seedName='"
					+ seed.getSeedName() + "'";
			System.out.println("sql3 = " + sql3);
			b = false;
			ResultSet rs3 = JDBCUtil.executeQuery(sql3);
			try {
				while (rs3.next()) {
					b = true;
					rs3.getInt(3);
					seedIdStateTwo = rs3.getLong(1);
					seedNumberStateTwo = rs3.getLong(12);
					System.out.println("��ѯ���û��Ƿ��и����ӣ�rs3.getInt(3) = "
							+ rs3.getInt(3) + "  seedIdStateTwo = "
							+ seedIdStateTwo + "  seedNumberStateTwo = "
							+ seedNumberStateTwo);
				}
			} catch (SQLException e) {
				b = false;
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs3, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			System.out.println("b = " + b);
			if (b) { // 3-2-1.�û����������ΪseedName������
				// �ı��û����еĸ���������
				String sql5 = "UPDATE farm_seed SET " + "seedNumber="
						+ (seedNumberStateTwo + seedNumber) + " WHERE seedId="
						+ seedIdStateTwo + ";";
				System.out.println("sql5 = " + sql5);
				try {
					JDBCUtil.executeUpdate(sql5);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// ���û����ع���ɹ�����Ϣ������
				// ���û���������
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			} else { // 3-2-2.�û�û�й��������ΪseedName�����ӣ�����Ӵ�����
				// 01.���û����������
				String sql7 = "insert into farm_seed ("
						+ "seedName,seedState,seedGrowthTime,"
						+ "seedBuyPrice,seedSellingPrice,seedExperience,"
						+ "seedYield,seedFruitSellingPrice,seedType,"
						+ "seedImage,seedNumber,userId" + ") values('"
						+ seed.getSeedName()
						+ "','"
						+ seedState2
						+ "',"
						+ seed.getSeedGrowthTime()
						+ ","
						+ seed.getSeedBuyPrice()
						+ ","
						+ seed.getSeedSellingPrice()
						+ ","
						+ seed.getSeedExperience()
						+ ","
						+ seed.getSeedYield()
						+ ","
						+ seed.getSeedFruitSellingPrice()
						+ ","
						+ seed.getSeedType()
						+ ",'"
						+ seed.getSeedImage()
						+ "'," + seedNumber + "," + userId + ")";
				System.out.println("sql7====" + sql7);

				// 02.��ӵ�������id
				long lastid = 0;
				try {
					lastid = JDBCUtil.executeUpdateGetId(sql7);
					System.out.println("�ող������ݵ�id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// ���û���������
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			}
		} else {
			// 3.��Ҳ��������û���ʾ
			// ���û���������
			return buySeedResultData;

		}

	}

	/**
	 * 
	 * @Description: �������ӳɹ���ķ�����Ϣ
	 * @param userId
	 * @param buySeedResultData
	 */
	private void resultBuySeedResult(Long userId,
			BuySeedResultData buySeedResultData) {
		// 1.��ѯ�û������Ϣ�����浽��������ʵ��
		String sql12 = "select * from farm_user where userId=" + userId;
		System.out.println("sql12 = " + sql12);
		ResultSet rs12 = JDBCUtil.executeQuery(sql12);
		long userGold = 0;
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs12.next()) {
				userGold = rs12.getLong(4);
				System.out.println("�û������    userGold = " + userGold);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs12, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.��ѯ�ѹ���������Ϣ�����浽��������ʵ����
		String sql = "select * from farm_seed where userId=" + userId
				+ " and seedState=" + seedState2;
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		List<Seed> seeds = new ArrayList<Seed>();
		try {
			while (rs.next()) {
				Seed seed = new Seed();
				seed.setSeedId(rs.getLong(1));
				seed.setSeedName(rs.getString(2));
				seed.setSeedState(rs.getString(3));
				seed.setSeedGrowthTime(rs.getLong(4));
				seed.setSeedBuyPrice(rs.getLong(5));
				seed.setSeedSellingPrice(rs.getLong(6));
				seed.setSeedExperience(rs.getLong(7));
				seed.setSeedYield(rs.getLong(8));
				seed.setSeedFruitSellingPrice(rs.getLong(9));
				seed.setSeedType(rs.getLong(10));
				seed.setSeedImage(rs.getString(11));
				seed.setSeedNumber(rs.getInt(12));
				seeds.add(seed);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 3.����ѯ�������ݱ��浽���ص�ʵ��������
		buySeedResultData.setUserGold(userGold);
		buySeedResultData.setUserSeeds(seeds);

	}

	/**
	 * 
	 * @Description: ��ȡ����������Ϣ
	 * @return
	 */
	public SeedMsgAllResultData getSeedMsgAll() {
		SeedMsgAllResultData seedMsgAllResultData = new SeedMsgAllResultData();
		List<Seed> seeds = new ArrayList<Seed>();

		// ��ѯ���ݿ�,��ȡ״̬Ϊ 1 ����������
		String sql = "select * from farm_seed where seedState='1'";
		ResultSet rs = JDBCUtil.executeQuery(sql);
		try {
			while (rs.next()) {
				Seed seed = new Seed();
				seed.setSeedId(rs.getLong(1));
				seed.setSeedName(rs.getString(2));
				seed.setSeedState(rs.getString(3));
				seed.setSeedGrowthTime(rs.getLong(4));
				seed.setSeedBuyPrice(rs.getLong(5));
				seed.setSeedSellingPrice(rs.getLong(6));
				seed.setSeedExperience(rs.getLong(7));
				seed.setSeedYield(rs.getLong(8));
				seed.setSeedFruitSellingPrice(rs.getLong(9));
				seed.setSeedType(rs.getLong(10));
				seed.setSeedImage(rs.getString(11));
				seeds.add(seed);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		seedMsgAllResultData.setSeeds(seeds);
		return seedMsgAllResultData;
	}

	/**
	 * 
	 * @Description: ΢�ŵ�¼�����������ݽ���
	 * @param json
	 */
	public void analysisGetAndRefreshAccess_tokenSuccess(String json) {
		try {
			jsonObject = new JSONObject(json);
			access_token = jsonObject.getString("access_token");
			expires_in = jsonObject.getInt("expires_in");
			refresh_token = jsonObject.getString("refresh_token");
			openid = jsonObject.getString("openid");
			scope = jsonObject.getString("scope");

		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: ΢�ŵ�¼�����������ݽ���
	 * @param json
	 */
	public void analysisDefeated(String json) {
		try {
			jsonObject = new JSONObject(json);
			errcode = jsonObject.getInt("errcode");
			errmsg = jsonObject.getString("errmsg");
		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: ΢�ŵ�¼�����������ݽ���
	 * @param json
	 */
	public void analysisGetUnionID(String json) {
		try {
			jsonObject = new JSONObject(json);
			openid = jsonObject.getString("openid");
			nickname = jsonObject.getString("nickname");
			sex = jsonObject.getInt("sex");
			province = jsonObject.getString("province");

			city = jsonObject.getString("city");
			country = jsonObject.getString("country");
			headimgurl = jsonObject.getString("headimgurl");
			jsonArray = jsonObject.getJSONArray("privilege");
			for (int i = 0; i < jsonArray.length(); i++) {
				privilege[i] = jsonArray.getString(i);
			}
			unionid = jsonObject.getString("unionid");

		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: ��¼�ж�
	 * @return
	 */
	public boolean login(String code) {
		boolean b = true;

		return b;
	}

	/**
	 * 
	 * @Description: ΢�ŵ�¼��������
	 * @param code
	 * @return
	 */
	public void WeiXinlogin(String code, User user) {
		System.out.println("����΢�ŵ�¼����WeiXinlogin");

		System.out.println("�������ݣ������û�����---- = " + user);

	}

	/**
	 * 
	 * @Description: �����û�id��ȡ��������Ϣ
	 * @param userId
	 * @return
	 */
	public GetSeedMsgResultData getSeedMsg(Long userId) {
		GetSeedMsgResultData getSeedMsgResultData = new GetSeedMsgResultData();

		// 1.��ȡ
		String sql = "select * from farm_seed where seedState=" + seedState2
				+ " and userId=" + userId;
		List<Seed> seeds = new ArrayList<Seed>();
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		try {
			while (rs.next()) {
				Seed seed = new Seed();
				seed.setSeedId(rs.getLong(1));
				seed.setSeedName(rs.getString(2));
				seed.setSeedState(rs.getString(3));
				seed.setSeedGrowthTime(rs.getLong(4));
				seed.setSeedBuyPrice(rs.getLong(5));
				seed.setSeedSellingPrice(rs.getLong(6));
				seed.setSeedExperience(rs.getLong(7));
				seed.setSeedYield(rs.getLong(8));
				seed.setSeedFruitSellingPrice(rs.getLong(9));
				seed.setSeedType(rs.getLong(10));
				seed.setSeedImage(rs.getString(11));
				seed.setSeedNumber(rs.getInt(12));

				seeds.add(seed);
				System.out.println("�û�������Ϣ    lands = " + seed);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		getSeedMsgResultData.setSeeds(seeds);
		return getSeedMsgResultData;
	}

	/**
	 * 
	 * @Description: ��ȡ���������ӵ���ֲʱ��
	 * @param userId
	 * @param landId
	 * @return
	 */
	public Long getResidueTime(Long userId, Long landId) {
		// 1.���ݴ����landId��ѯland_seed����ȡseedId
		Long seedId = null;
		Long plantTime = null; // ��ֲʱ��
		Long growthTime = null; // ����ʱ��
		Long residueTime = null; // ʣ��ʱ��
		Long nowTime = new Date().getTime(); // ��ǰʱ��
		String sql1 = "select * from land_seed where landId=" + landId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seedId = rs1.getLong(2);

				System.out.println("������������ֲ������id  = " + seedId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 2.���ݻ�ȡ����seedId�ʹ����userId,��ѯfarm_seed���ȡseedPlantTime
		String sql2 = "select * from farm_seed where seedId=" + seedId
				+ " and userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				growthTime = rs2.getLong(4);
				plantTime = rs2.getLong(14);
				System.out.println("������������ֲ��������ֲʱ��  = " + plantTime);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		residueTime = (plantTime + growthTime) - nowTime;
		if (residueTime > 0) {
			return residueTime;
		} else {
			residueTime = 0L;
			return residueTime;
		}
	}

	public User getUser(String openid2) {
		// ����openid��ѯ���ݿ⣬��ȡ�û�����
		User user = new User();

		return user;
	}

	public LoginResultData getLoginResultData(List<Land> lands) {
		// 2.����lands����ȡ��land�����landState��
		LoginResultData loginResponseData = new LoginResultData();
		List<LandData> landDatas = new ArrayList<LandData>();
		for (Land land : lands) {

			LandData landData = new LandData();
			landData.setLandId(land.getLandId());
			landData.setLandName(land.getLandName());
			landData.setLandState(land.getLandState());

			if (land.getLandState() == 3 || land.getLandState() == 4) {
				// 3.״̬��3����ֲ״̬����ѯland_seed����ȡseedId
				Long seedId = null;
				String sql = "select * from land_seed where landId="
						+ land.getLandId();
				ResultSet rs = JDBCUtil.executeQuery(sql);
				try {
					while (rs.next()) {
						seedId = rs.getLong(2);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
				}
				// ���ݻ�ȡ����seedId����ѯfarm_seed����ȡ��seedName
				String sql2 = "select * from farm_seed where seedId=" + seedId;
				ResultSet rs2 = JDBCUtil.executeQuery(sql2);
				try {
					while (rs2.next()) {
						landData.setSeedName(rs2.getString(2));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs2, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
				}

			} else {
				landData.setSeedName("");
			}
			landDatas.add(landData);
		}
		loginResponseData.setLandDatas(landDatas);
		return loginResponseData;
	}
}
