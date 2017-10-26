package com.jiudiannnnnnn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Test;

import com.jiudianlianxian.bean.BuySeedResult;
import com.jiudianlianxian.bean.FruitRipeResult;
import com.jiudianlianxian.bean.GetLandMsgResult;
import com.jiudianlianxian.bean.GetSeedMsgResult;
import com.jiudianlianxian.bean.HarvestResult;
import com.jiudianlianxian.bean.LoginResult;
import com.jiudianlianxian.bean.OpenWarehouseResult;
import com.jiudianlianxian.bean.PlantResult;
import com.jiudianlianxian.bean.ResidueTimeResult;
import com.jiudianlianxian.bean.SeedMsgAllResult;
import com.jiudianlianxian.bean.SellFruitResult;
import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.FruitRipeResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.HarvestResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.OpenWarehouseResultData;
import com.jiudianlianxian.data.PlantResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.data.SellFruitResultData;
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.LandData;
import com.jiudianlianxian.domain.RipeMessage;
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.service.JDBCService;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudianlianxian.util.SqlHelper;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;

public class SqlTest {
	JDBCService jdbcService = new JDBCService();
	String info = "------";
	String jsonObject = "";

	JSONObject jsonObject1;
	String access_token = null;
	int expires_in = 0;
	String refresh_token = null;
	String openid = "oVVdbwvclc7NjI2xjhUxE-Gq_daU";
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

	public static void main(String[] args) {
		SqlTest sqlTest = new SqlTest();
		// sqlTest.buy();
		// sqlTest.plant();
		// sqlTest.login();
		// sqlTest.harvest();
		// sqlTest.warehouse();
		// sqlTest.parcel();
		// sqlTest.sell();
//		sqlTest.test14();
		sqlTest.planttime();

	}

	// 计时给客户短发送果实成熟的消息
	public void timing() {
		// 判断用户是否连接，
		// 根据用户id和土地状态查询数据库，获取种子成熟信息
		// 遍历判断种子是否成熟，成熟则发送，未成熟则不用管
	}
	
