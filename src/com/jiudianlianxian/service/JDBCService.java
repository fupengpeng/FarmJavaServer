package com.jiudianlianxian.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.RepaintManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jiudianlianxian.bean.FruitRipeResult;
import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.FruitRipeResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.HarvestResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.OpenWarehouseResultData;
import com.jiudianlianxian.data.PlantResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.RipeMessage;
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;

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

	/**
	 * 
	 * @Description: 打开仓库
	 * @param userId
	 * @return
	 */
	public OpenWarehouseResultData openWarehouse(Long userId) {
		OpenWarehouseResultData openWarehouseResultData = new OpenWarehouseResultData();

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
		Seed seed = new Seed();
		String sql1 = "select * from land_seed where landId=" + landId;
		System.out.println("sql1 = " + sql1);
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
		System.out.println("sql2 = " + sql2);
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

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 3.根据传入 userId查询farm_user表，获取用户经验值
		Long userExperience = null;
		String sql3 = "select * from farm_user where userId=" + userId;
		System.out.println("sql3 = " + sql3);
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
			String updateLandState = "update farm_land set landState='2' WHERE landId="
					+ landId;
			// 4-4.改变用户经验值
			String updateUserExperience = "update farm_user set userExperience="
					+ (userExperience + seed.getSeedExperience())
					+ " WHERE userId=" + userId;
			// 4-5.在果实列表添加果实
			String insertFruit = "insert into farm_fruit (fruitName,fruitNumber,fruitSellingPrice,fruitImage,userId) VALUES ('"
					+ seed.getSeedName()
					+ "',"
					+ seed.getSeedYield()
					+ ","
					+ seed.getSeedFruitSellingPrice()
					+ ",'"
					+ seed.getSeedImage() + "'," + userId + ")";
			String[] sql = { deleteSeedStateThree, updateLandState,
					updateUserExperience, insertFruit };
			try {
				JDBCUtil.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}

			harvestResultData.setLandId(landId);
			harvestResultData.setLandState("2");
			harvestResultData.setSeedExperience(seed.getSeedExperience());
			harvestResultData.setUserId(userId);
			harvestResultData.setUserExperience(userExperience
					+ seed.getSeedExperience());
			harvestResultData.setFruitNumber(seed.getSeedYield());

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
		String landState = "1";
		// 1.查询种子列表，获取此种子信息
		Seed seed = new Seed(); // 传入seedId的种子信息
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seed.setSeedId(rs1.getLong(1));
				seed.setSeedNumber(rs1.getInt(12));
				System.out.println("传入seedId的种子数量    seedNumber = "
						+ seed.getSeedNumber());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 2.判断次种子数量是否大于0，给予客户提示
		if (seed.getSeedNumber() < 0) { // 没有此种子，需要购买
			// 返回空的数据，没有此种子需要购买
			return plantResultData;
		} else {
			// 3.查询土地列表，获取此土地状态
			Land land = new Land(); // 传入landId的土地信息
			String sql2 = "select * from farm_land where landId=" + landId;
			System.out.println("sql2 = " + sql2);
			ResultSet rs2 = JDBCUtil.executeQuery(sql2);
			try {
				while (rs2.next()) {
					land.setLandId(rs2.getLong(1));
					land.setLandName(rs2.getString(2));
					land.setLandState(rs2.getString(3));
					System.out.println("传入landId的土地状态    landState = "
							+ land.getLandState());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}

			// 4.判断此土地状态是否为2（已开垦，未种植），给予客户提示
			if (landState.equals(land.getLandState())
					|| "3".equals(land.getLandState())) {
				// 土地状态为1或者3，即土地已种植或者未开垦，提示用户不可以种植。
				return plantResultData;
			} else {
				// 5.种植
				// 5-1.改变背包中种子数量
				// 5-1-1.减少传入seedId种子数量
				String sql4 = "UPDATE farm_seed SET " + "seedNumber="
						+ (seed.getSeedNumber() - 1) + " " + "WHERE seedId="
						+ seedId + ";";
				System.out.println("改变id为landId的土地状态        landState =  "
						+ land.getLandState());
				System.out.println("sql4 = " + sql4);
				try {
					JDBCUtil.executeUpdate(sql4);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-1-2.添加用户为传入userId，种子名为seedName，种子状态为3的种子以及种植时间，并获取到其seedId
				Long seedPlantTime = new Date().getTime();
				String sql5 = "INSERT INTO farm_seed("
						+ "seedName,seedState,seedGrowthTime,seedPlantTime,seedSellingPrice,seedImage,"
						+ "seedNumber)" + "VALUES('seedname','" + 3 + "',"
						+ seed.getSeedGrowthTime() + "," + seedPlantTime + ","
						+ seed.getSeedSellingPrice() + ",'"
						+ seed.getSeedImage() + "'," + 1 + ") ";
				System.out.println("sql5====" + sql5);
				long lastid = 0;
				try {
					lastid = JDBCUtil.executeUpdateGetId(sql5);
					System.out.println("刚刚插入数据的id ---  11 = " + lastid);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-1-3.在land_seed表中添加获取到的seedId种子和传入的landId土地关系
				boolean b = true;
				String sql = "INSERT INTO land_seed(" + "landId,seedId) "
						+ "VALUES(" + landId + "," + lastid + ") ;";
				System.out.println("sql====" + sql);
				try {
					JDBCUtil.executeUpdate(sql);
				} catch (Exception e) {
					b = false;
					e.printStackTrace();
				}
				// 5-2.改变土地状态
				String sql3 = "UPDATE farm_land SET " + "landState='"
						+ landState + "' " + "WHERE landId=" + landId + ";";
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
				land.setLandState("1");
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
				land.setLandName(rs.getString(2));
				land.setLandState(rs.getString(3));
				lands.add(land);
				System.out.println("用户土地信息    lands = " + lands);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
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
	public BuySeedResultData getBuySeedResultData(String userId, String seedId,
			String seedNumber) {
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
				seedTotalPrices = (int) (Integer.parseInt(seedNumber) * (rs1
						.getInt(5)));
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
						+ seedTotalPrices);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.查询用户金币信息，
		int userGold = 0; // 用户所有的金币数量
		String sql2 = "select * from farm_user where userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				userGold = rs2.getInt(4);
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
			String sql4 = "UPDATE farm_user SET " + "userGold='"
					+ (userGold - seedTotalPrices) + "' " + "WHERE userId='"
					+ userId + "';";
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
					+ " and seedState='" + 2 + "' and seedName='"
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
							+ rs3.getInt(3) + "seedIdStateTwo = "
							+ seedIdStateTwo + "seedNumberStateTwo = "
							+ seedNumberStateTwo);
				}
			} catch (SQLException e) {
				b = false;
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs3, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}

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
						+ 2
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
	private void resultBuySeedResult(String userId,
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
				+ " and seedState='2' ";
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		List<Seed> seeds = new ArrayList<Seed>();
		try {
			// 查询数据库,获取上述uid对应的数据

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
		for (int i = 0; i < 10000; i++) {
			Seed seed = new Seed();
			// 查询数据库,获取状态为 1 的所有种子
			String sql = "select * from farm_seed where seedState='1'";
			ResultSet rs = JDBCUtil.executeQuery(sql);
			try {
				while (rs.next()) {
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
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			seeds.add(seed);
		}
		seedMsgAllResultData.setSeeds(seeds);
		return seedMsgAllResultData;
	}

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

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
				+ code + "&grant_type=authorization_code";
		
		HttpUtil.requestData(url, new HttpCallBackListener() {

			@Override
			public void onFinish(String respose) {

				// 处理请求
				if (respose != null) {
					System.out.println("获取access_token  json = " + respose);
					analysisGetAndRefreshAccess_tokenSuccess(respose);
					System.out.println("access_token = " + access_token);

					// 查询数据库
					// 看看用户是否已经使用此微信账号登陆过，登录过则直接使用数据库数据，否则在请求微信服务器，获取新的用户数据
					boolean bb = false;
					String sql = "select * from farm_user where openid='"
							+ openid + "'";
					System.out.println("sql = " + sql);
					ResultSet rs = JDBCUtil.executeQuery(sql);

					try {
						// 查询数据库,获取上述uid对应的数据
						while (rs.next()) {
							System.out.println("遍历数据库查询到的数据");
							user.setUserId(rs.getLong(1));
							user.setUserNickName(rs.getString(2));
							user.setUserImage(rs.getString(3));
							user.setUserGold(rs.getLong(4));
							user.setUserExperience(rs.getLong(6));
							bb = true;

						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("wenti  01  ----");
						e.printStackTrace();
					} finally {
						JDBCUtil.close(rs, JDBCUtil.getPs(),
								JDBCUtil.getConnection());
					}

					if (bb) {
						System.out.println("数据库查询到用户，无需再次请求，直接得到用户 = " + user);
						
						

					} else {
						System.out.println("wenti   02  ----");
						String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
								+ access_token + "&openid=" + openid;
						HttpUtil.requestData(url, 
								new HttpCallBackListener() {
									@Override
									public void onFinish(String respose) {
										// 处理请求
										if (respose != null) {
											System.out
													.println("获取到用户信息json  = "
															+ respose);
											analysisGetUnionID(respose);
											System.out.println("nickname = "
													+ nickname);

											Long userGold = (long) 5000;
											Long userExperience = (long) 500;
											String sql3 = "INSERT INTO farm_user("
													+ "userNickName,userImage,userGold,openid,userExperience)"
													+ "VALUES('"
													+ nickname
													+ "','"
													+ headimgurl
													+ "',"
													+ userGold
													+ ",'"
													+ openid
													+ "',"
													+ userExperience
													+ ") ";
											System.out.println("sql3===="
													+ sql3);

											try {
												user.setUserId(JDBCUtil
														.executeUpdateGetId(sql3));
												user.setUserNickName(nickname);
												user.setUserImage(headimgurl);
												user.setUserGold(userGold);
												user.setOpenid(openid);
												user.setUserExperience(userExperience);
												System.out.println("刚刚插入数据的id ---  11 = "
														+ user.getUserId());

											} catch (Exception e) {
												e.printStackTrace();
											}
											System.out.println("网络请求到用户数据  = "
													+ user);
										} else {
											System.out
													.println("获取到用户信息的json为空  = "
															+ user);
										}
									}

									@Override
									public void onError(Exception e) {
										// 处理异常
										System.out
												.println(" 获取到用户信息的网络请求失败 ，给客户端提示 = "
														+ user);

									}
								});
					}

				} else {
					System.out.println("获取access_token 的json为空 = " + user);
				}
			}

			@Override
			public void onError(Exception e) {
				// 处理异常
				System.out.println("获取access_token 的网络请求失败,给客户端提示  = " + user);

			}
		});

		System.out.println("请求导数据，返回用户数据---- = " + user);

	}

	public User weixin(String code) {
		
		
		
		
		
		
		System.out.println("调用微信登录方法WeiXinlogin");

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
				+ code + "&grant_type=authorization_code";
		User user = new User();
		
		
		HttpUtil.resquestData(url, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				if (response.toString() != null) {
					
					 System.out.println("获取access_token  json = " + response.toString());
					 analysisGetAndRefreshAccess_tokenSuccess(response.toString());
					 System.out.println("access_token = " + access_token);
					
					 // 查询数据库
					 // 看看用户是否已经使用此微信账号登陆过，登录过则直接使用数据库数据，否则在请求微信服务器，获取新的用户数据
					 boolean bb = false;
					 String sql = "select * from farm_user where openid='"
					 + openid + "'";
					 System.out.println("sql = " + sql);
					 ResultSet rs = JDBCUtil.executeQuery(sql);
					
					 try {
					 // 查询数据库,获取上述uid对应的数据
					 while (rs.next()) {
					 System.out.println("遍历数据库查询到的数据");
					 user.setUserId(rs.getLong(1));
					 user.setUserNickName(rs.getString(2));
					 user.setUserImage(rs.getString(3));
					 user.setUserGold(rs.getLong(4));
					 user.setUserExperience(rs.getLong(6));
					 bb = true;
					
					 }
					 } catch (SQLException e) {
					 // TODO Auto-generated catch block
					 System.out.println("wenti  01  ----");
					 e.printStackTrace();
					 } finally {
					 JDBCUtil.close(rs, JDBCUtil.getPs(),
					 JDBCUtil.getConnection());
					 }
					
					 if (bb) {
					 System.out.println("数据库查询到用户，无需再次请求，直接得到用户 = " + user);
					
					 } else {
					 System.out.println("数据库未查询到用户，再次请求，直接得到用户 = " + user);
					
					 }
				}else {
					System.out.println("请求数据为空");
				}
				
				System.out.println("请求成功");
				
			}
			
			@Override
			public void onError(Exception e) {
				System.out.println("请求失败    == " + e);
				
			}
		});
					



		// // 处理请求
		// if (sb.toString() != null) {
		// System.out.println("获取access_token  json = " + sb.toString());
		// analysisGetAndRefreshAccess_tokenSuccess(sb.toString());
		// System.out.println("access_token = " + access_token);
		//
		// // 查询数据库
		// // 看看用户是否已经使用此微信账号登陆过，登录过则直接使用数据库数据，否则在请求微信服务器，获取新的用户数据
		// boolean bb = false;
		// String sql = "select * from farm_user where openid='"
		// + openid + "'";
		// System.out.println("sql = " + sql);
		// ResultSet rs = JDBCUtil.executeQuery(sql);
		//
		// try {
		// // 查询数据库,获取上述uid对应的数据
		// while (rs.next()) {
		// System.out.println("遍历数据库查询到的数据");
		// user.setUserId(rs.getLong(1));
		// user.setUserNickName(rs.getString(2));
		// user.setUserImage(rs.getString(3));
		// user.setUserGold(rs.getLong(4));
		// user.setUserExperience(rs.getLong(6));
		// bb = true;
		//
		// }
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// System.out.println("wenti  01  ----");
		// e.printStackTrace();
		// } finally {
		// JDBCUtil.close(rs, JDBCUtil.getPs(),
		// JDBCUtil.getConnection());
		// }
		//
		// if (bb) {
		// System.out.println("数据库查询到用户，无需再次请求，直接得到用户 = " + user);
		//
		// } else {
		// System.out.println("数据库未查询到用户，再次请求，直接得到用户 = " + user);
		//
		// }
		//
		// } else {
		// System.out.println("获取access_token 的json为空 = " + user);
		// }


		return user;
	}

	/**
	 * 
	 * @Description: 登录返回数据处理
	 * @param uid
	 * @return
	 */
	public LoginResultData loginResult(User user) {
		List<Land> lands = new ArrayList<Land>();
		// Set<Land> lands = new HashSet<Land>();
		for (int i = 0; i < 10000; i++) {
			Land land = new Land();
			String sql = "select * from farm_land where userId="
					+ user.getUserId();
			ResultSet rs = JDBCUtil.executeQuery(sql);
			try {
				// 查询数据库,获取上述uid对应的数据
				while (rs.next()) {
					land.setLandId(rs.getLong(1));
					land.setLandName(rs.getString(2));
					land.setLandState(rs.getString(3));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			lands.add(land);
		}
		LoginResultData loginResponseData = new LoginResultData();
		// user.setUserLands(lands);

		loginResponseData.setUser(user);
		loginResponseData.setLands(lands);
		String loginResultData = com.alibaba.fastjson.JSONObject
				.toJSONString(loginResponseData);
		System.out.println("loginResultData------------  = " + loginResultData);

		return loginResponseData;

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
		String sql = "select * from farm_seed where seedState='2' userId="
				+ userId;
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
}
