package com.jiudianlianxian.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.jiudianlianxian.bean.BuySeedResult;
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
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.HarvestResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.OpenWarehouseResultData;
import com.jiudianlianxian.data.PlantResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.data.SellFruitResultData;
import com.jiudianlianxian.data.UnionID;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.service.JDBCService;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;


/**
 * 
 * @Title: Test
 * @Description: 接口测试
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年11月2日 下午4:43:02
 *
 */
public class Test {
	
	public static void main(String[] args) {
		Test test = new Test();
		test.test01();
//		Logger  
//		Queue 
		
		
	}
	

    
	
	
	public void test01(){
		String aString = new String("hello worle  ");
		String bString = new String("hello world@jdlx");
		String cString = new String("hello world  ");
		String dString = new String("HELLO WORLD  ");
		System.out.println("获取字符串的长度    "  + cString.length());    //13
		System.out.println("字母'o'在字符串中初次出现的位置   " + cString.indexOf("a"));    //4
		
		String[] bStrings = bString.split("fff",2);  // 参数1是分割符号，即根据什么分割，也可以使用正则表达式，参数2指定分割成几个字符串
		System.out.println(bStrings[0]);
		for (String string : bStrings) {
		    System.out.println("分割后的字符串--限定个数=" + string);
		}
		
	}
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // 存放消息的队列
	String msg = "{info:login,data:001B0mui27xesF0Jawti2QNgui2B0mul}";
//	JSONObject json = new JSONObject(msg);
	JSONObject json = new JSONObject();
	String info = "info";
	
	final User user = new User();