	public void planttime(){
		Long userId = 1L;
		Long landId = 2L;
		System.out.println("---------------" + new Date().getTime());
		//种子生长时间
		ResidueTimeResult residueTimeResult = new ResidueTimeResult();
		residueTimeResult.setInfo(info);
		Long residueTime = jdbcService.getResidueTime(userId,landId);
		if (residueTime >= 0) {
			residueTimeResult.setCode("1");
			residueTimeResult.setResidueTime(residueTime);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(residueTimeResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			residueTimeResult.setCode("0");
			residueTimeResult
					.setResidueTime(residueTime);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(residueTimeResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
		
	}
	private void furitRipe(User user) {
		String jsonObject;
		// 1.查询farm_seed表，获取seedState为3的种子集合

		// 2.遍历种子集合得到种子信息
		List<RipeMessage> ripeMessages = new ArrayList<RipeMessage>();
		String sql = "select * from farm_seed where seedState=3 and userId="
				+ user.getUserId();
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		try {
			while (rs.next()) {
				RipeMessage ripeMessage = new RipeMessage();
				ripeMessage.setSeedId(rs.getLong(1));
				ripeMessage.setSeedGrowthTime(rs.getLong(4));
				ripeMessage.setUserId(rs.getLong(13));
				ripeMessage.setSeedPlantTime(rs.getLong(14));
				ripeMessages.add(ripeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		System.out.println("seeds = " + ripeMessages.toString());
		for (RipeMessage ripeMessage : ripeMessages) {
			Long plantTime = ripeMessage.getSeedPlantTime();
			Long currentTime = new Date().getTime();
			Long growthTime = ripeMessage.getSeedGrowthTime();
			FruitRipeResult fruitRipeResult = new FruitRipeResult();
			fruitRipeResult.setInfo("fruitRipe");
			FruitRipeResultData fruitRipeResultData = new FruitRipeResultData();
			System.out.println("plantTime + growthTime - 3600000 = "
					+ (plantTime + growthTime - 3600000) + "  "
					+ new Date((plantTime + growthTime - 3600000)));

			System.out.println("currentTime = " + currentTime + "  "
					+ new Date(currentTime));
			if ((plantTime + growthTime - 3600000) >= currentTime
					&& currentTime >= (plantTime + growthTime + 3600000)) {
				Long landId = null;
				String sql1 = "select * from land_seed where seedId="
						+ ripeMessage.getSeedId();
				System.out.println("sql1 = " + sql1);
				ResultSet rs1 = JDBCUtil.executeQuery(sql);
				try {
					while (rs1.next()) {
						landId = rs1.getLong(1);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs1, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
				}

				fruitRipeResultData.setLandId(landId);
				fruitRipeResultData.setLandState(4L);
				fruitRipeResultData.setUserId(ripeMessage.getUserId());
				fruitRipeResult.setFruitRipeResultData(fruitRipeResultData);
				fruitRipeResult.setCode("1");
				jsonObject = com.alibaba.fastjson.JSONObject
						.toJSONString(fruitRipeResult);
				System.out.println("----jsonObject  = " + jsonObject);
				pushMsg(jsonObject);

			} else {
				// 果实为成熟
				fruitRipeResult.setCode("果实未成熟");
				jsonObject = com.alibaba.fastjson.JSONObject
						.toJSONString(fruitRipeResult);
				System.out.println("----jsonObject  = " + jsonObject);
				pushMsg(jsonObject);
			}
		}
	}

	public void sell() {
		// 根据传入果实id查询果实剩余数量，判断查询到的数量是否大于传入的数量，大于的话，删除数据库果实，改变用户所得金币，返回给客户端金币，和出售果实信息
		Long userId = (long) 1;
		Long fruitId = (long) 1;
		Long fruitNumber = (long) 5;
		SellFruitResultData sellFruitResultData = jdbcService.sellFruit(userId,
				fruitId, fruitNumber);
		Date date = new Date();
		System.out.println("请求时间：" + date.getHours() + " 时   "
				+ date.getMinutes() + " 分    " + date.getSeconds() + " 秒 ");
		SellFruitResult sellFruitResult = new SellFruitResult();
		sellFruitResult.setInfo(info);
		if (sellFruitResultData != null) {
			sellFruitResult.setCode("1");
			sellFruitResult.setSellFruitResultData(sellFruitResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(sellFruitResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			sellFruitResult.setCode("0");
			sellFruitResult.setSellFruitResultData(sellFruitResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(sellFruitResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void parcel() {

		// 查询背包种子所有信息
		Long userId = (long) 1;
		GetSeedMsgResultData getSeedMsgResultData = jdbcService
				.getSeedMsg(userId);
		GetSeedMsgResult getSeedMsgResult = new GetSeedMsgResult();
		getSeedMsgResult.setInfo(info);
		if (getSeedMsgResultData != null) {
			getSeedMsgResult.setCode("1");
			getSeedMsgResult.setGetSeedMsgResultData(getSeedMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getSeedMsgResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			getSeedMsgResult.setCode("0");
			getSeedMsgResult.setGetSeedMsgResultData(getSeedMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getSeedMsgResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}

	}

	public void warehouse() {

		// 查询果实所有信息
		Long userId = (long) 1;
		OpenWarehouseResultData openWarehouseResultData = jdbcService
				.openWarehouse(userId);
		OpenWarehouseResult openWarehouseResult = new OpenWarehouseResult();
		openWarehouseResult.setInfo(info);
		if (openWarehouseResultData != null) {
			openWarehouseResult.setCode("1");
			openWarehouseResult
					.setOpenWarehouseResultData(openWarehouseResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(openWarehouseResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			openWarehouseResult.setCode("0");
			openWarehouseResult
					.setOpenWarehouseResultData(openWarehouseResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(openWarehouseResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void harvest() {
		// 修改土地状态，种子状态，生成果实修改果实数量
		Long userId = (long) 1;
		Long landId = (long) 1;

		HarvestResult harvestResult = new HarvestResult();
		HarvestResultData harvestResultData = jdbcService.harvest(landId,
				userId);

		harvestResult.setInfo(info);
		if (harvestResultData != null) {
			harvestResult.setCode("1");
			harvestResult.setHarvestResultData(harvestResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(harvestResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			harvestResult.setCode("0");
			harvestResult.setHarvestResultData(harvestResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(harvestResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	private boolean queryUser(User user, String openid) {
		// 查询数据库
		// 看看用户是否已经使用此微信账号登陆过，登录过则直接使用数据库数据，否则在请求微信服务器，获取新的用户数据
		boolean bb = false;
		String sql = "select * from farm_user where openid='" + openid + "'";
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);

		try {
			// 查询数据库,获取上述uid对应的数据
			while (rs.next()) {
				System.out.println("遍历数据库查询到数据");
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
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		return bb;
	}

	public void plant() {

		// 修改种子数量，种子状态，土地状态，种子生长的过程监控。及时给客户端发送消息
		// userId seedId landId
		Long userId = (long) 1;
		Long seedId = (long) 104;
		Long landId = (long) 3;

		PlantResult plantResult = new PlantResult();
		PlantResultData plantResultData = jdbcService.plant(userId, seedId,
				landId);
		plantResult.setInfo(info);
		if (plantResultData != null) {
			plantResult.setCode("1");
			plantResult.setPlantResultData(plantResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(plantResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			plantResult.setCode("0");
			plantResult.setPlantResultData(plantResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(plantResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void getlandmsg() {
		// 查询土地所有信息
		Long userId = (long) 1;
		GetLandMsgResultData getLandMsgResultData = jdbcService
				.getLandMsg(userId);
		GetLandMsgResult getLandMsgResult = new GetLandMsgResult();
		getLandMsgResult.setInfo(info);
		if (getLandMsgResultData != null) {
			getLandMsgResult.setCode("1");
			getLandMsgResult.setGetLandMsgResultData(getLandMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getLandMsgResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			getLandMsgResult.setCode("0");
			getLandMsgResult.setGetLandMsgResultData(getLandMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getLandMsgResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}

	}

	public void buy() {
		// 根据请求参数，修改用户的种子数量和金币数量
		Long userId = (long) 2;
		Long seedId = (long) 50;
		int seedNumber = 2;
		// 根据id查找用户信息，更改其种子数量和金币数量
		// 1.查询种子信息，计算价格
		// 2.查询用户金币信息，金币是否购买种子，如不够直接返回提示用户
		// 3.金币够，则修改用户的种子信息和金币信息
		// 4.将新的用户数据返回给用户
		BuySeedResultData buySeedResultData = jdbcService.getBuySeedResultData(
				userId, seedId, seedNumber);
		BuySeedResult buySeedResult = new BuySeedResult();
		buySeedResult.setInfo(info);

		if (buySeedResultData != null) {
			buySeedResult.setCode("1");
			buySeedResult.setBuySeedResultData(buySeedResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(buySeedResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			buySeedResult.setCode("0");
			buySeedResult.setBuySeedResultData(buySeedResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(buySeedResult);
			System.out.println("jsonObject--  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void test17() {
		// 查询数据库种子列表所有数据
		SeedMsgAllResultData seedMsgAllResultData = jdbcService.getSeedMsgAll();
		SeedMsgAllResult seedMsgAllResult = new SeedMsgAllResult();
		seedMsgAllResult.setInfo(info);
		if (seedMsgAllResultData != null) {
			seedMsgAllResult.setCode("1");
			seedMsgAllResult.setSeedMsgAllResultData(seedMsgAllResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(seedMsgAllResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);

		} else {
			seedMsgAllResult.setCode("0");
			seedMsgAllResult.setSeedMsgAllResultData(seedMsgAllResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(seedMsgAllResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void pushMsg(String str) {
		System.out.println("str  -----------------------==  " + str);
	}

	public void login() {

		// 如果是登录请求，则获取到登录的用户名和密码，进行数据库查询数据，是否用户名和密码争取，如果正确，则返回个用户登录成功的提示
		String code = "data";
		String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
				+ code + "&grant_type=authorization_code";
		System.out.println("urlStr  = " + urlStr);

		UnionID unionID = new UnionID();
		unionID.setOpenid("zhangsan");
		LoginResult loginResult = new LoginResult();
		loginResult.setInfo(info);
		System.out.println("获取到了用户的openid     Access_token = "
				+ unionID.getAccess_token() + "    getOpenid = "
				+ unionID.getOpenid());
		User user = new User();
		if (queryUser(user, unionID.getOpenid())) {
			System.out.println("数据库中查询到此openid用户的数据，返回给客户端");

			resultLoginData(loginResult, user);

		} else {
			System.out.println("数据库未查询到此openid用户的数据，进行网络请求，获取此openid用户的数据");

			// 没有数据，根据获取到的openid去微信服务器获取用户数据
			String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
					+ unionID.getAccess_token() + "&openid="
					+ unionID.getOpenid();
			System.out.println("未从数据库获取到数据，网络请求进行数据获取----url  = " + url);
			HttpUtil.requestData(url, new HttpCallBackListener() {
				@Override
				public void onFinish(String respose) {
					// 判断请求到的数据是否为空
					if (respose != null) {
						System.out.println("获取到用户信息json  = " + respose);
						// 不为空，进行解析，并将解析的数据保存到本地数据库中去
						analysisInsertData(user, respose);
						// 将获取到的数据返回给客户端
						resultLoginData(loginResult, user);
						System.out.println("网络请求到用户数据  = " + user);
					} else {
						System.out.println("获取到用户信息的json为空  = " + user);
					}
				}

				@Override
				public void onError(Exception e) {
					// 处理异常
					System.out.println(" 获取到用户信息的网络请求失败 ，给客户端提示 = " + user);

				}

				// 解析微信首次登录获取到的用户数据，并插入到本地数据库
				private void analysisInsertData(User user, String respose) {
					try {
						JSONObject jsonObject2 = new JSONObject(respose);
						user.setOpenid(jsonObject2.getString("openid"));
						user.setUserNickName(jsonObject2.getString("nickname"));
						System.out.println(jsonObject2.getString("nickname"));
						user.setSex(jsonObject2.getInt("sex"));
						user.setProvince(jsonObject2.getString("province"));
						user.setCity(jsonObject2.getString("city"));
						user.setCountry(jsonObject2.getString("country"));
						user.setUserImage(jsonObject2.getString("headimgurl"));
						JSONArray jsonArray = jsonObject2
								.getJSONArray("privilege");
						String[] privilege = null;
						for (int i = 0; i < jsonArray.length(); i++) {
							privilege[i] = jsonArray.getString(i);
						}
						user.setPrivilege(privilege);
						user.setUnionid(jsonObject2.getString("unionid"));

					} catch (JSONException e) {
						//
						e.printStackTrace();
					}
					System.out.println("nickname = " + user.getUserNickName());

					Long userGold = (long) 5000;
					Long userExperience = (long) 500;
					String sql3 = "INSERT INTO farm_user("
							+ "userNickName,userImage,userGold,openid,userExperience)"
							+ "VALUES('" + user.getUserNickName() + "','"
							+ user.getUserImage() + "'," + userGold + ",'"
							+ user.getOpenid() + "'," + userExperience + ") ";
					System.out.println("sql3====" + sql3);

					try {
						user.setUserId(JDBCUtil.executeUpdateGetId(sql3));
						user.setUserGold(userGold);
						user.setUserExperience(userExperience);
						System.out.println("刚刚插入数据的id ---  11 = "
								+ user.getUserId());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});

		}

	}

	private void resultLoginData(LoginResult loginResult, User user) {
		List<Land> lands = new ArrayList<Land>();
		// for (int i = 0; i < 10000; i++) {

		String sql1 = "select * from farm_land where userId="
				+ user.getUserId();
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			// 查询数据库,获取上述uid对应的数据
			while (rs1.next()) {
				Land land = new Land();
				land.setLandId(rs1.getLong(1));
				land.setLandName(rs1.getLong(2));
				land.setLandState(rs1.getLong(3));
				lands.add(land);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// }
		// 遍历lands，获取其land对象的landState，
		List<LandData> landDatas = new ArrayList<LandData>();
		for (Land land : lands) {
			LandData landData = new LandData();
			landData.setLandId(land.getLandId());
			landData.setLandName(land.getLandName());
			landData.setLandState(land.getLandState());
			if (land.getLandState().equals("3")) {
				// 状态是3，查询land_seed表，获取seedId
				Long seedId = null;
				String sql = "select * from land_seed where landId="
						+ land.getLandId();

				System.out.println("sql = " + sql);
				ResultSet rs = JDBCUtil.executeQuery(sql);
				try {
					while (rs.next()) {
						seedId = rs.getLong(2);
						System.out.println("用户土地信息    seedId = " + seedId);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
				}
				// 根据获取到的seedId，查询farm_seed表，获取其seedName
				String sql2 = "select * from farm_seed where seedId=" + seedId;

				System.out.println("sql2 = " + sql2);
				ResultSet rs2 = JDBCUtil.executeQuery(sql2);
				try {
					while (rs2.next()) {

						landData.setSeedName(rs2.getString(2));
						System.out.println("用户土地信息  landData.getSeedName() = "
								+ landData.getSeedName());
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

		LoginResultData loginResponseData = new LoginResultData();
		loginResponseData.setUser(user);
		loginResponseData.setLandDatas(landDatas);
		String loginResultData = com.alibaba.fastjson.JSONObject
				.toJSONString(loginResponseData);
		System.out.println("loginResultData------------  = " + loginResultData);

		loginResult.setCode("1");
		loginResult.setLoginResponseData(loginResponseData);
		String jsonObject = com.alibaba.fastjson.JSONObject
				.toJSONString(loginResult);
		System.out.println("jsonObject  = " + jsonObject);
		pushMsg(jsonObject);
	}

	public void test13() {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		long between = 0;
		try {
			java.util.Date begin = dfs.parse("2009-07-10 10:22:21.214");
			java.util.Date end = dfs.parse("2009-07-20 11:24:49.145");
			between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		long day = between / (24 * 60 * 60 * 1000);
		long hour = (between / (60 * 60 * 1000) - day * 24);
		long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
				- min * 60 * 1000 - s * 1000);
		System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒" + ms
				+ "毫秒");
	}

	public void test14() {
		User user = new User();
		user.setUserId(7L);
		Date date1 = new Date();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				furitRipe(user);
				System.out.println("执行任务 ");

			}
		};
		long timestamp = 5000;
		timer.schedule(timerTask, date1, timestamp);
	}

	public void test10() {
		Date date = new Date();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// System.out.println("执行任务 ");

			}
		};
		Long a = date.getTime() + 10000L;
		Date date1 = new Date(a);
		System.out.println("当前时间  = " + date.getTime() + "   当前date =  " + date
				+ "    增加后 = " + date1.getTime() + "   增加后date1 = " + date1);

		long timestamp = 1000;
		timer.schedule(timerTask, date, timestamp);

	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// // 设置通用的请求属性
			// connection.setRequestProperty("accept", "*/*");
			// connection.setRequestProperty("connection", "Keep-Alive");
			// connection.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			// connection.connect();
			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// // 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;

			}

		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public void test09() {
		String uri = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
		StringBuilder builder = null;
		String json_access_token = null;
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
		json_access_token = builder.toString();
		System.out.println(builder.toString());

		JSONObject jsonObject1;
		JSONObject data = null;
		try {
			jsonObject1 = new JSONObject(json_access_token);
			info = jsonObject1.getString("info");
			data = jsonObject1.getJSONObject("data");
			System.out.println("data  = " + data.toString());
			System.out.println("username = " + data.getString("username"));
			System.out.println("sex = " + data.getString("sex"));
			System.out.println("age = " + data.getString("age"));

		} catch (JSONException e) {
			//
			e.printStackTrace();
		}

	}

}
