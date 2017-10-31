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
 * Title: UsersService Description: User對應的數據庫處理類 Company: 济宁九点连线信息技术有限公司
 * ProjectName: UsersManager
 * 
 * @author fupengpeng
 * @date 2017年7月19日 上午9:17:44
 *
 */
public class JDBCService {
	Long seedState1 = 1L; // 种子状态----商店
	Long seedState2 = 2L; // 种子状态----背包
	Long seedState3 = 3L; // 种子状态----种植
	Long landState1 = 1L; // 土地状态----未开垦
	Long landState2 = 2L; // 土地状态----未种植，已开垦
	Long landState3 = 3L; // 土地状态----已种植，此时土地持有一个种子对象
	Long landState4 = 4L; // 土地状态----果实成熟

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
	 * @Description: 出售果实
	 * @param userId
	 * @param fruitId
	 * @param fruitNumber
	 * @return
	 */
	public SellFruitResultData sellFruit(Long userId, Long fruitId,
			Long fruitNumber) {
		SellFruitResultData sellFruitResultData = new SellFruitResultData(); // 出售果实返回数据
		Long userGold = null; // 用户出售果实之前的金币数量
		Fruit fruit = new Fruit(); // 查询果实表得到的数据

		// 根据传入果实id查询果实剩余数量，判断查询到的数量是否大于传入的数量，大于的话，删除数据库果实，改变用户所得金币，返回给客户端金币，和出售果实信息
		// 1.根据传入的fruitId查询farm_fruit表，获取Fruit对象（包含数据）
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

		// 2.判断查询到的fruit数量是否比传入的果实数量大
		if (fruit.getFruitNumber() >= fruitNumber) {
			// 数据库果实数量大于后等于传入的数量
			// 3.获取用户出售果实之前的金币数量
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
			if ((fruit.getFruitNumber() - fruitNumber) == 0) { // 判断传入的果实数量和数据库中果实数量相同则直接删除此果实
				// 4.删除数据库中此果实
				fruit.setFruitNumber(fruit.getFruitNumber());
				String updateFruitNumber = "delete from farm_fruit WHERE fruitId="
						+ fruitId;
				// 5.改变用户金币值
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
				// 4.改变数据库果实数量
				fruit.setFruitNumber(fruit.getFruitNumber());
				String updateFruitNumber = "update farm_fruit set fruitNumber="
						+ (fruit.getFruitNumber() - fruitNumber)
						+ " WHERE fruitId=" + fruitId;
				// 5.改变用户金币值
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

			// 6.设置返回数据，并返回
			fruit.setFruitNumber(fruit.getFruitNumber() - fruitNumber); // 返回给用户数据库果实数量
			sellFruitResultData.setFruit(fruit);
			sellFruitResultData.setUserGold(fruitNumber
					* fruit.getFruitSellingPrice() + userGold);

			return sellFruitResultData;
		} else {
			// 数据库果实数量小雨传入的数量

			sellFruitResultData.setFruit(fruit);

			return sellFruitResultData;
		}
	}

	/**
	 * 
	 * @Description: 打开仓库
	 * @param userId
	 * @return
	 */
	public OpenWarehouseResultData openWarehouse(Long userId) {
		OpenWarehouseResultData openWarehouseResultData = new OpenWarehouseResultData();
		// 根据userId查询farm_fruit表，获取其果实信息，输出
		List<Fruit> fruits = new ArrayList<Fruit>(); // 果实列表

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
	 * @Description: 收获
	 * @param landId
	 * @param userId
	 * @return
	 */
	public HarvestResultData harvest(Long landId, Long userId) {
		HarvestResultData harvestResultData = new HarvestResultData();
		// 1.根据传入土地id查询land_seed表，获取seedId
		Seed seed = new Seed(); // 种子对象，接收在land_seed查询到的seedId
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

		// 2.根据seedId查询farm_seed表，获取seed对象(信息)
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

		// 3.根据传入 userId查询farm_user表，获取用户经验值
		Long userExperience = null; // 收获前用户经验值
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

		// 4.判断果实是否成熟
		if (new Date().getTime() >= (seed.getSeedPlantTime() + seed
				.getSeedGrowthTime())) {
			// 果实已成熟
			// 4-1.删除land_seed表中土地与种子关系
			String deleteLandSeedRelation = "delete from land_seed WHERE landId="
					+ landId;
			try {
				JDBCUtil.executeUpdate(deleteLandSeedRelation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 4-2.删除种子表中种植在土地上的种子
			String deleteSeedStateThree = "delete from farm_seed WHERE seedId="
					+ seed.getSeedId();
			// 4-3.改变土地状态
			String updateLandState = "update farm_land set landState="
					+ landState2 + " WHERE landId=" + landId;
			// 4-4.改变用户经验值
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
			// 4-5.果实列表数据修改
			// 4-5-1。查询果实列表，获取要收获的果实信息
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
			// 4-5-2。判断是否有此果实，有的话改变数量，没有的话插入新的果实
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

			// 5.设置返回数据
			harvestResultData.setLandId(landId);
			harvestResultData.setLandState(landState2);
			harvestResultData.setSeedExperience(seed.getSeedExperience());
			harvestResultData.setUserId(userId);
			harvestResultData.setUserExperience(userExperience
					+ seed.getSeedExperience());
			harvestResultData.setFruitNumber(fruitNumber);

			return harvestResultData;

		} else {
			// 果实未成熟
			return harvestResultData;
		}

	}

	/**
	 * 
	 * @Description: 种植
	 * @param userId
	 * @param seedId
	 * @param landId
	 */
	public PlantResultData plant(Long userId, Long seedId, Long landId) {
		PlantResultData plantResultData = new PlantResultData();
		Long seedid = plantResultData.getSeedId();
		// 1.查询种子列表，获取此种子信息
		Seed seed = new Seed(); // 传入seedId的种子信息
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
		// 2.判断次种子数量是否大于0，给予客户提示
		if (seed.getSeedNumber() <= 0) { // 没有此种子，需要购买
			// 返回空的数据，没有此种子需要购买
			System.out.println("plantResultData----" + plantResultData);
			return plantResultData;
		} else {
			System.out.println("seedid    tt= " + seedid);
			// 3.查询土地列表，获取此土地状态
			Land land = new Land(); // 传入landId的土地信息
			String sql2 = "select * from farm_land where landId=" + landId;
			System.out.println("sql2 = " + sql2);
			ResultSet rs2 = JDBCUtil.executeQuery(sql2);
			try {
				while (rs2.next()) {
					land.setLandId(rs2.getLong(1));
					land.setLandName(rs2.getLong(2));
					land.setLandState(rs2.getLong(3));
					System.out.println("传入landId的土地状态    landState = "
							+ land.getLandState());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			System.out.println("sesdsdsdsdsdsdedid = " + seedid);
			// 4.判断此土地状态是否为2（已开垦，未种植），给予客户提示
			if (landState1 == land.getLandState()
					|| landState3 == land.getLandState()) {
				// 土地状态为1或者3，即土地已种植或者未开垦，提示用户不可以种植。
				System.out.println("seedid =----------------- " + seedid);
				System.out.println("plantResultData====" + plantResultData);
				return plantResultData;
			} else {

				// 5.种植
				// 5-1.改变背包中种子数量

				if ((seed.getSeedNumber() - 1) > 0) {
					// 5-1-1.减少传入seedId种子数量
					String sql4 = "UPDATE farm_seed SET " + "seedNumber="
							+ (seed.getSeedNumber() - 1) + " "
							+ "WHERE seedId=" + seedId + ";";
					System.out.println("改变id为landId的土地状态        landState =  "
							+ land.getLandState());
					System.out.println("sql4 = " + sql4);
					try {
						JDBCUtil.executeUpdate(sql4);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// 5-1-1.减少传入seedId种子数量
					String sql4 = "delete from farm_seed WHERE seedId="
							+ seedId + ";";
					System.out.println("改变id为landId的土地状态        landState =  "
							+ land.getLandState());
					System.out.println("sql4 = " + sql4);
					try {
						JDBCUtil.executeUpdate(sql4);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// 5-1-2.添加用户为传入userId，种子名为seedName，种子状态为3的种子以及种植时间，并获取到其seedId
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
					System.out.println("刚刚插入数据的id ---  11 = " + lastid);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-1-3.在land_seed表中添加获取到的seedId种子和传入的landId土地关系

				String sql = "INSERT INTO land_seed(" + "landId,seedId) "
						+ "VALUES(" + landId + "," + lastid + ") ;";
				System.out.println("sql====" + sql);
				try {
					JDBCUtil.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 5-2.改变土地状态
				String sql3 = "UPDATE farm_land SET " + "landState="
						+ landState3 + " WHERE landId=" + landId + ";";
				System.out.println("改变id为landId的土地状态        landState =  "
						+ land.getLandState());
				System.out.println("sql4 = " + sql3);
				try {
					JDBCUtil.executeUpdate(sql3);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-3.监视种子生长情况，实时给客户返回信息，每隔1分钟给客户端发送一次消息
				// 5-4.种子生长成果实，提示客户收取果实
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
	 * @Description: 根据用户id获取用户土地信息
	 * @param userId
	 * @return
	 */
	public GetLandMsgResultData getLandMsg(Long userId) {
		GetLandMsgResultData getLandMsgResultData = new GetLandMsgResultData();

		// 查询土地信息返回给客户端
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
		System.out.println("用户土地信息    lands = " + lands);
		getLandMsgResultData.setLands(lands);
		return getLandMsgResultData;
	}

	/**
	 * 
	 * @Description: 购买种子，并返回购买后用户数据
	 * @param userId
	 * @param seedId
	 * @param seedNumber
	 * @return
	 */
	public BuySeedResultData getBuySeedResultData(Long userId, Long seedId,
			int seedNumber) {
		BuySeedResultData buySeedResultData = new BuySeedResultData();
		boolean b = true;
		// 1.查询种子信息，计算购买所需金币
		int seedTotalPrices = 0; // 种子总价
		Seed seed = new Seed(); // 所要购买的种子信息
		// 查询种子单价，计算购买传入数量种子所需金币
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
				System.out.println("购买种子所需金币    seedTotalPrices = "
						+ seedTotalPrices + "    SeedName  ==  "
						+ rs1.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.查询用户金币信息，
		Long userGold = (long) 0; // 用户所有的金币数量
		String sql2 = "select * from farm_user where userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				userGold = rs2.getLong(4);
				System.out.println("用户金币数    price2 = " + userGold);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 3. 判断用户剩余金币是否够买种子，如不够直接返回提示用户
		if (userGold > seedTotalPrices) {
			// 3.金币够，则修改用户的种子信息和金币信息
			// 3-1.修改用户的金币数量
			String sql4 = "UPDATE farm_user SET userGold="
					+ (userGold - seedTotalPrices) + " WHERE userId=" + userId
					+ ";";
			System.out.println("设置用户金币剩余额  price2-price1 = "
					+ (userGold - seedTotalPrices));
			System.out.println("sql4 = " + sql4);
			try {
				JDBCUtil.executeUpdate(sql4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 3-2.查询种子表，用户是否购买过名称为seedName的种子
			Long seedIdStateTwo = null; // 已购买的种子id
			Long seedNumberStateTwo = null; // 已购买的种子数量

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
					System.out.println("查询该用户是否有该种子，rs3.getInt(3) = "
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
			if (b) { // 3-2-1.用户购买过名称为seedName的种子
				// 改变用户已有的该种子数量
				String sql5 = "UPDATE farm_seed SET " + "seedNumber="
						+ (seedNumberStateTwo + seedNumber) + " WHERE seedId="
						+ seedIdStateTwo + ";";
				System.out.println("sql5 = " + sql5);
				try {
					JDBCUtil.executeUpdate(sql5);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 给用户返回购买成功的信息及数据
				// 给用户返回数据
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			} else { // 3-2-2.用户没有购买过名称为seedName的种子，则添加此种子
				// 01.给用户添加新种子
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

				// 02.添加的新种子id
				long lastid = 0;
				try {
					lastid = JDBCUtil.executeUpdateGetId(sql7);
					System.out.println("刚刚插入数据的id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// 给用户返回数据
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			}
		} else {
			// 3.金币不够，给用户提示
			// 给用户返回数据
			return buySeedResultData;

		}

	}

	/**
	 * 
	 * @Description: 购买种子成功后的返回信息
	 * @param userId
	 * @param buySeedResultData
	 */
	private void resultBuySeedResult(Long userId,
			BuySeedResultData buySeedResultData) {
		// 1.查询用户金币信息，保存到返回数据实体
		String sql12 = "select * from farm_user where userId=" + userId;
		System.out.println("sql12 = " + sql12);
		ResultSet rs12 = JDBCUtil.executeQuery(sql12);
		long userGold = 0;
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs12.next()) {
				userGold = rs12.getLong(4);
				System.out.println("用户金币数    userGold = " + userGold);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs12, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.查询已购买种子信息，保存到返回数据实体中
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
		// 3.将查询到的数据保存到返回的实体类里面
		buySeedResultData.setUserGold(userGold);
		buySeedResultData.setUserSeeds(seeds);

	}

	/**
	 * 
	 * @Description: 获取所有种子信息
	 * @return
	 */
	public SeedMsgAllResultData getSeedMsgAll() {
		SeedMsgAllResultData seedMsgAllResultData = new SeedMsgAllResultData();
		List<Seed> seeds = new ArrayList<Seed>();

		// 查询数据库,获取状态为 1 的所有种子
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
	 * @Description: 微信登录网络请求数据解析
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
	 * @Description: 微信登录网络请求数据解析
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
	 * @Description: 微信登录网络请求数据解析
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
	 * @Description: 登录判断
	 * @return
	 */
	public boolean login(String code) {
		boolean b = true;

		return b;
	}

	/**
	 * 
	 * @Description: 微信登录网络请求
	 * @param code
	 * @return
	 */
	public void WeiXinlogin(String code, User user) {
		System.out.println("调用微信登录方法WeiXinlogin");

		System.out.println("请求导数据，返回用户数据---- = " + user);

	}

	/**
	 * 
	 * @Description: 根据用户id获取其种子信息
	 * @param userId
	 * @return
	 */
	public GetSeedMsgResultData getSeedMsg(Long userId) {
		GetSeedMsgResultData getSeedMsgResultData = new GetSeedMsgResultData();

		// 1.获取
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
				System.out.println("用户土地信息    lands = " + seed);
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
	 * @Description: 获取土地上种子的种植时间
	 * @param userId
	 * @param landId
	 * @return
	 */
	public Long getResidueTime(Long userId, Long landId) {
		// 1.根据传入的landId查询land_seed表，获取seedId
		Long seedId = null;
		Long plantTime = null; // 种植时间
		Long growthTime = null; // 生长时间
		Long residueTime = null; // 剩余时间
		Long nowTime = new Date().getTime(); // 当前时间
		String sql1 = "select * from land_seed where landId=" + landId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seedId = rs1.getLong(2);

				System.out.println("传入土地上种植的种子id  = " + seedId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 2.根据获取到的seedId和传入的userId,查询farm_seed表获取seedPlantTime
		String sql2 = "select * from farm_seed where seedId=" + seedId
				+ " and userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				growthTime = rs2.getLong(4);
				plantTime = rs2.getLong(14);
				System.out.println("传入土地上种植的种子种植时间  = " + plantTime);
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
		// 根据openid查询数据库，获取用户数据
		User user = new User();

		return user;
	}

	public LoginResultData getLoginResultData(List<Land> lands) {
		// 2.遍历lands，获取其land对象的landState，
		LoginResultData loginResponseData = new LoginResultData();
		List<LandData> landDatas = new ArrayList<LandData>();
		for (Land land : lands) {

			LandData landData = new LandData();
			landData.setLandId(land.getLandId());
			landData.setLandName(land.getLandName());
			landData.setLandState(land.getLandState());

			if (land.getLandState() == 3 || land.getLandState() == 4) {
				// 3.状态是3即种植状态，查询land_seed表，获取seedId
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
				// 根据获取到的seedId，查询farm_seed表，获取其seedName
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