	public void socketServer(){
		
		String jsonObject;
		final JDBCService jdbcService = new JDBCService();


		try {
			if ("login".equals(info)) {
				login(jdbcService);
			} else if ("shop".equals(info)) { // 获取所有种子信息----商店
				
				shop(jdbcService);

			} else if ("buy".equals(info)) { // 购买种子
				buy(jdbcService);

			} else if ("getlandmsg".equals(info)) { // 获取土地信息
				getlandmsg(jdbcService);

			} else if ("plant".equals(info)) { // 种植
				plant(jdbcService);

			} else if ("harvest".equals(info)) { // 收获
				harvest(jdbcService);

			} else if ("warehouse".equals(info)) { // 打开仓库 收获的果实
				warehouse(jdbcService);

			} else if ("parcel".equals(info)) { // 包裹 已购买的种子
				parcel(jdbcService);

			} else if ("sell".equals(info)) { // 出售果实
				sell(jdbcService);
			} else if ("planttime".equals(info)) {
				planttime(jdbcService);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}




	private void login(final JDBCService jdbcService) {
		// 如果是登录请求，则获取到登录的用户名和密码，进行数据库查询数据，是否用户名和密码争取，如果正确，则返回个用户登录成功的提示
		String code = null;
		try {
			code = json.getString("data");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
				+ code
				+ "&grant_type=authorization_code";
		HttpUtil.requestData(urlStr,
				new HttpCallBackListener() {
					@Override
					public void onFinish(String response) {
						if (response != null) {
							UnionID unionID = JSON
									.parseObject(
											response,
											UnionID.class);
							final LoginResult loginResult = new LoginResult();
							loginResult.setInfo(info);
							// 根据网络请求到的openid查询数据库，获取用户信息，土地信息

							if (queryUser(user,
									unionID.getOpenid())) {
								// 获取土地集合
								GetLandMsgResultData getLandMsgResultData = jdbcService
										.getLandMsg(user
												.getUserId());
								// 遍历土地集合，获取种植在土地上的种子名称
								LoginResultData loginResultData = jdbcService
										.getLoginResultData(getLandMsgResultData
												.getLands());
								// 设置数据，返回给客户端
								loginResultData
										.setUser(user);

								loginResult
										.setCode("1");
								loginResult
										.setLoginResponseData(loginResultData);
								String jsonObject = com.alibaba.fastjson.JSONObject
										.toJSONString(loginResult);
								Date date1 = new Date();
								System.out.println("请求时间：" + date1.getHours()
										+ " 时   " + date1.getMinutes() + " 分    "
										+ date1.getSeconds() + " 秒        "  + date1.getTime());
								System.out
										.println("jsonObject  = "
												+ jsonObject);
								pushMsg(jsonObject);

							} else {
								// 没有数据，根据获取到的openid去微信服务器获取用户数据
								String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
										+ unionID
												.getAccess_token()
										+ "&openid="
										+ unionID
												.getOpenid();
								HttpUtil.requestData(
										url,
										new HttpCallBackListener() {
											@Override
											public void onFinish(
													String respose) {
												// 判断请求到的数据是否为空
												if (respose != null) {
													// 不为空，进行解析，并将解析的数据保存到本地数据库中去
													analysisInsertData(
															user,
															respose);

													// 获取土地集合
													GetLandMsgResultData getLandMsgResultData = jdbcService
															.getLandMsg(user
																	.getUserId());
													// 遍历土地集合，获取种植在土地上的种子名称
													LoginResultData loginResultData = jdbcService
															.getLoginResultData(getLandMsgResultData
																	.getLands());
													// 设置数据，返回给客户端
													loginResultData
															.setUser(user);

													loginResult
															.setCode("1");
													loginResult
															.setLoginResponseData(loginResultData);
													String jsonObject = com.alibaba.fastjson.JSONObject
															.toJSONString(loginResult);
													System.out
															.println("jsonObject  = "
																	+ jsonObject);
													pushMsg(jsonObject);

												} else {
													System.out
															.println("获取到用户信息的json为空  = "
																	+ user);
												}
											}

											@Override
											public void onError(
													Exception e) {
												// 处理异常
												System.out
														.println(" 获取到用户信息的网络请求失败 ，给客户端提示 = "
																+ user);

											}

											// 解析微信首次登录获取到的用户数据，并插入到本地数据库
											@SuppressWarnings("null")
											private void analysisInsertData(
													User user,
													String respose) {
												try {
													JSONObject jsonObject2 = new JSONObject(
															respose);
													user.setUserGold(50000L);
													user.setUserExperience(5000L);
													user.setOpenid(jsonObject2
															.getString("openid"));
													user.setUserNickName(jsonObject2
															.getString("nickname"));
													user.setSex(jsonObject2
															.getInt("sex"));
													user.setProvince(jsonObject2
															.getString("province"));
													user.setCity(jsonObject2
															.getString("city"));
													user.setCountry(jsonObject2
															.getString("country"));
													user.setUserImage(jsonObject2
															.getString("headimgurl"));
													JSONArray jsonArray = jsonObject2
															.getJSONArray("privilege");
													String[] privilege = null;
													for (int i = 0; i < jsonArray
															.length(); i++) {
														privilege[i] = jsonArray
																.getString(i);
														user.setPrivilege(privilege);
													}
													user.setPrivilege(privilege);
													user.setUnionid(jsonObject2
															.getString("unionid"));

												} catch (JSONException e) {
													//
													e.printStackTrace();
												}

												String sql3 = "INSERT INTO farm_user("
														+ "userNickName,userImage,userGold,openid,userExperience)"
														+ "VALUES('"
														+ user.getUserNickName()
														+ "','"
														+ user.getUserImage()
														+ "',"
														+ user.getUserGold()
														+ ",'"
														+ user.getOpenid()
														+ "',"
														+ user.getUserExperience()
														+ ") ";

												try {
													user.setUserId(JDBCUtil
															.executeUpdateGetId(sql3));

												} catch (Exception e) {
													e.printStackTrace();
												}
												for (int i = 1; i <= 24; i++) {
													String sql4 = "INSERT INTO farm_land("
															+ "landName,landState,userId)"
															+ "VALUES('"
															+ i
															+ "','"
															+ 2
															+ "',"
															+ user.getUserId()
															+ ") ";
													try {
														JDBCUtil.executeUpdate(sql4);
													} catch (Exception e) {
														e.printStackTrace();
													}

												}

											}

										});
								// 解析数据

								// 插入数据

								// 返回数据

							}
						} else {
							System.out
									.println("response  请求数据为空");
						}
					}

					@Override
					public void onError(Exception e) {
						// TODO
						System.out
								.println("获取assess_token网络请求失败  ==  "
										+ e);
					}
				});
	}




	private void shop(final JDBCService jdbcService) {
		String jsonObject;
		//  shop".equals(info)) { // 获取所有种子信息----商店
		// 查询数据库种子列表所有数据
		SeedMsgAllResultData seedMsgAllResultData = jdbcService
				.getSeedMsgAll();
		SeedMsgAllResult seedMsgAllResult = new SeedMsgAllResult();
		seedMsgAllResult.setInfo(info);
		if (seedMsgAllResultData != null) {
			seedMsgAllResult.setCode("1");
			seedMsgAllResult
					.setSeedMsgAllResultData(seedMsgAllResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(seedMsgAllResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);

		} else {
			seedMsgAllResult.setCode("0");
			seedMsgAllResult
					.setSeedMsgAllResultData(seedMsgAllResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(seedMsgAllResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}




	private void buy(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// 根据请求参数，修改用户的种子数量和金币数量           buy      // 购买种子
		Long userId = Long.valueOf(json
				.getString("userId"));
		Long seedId = Long.valueOf(json
				.getString("seedId"));
		int seedNumber = Integer.valueOf(json
				.getString("seedNumber"));
		// 根据id查找用户信息，更改其种子数量和金币数量
		// 1.查询种子信息，计算价格
		// 2.查询用户金币信息，金币是否购买种子，如不够直接返回提示用户
		// 3.金币够，则修改用户的种子信息和金币信息
		// 4.将新的用户数据返回给用户
		BuySeedResultData buySeedResultData = jdbcService
				.getBuySeedResultData(userId, seedId,
						seedNumber);
		BuySeedResult buySeedResult = new BuySeedResult();
		buySeedResult.setInfo(info);
		if (buySeedResultData != null) {
			buySeedResult.setCode("1");
			buySeedResult
					.setBuySeedResultData(buySeedResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(buySeedResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			buySeedResult.setCode("0");
			buySeedResult
					.setBuySeedResultData(buySeedResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(buySeedResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}




	private void planttime(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		//  planttime   种植时间
		Long userId = Long.valueOf(json
				.getString("userId"));
		Long landId = Long.valueOf(json
				.getString("landId"));
		// 种子生长时间
		ResidueTimeResult residueTimeResult = new ResidueTimeResult();
		residueTimeResult.setInfo(info);
		Long residueTime = jdbcService.getResidueTime(
				userId, landId);
		if (residueTime >= 0) {
			residueTimeResult.setCode("1");
			residueTimeResult
					.setResidueTime(residueTime);
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




	private void sell(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// 根据传入果实id查询果实剩余数量，判断查询到的数量是否大于传入的数量，大于的话，删除数据库果实，改变用户所得金币，返回给客户端金币，和出售果实信息    
		// sell   // 出售果实
		
		Long userId = Long.valueOf(json
				.getString("userId"));
		Long fruitId = Long.valueOf(json
				.getString("fruitId"));
		Long fruitNumber = Long.valueOf(json
				.getString("fruitNumber"));
		SellFruitResultData sellFruitResultData = jdbcService
				.sellFruit(userId, fruitId, fruitNumber);

		SellFruitResult sellFruitResult = new SellFruitResult();
		sellFruitResult.setInfo(info);
		if (sellFruitResultData != null) {
			sellFruitResult.setCode("1");
			sellFruitResult
					.setSellFruitResultData(sellFruitResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(sellFruitResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			sellFruitResult.setCode("0");
			sellFruitResult
					.setSellFruitResultData(sellFruitResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(sellFruitResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}




	private void parcel(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// 查询背包种子所有信息         parcel        // 包裹 已购买的种子
		Long userId = Long.valueOf(json
				.getString("userId"));
		GetSeedMsgResultData getSeedMsgResultData = jdbcService
				.getSeedMsg(userId);
		GetSeedMsgResult getSeedMsgResult = new GetSeedMsgResult();
		getSeedMsgResult.setInfo(info);
		if (getSeedMsgResultData != null) {
			getSeedMsgResult.setCode("1");
			getSeedMsgResult
					.setGetSeedMsgResultData(getSeedMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getSeedMsgResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			getSeedMsgResult.setCode("0");
			getSeedMsgResult
					.setGetSeedMsgResultData(getSeedMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getSeedMsgResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}




	private void warehouse(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// 查询果实所有信息          warehouse 打开仓库 收获的果实
		Long userId = Long.valueOf(json
				.getString("userId"));
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
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			openWarehouseResult.setCode("0");
			openWarehouseResult
					.setOpenWarehouseResultData(openWarehouseResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(openWarehouseResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}




	private void harvest(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// 修改土地状态，种子状态，生成果实修改果实数量  harvest  收获
		Long userId = Long.valueOf(json
				.getString("userId"));
		Long landId = Long.valueOf(json
				.getString("landId"));

		HarvestResult harvestResult = new HarvestResult();
		HarvestResultData harvestResultData = jdbcService
				.harvest(landId, userId);

		harvestResult.setInfo(info);
		if (harvestResultData != null) {
			harvestResult.setCode("1");
			harvestResult
					.setHarvestResultData(harvestResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(harvestResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			harvestResult.setCode("0");
			harvestResult
					.setHarvestResultData(harvestResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(harvestResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}




	private void plant(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// 修改种子数量，种子状态，土地状态，种子生长的过程监控。及时给客户端发送消息         plant
		// userId seedId landId
		Long userId = Long.valueOf(json
				.getString("userId"));
		Long seedId = Long.valueOf(json
				.getString("seedId"));
		Long landId = Long.valueOf(json
				.getString("landId"));

		PlantResult plantResult = new PlantResult();
		PlantResultData plantResultData = jdbcService
				.plant(userId, seedId, landId);

		plantResult.setInfo(info);
		if (plantResultData != null) {
			plantResult.setCode("1");
			plantResultData.setSeedId(seedId);
			plantResult
					.setPlantResultData(plantResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(plantResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			plantResult.setCode("0");
			plantResult
					.setPlantResultData(plantResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(plantResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}




	private void getlandmsg(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// 查询土地所有信息    getlandmsg
		Long userId = Long.valueOf(json
				.getString("userId"));
		GetLandMsgResultData getLandMsgResultData = jdbcService
				.getLandMsg(userId);
		GetLandMsgResult getLandMsgResult = new GetLandMsgResult();
		getLandMsgResult.setInfo(info);
		if (getLandMsgResultData != null) {
			getLandMsgResult.setCode("1");
			getLandMsgResult
					.setGetLandMsgResultData(getLandMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getLandMsgResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		} else {
			getLandMsgResult.setCode("0");
			getLandMsgResult
					.setGetLandMsgResultData(getLandMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getLandMsgResult);
			System.out.println("jsonObject  = "
					+ jsonObject);
			pushMsg(jsonObject);
		}
	}
	
	
	
	
	/**
	 * 
	 * @Description: 获取access_token后进行数据库查询是否有openid的用户
	 * @param user
	 * @return
	 */
	private boolean queryUser(User user,String openid){
		// 查询数据库
		// 看看用户是否已经使用此微信账号登陆过，登录过则直接使用数据库数据，否则在请求微信服务器，获取新的用户数据
		boolean bb = false;
		String sql = "select * from farm_user where openid='" + openid
				+ "'";
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
	
	
	/**
	 * 准备发送的消息存入队列
	 * 
	 * @param msg
	 */
	private void pushMsg(String msg) {
		try {
			msgQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
