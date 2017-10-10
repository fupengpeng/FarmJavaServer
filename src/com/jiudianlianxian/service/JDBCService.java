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
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.util.SqlHelper;

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

	public void plant(Long userId, Long seedId, Long landId) {
		// 1.判断传入的种子是否还有
		// 1-1.根据seedId查询数据库，获取此id的种子数量
		int seedNumber = 0; // 传入seedId的种子数量
		String sql1 = "select * from farm_seed where userId=" + userId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = SqlHelper.executeQuery(sql1);
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs1.next()) {
				seedNumber = rs1.getInt(12);

				System.out.println("传入seedId的种子数量    seedNumber = "
						+ seedNumber);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		// 1-2.获取到此id种子数量，进行判断，验证是否还有此种子
		if (seedNumber > 0) { // 还有此种子，可以种植
			// 2.判断传入的土地状态
			// 2-1.根据landId查询数据库，获取此id的土地状态
			String landState = null; // 传入landId的土地状态
			String sql2 = "select * from farm_land where landId=" + landId;
			System.out.println("sql2 = " + sql2);
			ResultSet rs2 = SqlHelper.executeQuery(sql2);
			try {
				// 查询数据库,获取上述uid对应的数据

				while (rs2.next()) {
					landState = rs2.getString(3);

					System.out.println("传入landId的土地状态    landState = "
							+ landState);
				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs2, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			// 2-2.判断土地状态是否可以种植
			if ("2".equals(landState)) {
				// 土地状态为2，可种植
				// 3.改变土地状态
				// 4.改变传入seedId种子数量
				// 5.获取seedId的种子名称，根据seedId的种子名称、seedState、userId查询farm_seed表，是否有种植在土地上的该种子
				// 6.有则改变数量，无则添加该种子，并保存此种子的landId属性,注意，此时发现一个类型的种子可以隶属于多个土地，即种子持有一个土地集合
				// 5.监视种子是否长成果实，并实时给客户端发送种子生长情况
			} else {
				// 土地状态不是2，不可种植，返回提示信息

			}

		} else { // 没有此种子了，需要购买

		}

	}

	/**
	 * 
	 * @Description: 打开背包
	 * @return
	 */
	public List<Seed> openKnapsack() {
		Long userId = (long) 1;
		// 1.
		List<Seed> seeds = new ArrayList<Seed>();
		String sql1 = "select * from farm_seed where userId=" + userId
				+ " and seedState='" + 2 + "' ";
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = SqlHelper.executeQuery(sql1);
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs1.next()) {
				Seed seed = new Seed();
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

				seeds.add(seed);
				System.out.println("购买种子所需金币    lands = " + seeds);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
		}
		return seeds;
	}

	public GetSeedMsgResultData getSeedMsg(Long userId) {
		GetSeedMsgResultData getSeedMsgResultData = new GetSeedMsgResultData();
		// 1
		String sql = "select * from user_seed where userId=" + userId;
		List<Long> seedIds = new ArrayList<Long>();
		System.out.println("sql = " + sql);
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs.next()) {
				seedIds.add(rs.getLong(3));

				// System.out.println("购买种子所需金币    lands = " + seedIds.get(1));
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
		}
		System.out.println("seedIds = " + seedIds);
		List<Seed> seeds = new ArrayList<Seed>();
		for (Long long1 : seedIds) {
			// 1.
			String sql1 = "select * from farm_seed where seedId=" + long1;
			System.out.println("sql1 = " + sql1);
			ResultSet rs1 = SqlHelper.executeQuery(sql1);
			try {
				// 查询数据库,获取上述uid对应的数据

				while (rs1.next()) {
					Seed seed = new Seed();
					seed.setSeedId(long1);
					seed.setSeedName(rs1.getString(2));
					seed.setSeedState(rs1.getString(3));
					seed.setSeedGrowthTime(rs1.getLong(4));
					seed.setSeedSellingPrice(rs1.getLong(5));
					seed.setSeedImage(rs1.getString(6));
					seed.setSeedNumber(rs1.getInt(7));

					seeds.add(seed);
					System.out.println("购买种子所需金币    lands = " + seeds);
				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs1, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}

		}
		String seedname = "";
		for (Seed seed : seeds) { // 遍历查询到的用户种子列表种子
			if (seed.getSeedName().equals(seedname)
					&& seed.getSeedState().equals("3")) { // 判断用户种子列表中种到土地中的种子是否含有现在要种植的种子
				// 有 则改变数量
				// 修改用户的种子数量
				String sql2 = "UPDATE farm_seed SET " + "seedNumber='"
						+ (seed.getSeedNumber() + 1) + "' " + "WHERE seedId="
						+ seed.getSeedId() + ";";
				System.out
						.println("设置在当前种子的数量 = " + (seed.getSeedNumber() + 1));
				System.out.println("sql2 = " + sql2);
				try {
					SqlHelper.executeUpdate(sql2);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				// 无 则增加状态为3，种子名为传入的seedName的种子，数量为1
				// 给用户添加新种子
				String sql3 = "INSERT INTO farm_seed("
						+ "seedName,seedState,seedGrowthTime,seedSellingPrice,seedImage,"
						+ "seedNumber)" + "VALUES('" + seed.getSeedName()
						+ "','" + 3 + "'," + seed.getSeedGrowthTime() + ","
						+ seed.getSeedSellingPrice() + ",'"
						+ seed.getSeedImage() + "'," + 1 + ") ";
				System.out.println("sql3====" + sql3);
				String sql4 = "select last_insert_id()";
				System.out.println("sql11 = " + sql4);

				long lastid = 0;
				try {
					lastid = SqlHelper.executeUpdateGetId(sql3, sql4);
					System.out.println("刚刚插入数据的id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (seed.getSeedName().equals(seedname)
					&& seed.getSeedState().equals("2")) { // 判断用户种子状态是2，名称时传入的seedname的种子时改变其数量
				// 有 则改变数量
				// 修改用户的种子数量
				String sql2 = "UPDATE farm_seed SET " + "seedNumber='"
						+ (seed.getSeedNumber() - 1) + "' " + "WHERE seedId="
						+ seed.getSeedId() + ";";
				System.out
						.println("设置在当前种子的数量 = " + (seed.getSeedNumber() + 1));
				System.out.println("sql2 = " + sql2);
				try {
					SqlHelper.executeUpdate(sql2);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		getSeedMsgResultData.setSeeds(seeds);
		return getSeedMsgResultData;
	}

	/**
	 * 
	 * @Description: 根据用户id获取用户土地信息
	 * @param userId
	 * @return
	 */
	public GetLandMsgResultData getLandMsg(Long userId) {
		GetLandMsgResultData getLandMsgResultData = new GetLandMsgResultData();
		boolean b = true;
		// 1.查询种子信息，计算价格
		String sql = "select * from farm_land where userId=" + userId;
		List<Land> lands = new ArrayList<Land>();
		System.out.println("sql1 = " + sql);
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs.next()) {
				Land land = new Land();
				land.setLandId(rs.getLong(1));
				land.setLandName(rs.getString(2));
				land.setLandState(rs.getString(3));
				lands.add(land);
				System.out.println("购买种子所需金币    lands = " + lands);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
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

		// 1-1.查询种子单价，计算购买传入数量种子所需金币
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = SqlHelper.executeQuery(sql1);
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
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs1, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		// 2.查询用户金币信息，
		int userGold = 0; // 用户所有的金币数量
		String sql2 = "select * from farm_user where userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = SqlHelper.executeQuery(sql2);
		try {
			while (rs2.next()) {
				userGold = rs2.getInt(4);
				System.out.println("用户金币数    price2 = " + userGold);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs2, SqlHelper.getPs(), SqlHelper.getConnection());
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
				SqlHelper.executeUpdate(sql4);
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
			ResultSet rs3 = SqlHelper.executeQuery(sql3);
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
				//
				b = false;
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs3, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}

			if (b) { // 3-2-1.用户购买过名称为seedName的种子
				// 改变用户已有的该种子数量

				String sql5 = "UPDATE farm_seed SET " + "seedNumber="
						+ (seedNumberStateTwo + seedNumber) + " WHERE seedId="
						+ seedIdStateTwo + ";";
				System.out.println("sql5 = " + sql5);
				try {
					SqlHelper.executeUpdate(sql5);
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
				String sql11 = "select last_insert_id()";
				System.out.println("sql11 = " + sql11);
				long lastid = 0;
				try {
					lastid = SqlHelper.executeUpdateGetId(sql7, sql11);
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
		// 给用户返回购买成功的信息及数据
		// 2.查询用户金币信息，金币是否购买种子，如不够直接返回提示用户
		String sql12 = "select * from farm_user where userId=" + userId;
		System.out.println("sql12 = " + sql12);
		ResultSet rs12 = SqlHelper.executeQuery(sql12);
		long price12 = 0;
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs12.next()) {
				price12 = rs12.getLong(4);
				System.out.println("用户金币数    price12 = " + price12);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs12, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		String sql13 = "select * from user_seed where userId=" + userId;
		System.out.println("sql13 = " + sql13);
		ResultSet rs13 = SqlHelper.executeQuery(sql13);
		List<Long> seedIds = new ArrayList<Long>();
		try {
			// 查询数据库,获取上述uid对应的数据

			while (rs13.next()) {
				seedIds.add(rs13.getLong(3));
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs13, SqlHelper.getPs(), SqlHelper.getConnection());
		}
		List<Seed> seeds = new ArrayList<Seed>();
		for (Long seedid : seedIds) {
			String sql14 = "select * from farm_seed where seedId=" + seedid;
			System.out.println("sql14 = " + sql14);
			ResultSet rs14 = SqlHelper.executeQuery(sql14);
			Seed seed = new Seed();
			try {
				// 查询数据库,获取上述uid对应的数据

				while (rs14.next()) {
					seed.setSeedId(rs14.getLong(1));
					seed.setSeedName(rs14.getString(2));
					seed.setSeedState(rs14.getString(3));
					seed.setSeedGrowthTime(rs14.getLong(4));
					seed.setSeedSellingPrice(rs14.getLong(5));
					seed.setSeedImage(rs14.getString(6));
					seed.setSeedNumber(rs14.getInt(7));

				}
			} catch (SQLException e) {
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs14, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			seeds.add(seed);
		}
		buySeedResultData.setUserGold(price12);
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
			String sql = "select * from farm_seed where seedState='1'";

			// TODO SELECT DISTINCT * FROM farm_seed
			ResultSet rs = SqlHelper.executeQuery(sql);
			try {
				// 查询数据库,获取上述uid对应的数据

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
				//
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			seeds.add(seed);
		}
		seedMsgAllResultData.setSeeds(seeds);

		return seedMsgAllResultData;
	}

	class ReadByGet extends Thread {
		String uri = null;
		String json = null;

		public ReadByGet(String uri, String json) {
			this.uri = uri;
			this.json = json;
		}

		StringBuilder builder = null;

		@Override
		public void run() {
			try {
				URL url = new URL(uri);// 如果有参数，在网址中携带参数
				URLConnection conn = url.openConnection();
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				String line;
				builder = new StringBuilder();
				while ((line = br.readLine()) != null) {
					builder.append(line);
				}
				br.close();
				isr.close();
				is.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			json = builder.toString();
			System.out.println(builder.toString());
		}

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
//		String json = null;
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code="+code+"&grant_type=authorization_code",
//				json).start();
//		if (json != null) {
//			analysisGetAndRefreshAccess_tokenSuccess(json);
//		}
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN",
//				json).start();
//		if (json != null) {
//			analysisGetAndRefreshAccess_tokenSuccess(json);
//		}
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID",
//				json).start();
//		if (json != null) {
//			analysisDefeated(json);
//		}
//		new ReadByGet(
//				"https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid,
//				json).start();
//		if (json != null) {
//			analysisGetUnionID(json);
//		}
//		
//		// 无 则增加状态为3，种子名为传入的seedName的种子，数量为1
//		// 给用户添加新种子
//		String sql3 = "INSERT INTO farm_user("
//				+ "userNickName,userImage,userGold,userEcperience)" 
//				+ "VALUES('" 
//				+ nickname + "','" + headimgurl + "'," + 1000 + "," + 500 + ") ";
//		System.out.println("sql3====" + sql3);
//		String sql4 = "select last_insert_id()";
//		System.out.println("sql11 = " + sql4);
//
//		long lastid = 0;
//		try {
//			lastid = SqlHelper.executeUpdateGetId(sql3, sql4);
//			System.out.println("刚刚插入数据的id ---  11 = " + lastid);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return b;
	}

	/**
	 * 
	 * @Description: 登录判断
	 * @return
	 */
	public Long WeiXinlogin(String code) {
		
		String json = null;
		new ReadByGet(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code="+code+"&grant_type=authorization_code",
				json).start();
		if (json != null) {
			analysisGetAndRefreshAccess_tokenSuccess(json);
			System.out.println("access_token = " + access_token);
		}
		new ReadByGet(
				"https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN",
				json).start();
		if (json != null) {
			analysisGetAndRefreshAccess_tokenSuccess(json);
		}
		new ReadByGet(
				"https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID",
				json).start();
		if (json != null) {
			analysisDefeated(json);
		}
		new ReadByGet(
				"https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid,
				json).start();
		if (json != null) {
			analysisGetUnionID(json);
			System.out.println("nickname = " + nickname);
		}
		
		// 无 则增加状态为3，种子名为传入的seedName的种子，数量为1
		// 给用户添加新种子
		String sql3 = "INSERT INTO farm_user("
				+ "userNickName,userImage,userGold,userEcperience)" 
				+ "VALUES('" 
				+ nickname + "','" + headimgurl + "'," + 1000 + "," + 500 + ") ";
		System.out.println("sql3====" + sql3);
		String sql4 = "select last_insert_id()";
		System.out.println("sql11 = " + sql4);

		long lastid = 0;
		try {
			lastid = SqlHelper.executeUpdateGetId(sql3, sql4);
			System.out.println("刚刚插入数据的id ---  11 = " + lastid);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lastid;
	}

	/**
	 * 
	 * @Description: 登录返回数据处理
	 * @param uid
	 * @return
	 */
	public LoginResultData loginResult(Long userId) {

		User user = new User();
		String sql = "select * from farm_user where userId=" + userId;
		ResultSet rs = SqlHelper.executeQuery(sql);
		try {
			// 查询数据库,获取上述uid对应的数据
			while (rs.next()) {
				user.setUserId(rs.getLong(1));

				user.setUserNickName(rs.getString(2));
				user.setUserImage(rs.getString(3));
				user.setUserGold(rs.getLong(4));
				user.setUserExperience(rs.getLong(5));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
		}

		List<Land> lands = new ArrayList<Land>();
		for (int i = 0; i < 100; i++) {
			Land land = new Land();
			String sql2 = "select * from farm_land where userId="
					+ user.getUserId();
			ResultSet rs2 = SqlHelper.executeQuery(sql2);
			try {
				// 查询数据库,获取上述uid对应的数据
				while (rs2.next()) {
					land.setLandId(rs2.getLong(1));
					land.setLandName(rs2.getString(2));
					land.setLandState(rs2.getString(3));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				SqlHelper.close(rs, SqlHelper.getPs(),
						SqlHelper.getConnection());
			}
			lands.add(land);
		}
		LoginResultData loginResponseData = new LoginResultData();
		loginResponseData.setUser(user);
		loginResponseData.setLands(lands);

		return loginResponseData;

	}

	// /**
	// *
	// * Description: 添加用户
	// * @param user
	// * @return
	// */
	// public boolean addUser(User user){
	// boolean b = true;
	// String sql = "INSERT INTO user("
	// + "id,uid,username,sex,phonenumber,"
	// + "location,detailedaddress,postcode,birthday,wechat,"
	// + "growthvalue,account,password,integral,isdefaultaddress) "
	// + "VALUES("
	// +
	// user.getId()+","+user.getUid()+","+user.getUsername()+","+user.getSex()+","+user.getPhonenumber()+","
	// +
	// user.getLocation()+","+user.getDetailedaddress()+","+user.getPostcode()+","+user.getBirthday()+","+user.getWechat()+","
	// + user.getGrowthvalue()+","+
	// user.getAccount()+","+user.getPassword()+","+user.getIntegral()+","+user.getIsdefaultaddress()+") ;";
	// System.out.println("sql====" + sql);
	// try {
	// SqlHelper.executeUpdate(sql);
	// } catch (Exception e) {
	// b = false;
	// e.printStackTrace();
	// }
	// return b;
	// }
	//
	// /**
	// *
	// * Description: 修改用户
	// * @param user
	// * @return
	// */
	// public boolean updateUser(User user){
	// boolean b = true;
	// String sql = "UPDATE user SET "
	// + "username='"+user.getUsername()+"' , "
	// + "sex='"+user.getSex()+"' ,  "
	// + "phonenumber='"+user.getPhonenumber()+"' ,  "
	// + "location='"+user.getLocation()+"' ,  "
	// + "detailedaddress='"+user.getDetailedaddress()+"' ,  "
	// + "postcode='"+user.getPostcode()+"' ,  "
	// + "birthday='"+user.getBirthday()+"' ,  "
	// + "wechat='"+user.getWechat()+"' ,  "
	// + "growthvalue='"+user.getGrowthvalue()+"' ,  "
	// + "account='"+user.getAccount()+"' ,  "
	// + "password='"+user.getPassword()+"' ,  "
	// + "integral='"+user.getIntegral()+"' ,  "
	// + "isdefaultaddress='"+user.getIsdefaultaddress()+"' "
	// + "WHERE uid='"+user.getUid()+"';";
	// try {
	// SqlHelper.executeUpdate(sql);
	// } catch (Exception e) {
	// b = false;
	// e.printStackTrace();
	// }
	//
	//
	// return b;
	// }
	//
	// /**
	// *
	// * Description: 根据给定的uid查询数据库数据
	// * @param uid
	// * @return
	// */
	// public User getUserByUid (String uid){
	//
	// User user = new User();
	// String sql = "select * from user where uid='"+uid+"'";
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// try {
	// // 查询数据库,获取上述uid对应的数据
	// while (rs.next()) {
	//
	// user.setId(rs.getInt(1));
	// user.setUid(rs.getString(2));
	// user.setUsername(rs.getString(3));
	// user.setSex(rs.getString(4));
	// user.setPhonenumber(rs.getString(5));
	// user.setLocation(rs.getString(6));
	// user.setDetailedaddress(rs.getString(7));
	// user.setPostcode(rs.getString(8));
	// user.setBirthday(rs.getString(9));
	// user.setWechat(rs.getString(10));
	// user.setGrowthvalue(rs.getString(11));
	// user.setAccount(rs.getString(12));
	// user.setPassword(rs.getString(13));
	// user.setIntegral(rs.getString(14));
	// user.setIsdefaultaddress(rs.getString(15));
	//
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }finally{
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
	// }
	//
	// return user;
	//
	// }
	//
	// /**
	// *
	// * Description: 根据给定的uid刪除用戶
	// * @param uid
	// * @return
	// */
	// public boolean delUser(String uid){
	// boolean b = true;
	// String sql = "delete from user where uid = '"+uid+"'";
	//
	// try {
	// System.out.println("uid---"+uid);
	// SqlHelper.executeUpdate(sql);
	// } catch (Exception e) {
	// b = false;
	// e.printStackTrace();
	// }
	//
	// return b;
	// }
	// /**
	// *
	// * Description: 获取pageCount
	// * @param pageSize
	// * @return
	// */
	// public int getPageCount(int pageSize) {
	// int rowCount = 0;
	// ResultSet rs = null;
	// String sql = "SELECT * from user";
	//
	// try {
	// rs = SqlHelper.executeQuery(sql);
	//
	// // 查询数据库,计算数据共有多少页
	// while (rs.next()) {
	// rowCount = rs.getInt(1);
	// }
	//
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
	// }
	// return (rowCount - 1) / pageSize + 1;
	//
	// }
	//
	// /**
	// * @return
	// * Description: 按照分页来获取用户
	// * @param pageNow 当前页
	// * @param pageSize 当前页显示的数据数
	// * @return 用户数据对象集合
	// */
	// public ArrayList<User> getUsersByPage(int pageNow, int pageSize) {
	// ArrayList<User> al = new ArrayList<User>();
	//
	// String sql = "SELECT * from user WHERE id<=" + pageSize * pageNow
	// + " and id>=" + (pageSize * (pageNow - 1) + 1) + "; ";
	//
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// // 二次封装，将ResultSet---->User对象---->ArrayList集合
	// try {
	// while (rs.next()) {
	// User user = new User();
	// user.setUid(rs.getString(2));
	// user.setUsername(rs.getString(3));
	// user.setPhonenumber(rs.getString(5));
	// user.setLocation(rs.getString(6));
	// user.setDetailedaddress(rs.getString(7));
	// al.add(user);
	// }
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } finally {
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getConnection());
	// }
	//
	// return al;
	// }
	//
	// /**
	// * Description: 登录验证
	// * @param user
	// * @return 登录是否成功
	// */
	// public boolean checkUser(User user) {
	//
	// boolean b = false;
	// // //1.连接数据库
	// // Connection ct = null;
	// // PreparedStatement ps = null;
	// // ResultSet rs = null;
	// //
	// // try {
	// //
	// // Class.forName("com.mysql.jdbc.Driver");
	// // ct =
	// //
	// DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppingmall",
	// // "root", "root");
	// // //SELECT * FROM user WHERE account='15858585959' and
	// // password='100101'
	// // // ps =
	// //
	// ct.prepareStatement("SELECT * from user WHERE 	account='"+user.getAccount()+"' and password='"+user.getPassword()+"'");
	// // ps = ct.prepareStatement("SELECT * FROM user ");
	// //
	// // //给？赋值
	// // rs = ps.executeQuery();
	// //
	// // if(rs.next()){
	// // b = true;
	// // }
	// //
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }finally{
	// // if(rs != null){
	// // try {
	// // rs.close();
	// // } catch (SQLException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // rs = null;
	// // }
	// // if(ps != null){
	// // try {
	// // ps.close();
	// // } catch (SQLException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // ps = null;
	// // }
	// // if(ct != null){
	// // try {
	// // ct.close();
	// // } catch (SQLException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // ct = null;
	// // }
	// // }
	//
	// // 使用SqlHelper来实现数据库操作
	// String sql = "SELECT * FROM user";
	// ResultSet rs = SqlHelper.executeQuery(sql);
	// try {
	// if (rs.next()) {
	// System.out.println("userservi==" + rs.toString());
	// b = true;
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getCt());
	// }
	//
	// return b;
	// }
	//

}
