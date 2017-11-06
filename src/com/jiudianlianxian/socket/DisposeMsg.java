package com.jiudianlianxian.socket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
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
import com.jiudianlianxian.data.RipeMessage;
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
 * @Title: DisposeMsg
 * @Description: 观察消息队列变化
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年11月6日 下午4:08:44
 *
 */

public class DisposeMsg implements Observer {
	
	private static BlockingQueue<String> sendMsgQueue = new ArrayBlockingQueue<String>(
			20); // 存放消息的队列
	private JDBCService jdbcService = new JDBCService();
	
	public DisposeMsg ( BlockingQueue<String> sendMsgQueue){
		DisposeMsg.sendMsgQueue = sendMsgQueue;
	}
	
	
	// 观察者，一旦消息队列有变化，则从消息队列拿到变化的消息
	@SuppressWarnings("unchecked")
	public void update(Observable observable, Object obj) {
		this.disposeMsg((BlockingQueue<String>) obj);
	}

	// 处理变化的消息
	public void disposeMsg(BlockingQueue<String> requestMsgQueue ) {
		
		final User user = new User();
		Long userId = null;
		Long seedId = null;
		
		Long fruitId = null;
		Long fruitNumber = null;
		Long landId = null;
		String jsonObject = null;
		
		String msg = null;
		try {
			msg = requestMsgQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("msg ==== " + msg);
		// && (msg.indexOf("a")) != -1  测试是否是一条完整的信息
		if (msg != null && (msg.indexOf("@jdlx")) != -1 ) {
			String[] msgs = msg.split("@jdlx", 2); // 参数1是分割符号，即根据什么分割，也可以使用正则表达式，参数2指定分割成几个字符串
			System.out.println("msgs ----------- " + msgs[0]);
			try {
				JSONObject json = new JSONObject(msgs[0]);
				final String info = json.getString("info");
				switch (info) {
				case "login":
					// 如果是登录请求，则获取到登录的用户名和密码，进行数据库查询数据，是否用户名和密码争取，如果正确，则返回个用户登录成功的提示
					String code = json.getString("data");
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
//											Date date1 = new Date();
//											System.out.println("请求时间："
//													+ date1.getHours()
//													+ " 时   "
//													+ date1.getMinutes()
//													+ " 分    "
//													+ date1.getSeconds()
//													+ " 秒        "
//													+ date1.getTime());
											System.out
													.println("jsonObject  = "
															+ jsonObject);
											pushMsg(jsonObject+"@jdlx",sendMsgQueue);

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
																pushMsg(jsonObject+"@jdlx",sendMsgQueue);

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
									System.out
											.println("获取assess_token网络请求失败  ==  "
													+ e);
								}
							});

					break;
				case "shop":
					// 获取所有种子信息----商店
					// 查询数据库种子列表所有数据
					System.out.println("sssssssssssssssssssssssssssss");
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);

					} else {
						seedMsgAllResult.setCode("0");
						seedMsgAllResult
								.setSeedMsgAllResultData(seedMsgAllResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(seedMsgAllResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}
					break;
				case "buy":
					 // 购买种子
					// 根据请求参数，修改用户的种子数量和金币数量
					userId = Long.valueOf(json
							.getString("userId"));
					seedId  = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						buySeedResult.setCode("0");
						buySeedResult
								.setBuySeedResultData(buySeedResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(buySeedResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}
					break;
				case "getlandmsg":
					// 获取土地信息
					// 查询土地所有信息
					userId = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						getLandMsgResult.setCode("0");
						getLandMsgResult
								.setGetLandMsgResultData(getLandMsgResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(getLandMsgResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}
					break;
				case "plant":
					// 修改种子数量，种子状态，土地状态，种子生长的过程监控。及时给客户端发送消息
					// userId seedId landId
					userId = Long.valueOf(json
							.getString("userId"));
					seedId = Long.valueOf(json
							.getString("seedId"));
					landId = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						plantResult.setCode("0");
						plantResult
								.setPlantResultData(plantResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(plantResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}
					break;
				case "harvest":
					// 修改土地状态，种子状态，生成果实修改果实数量
					userId = Long.valueOf(json
							.getString("userId"));
					landId = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						harvestResult.setCode("0");
						harvestResult
								.setHarvestResultData(harvestResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(harvestResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}
					break;
				case "warehouse":    // 打开仓库 收获的果实
					// 查询果实所有信息
					userId = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						openWarehouseResult.setCode("0");
						openWarehouseResult
								.setOpenWarehouseResultData(openWarehouseResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(openWarehouseResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}

					break;
				case "parcel":    // 包裹 已购买的种子
					// 查询背包种子所有信息
					userId = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						getSeedMsgResult.setCode("0");
						getSeedMsgResult
								.setGetSeedMsgResultData(getSeedMsgResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(getSeedMsgResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}

				
					break;
				case "sell":   // 出售果实
					// 根据传入果实id查询果实剩余数量，判断查询到的数量是否大于传入的数量，大于的话，删除数据库果实，改变用户所得金币，返回给客户端金币，和出售果实信息
					userId = Long.valueOf(json
							.getString("userId"));
					fruitId = Long.valueOf(json
							.getString("fruitId"));
					fruitNumber = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						sellFruitResult.setCode("0");
						sellFruitResult
								.setSellFruitResultData(sellFruitResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(sellFruitResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}
				
					break;
				case "planttime":
					userId = Long.valueOf(json
							.getString("userId"));
					landId = Long.valueOf(json
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
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					} else {
						residueTimeResult.setCode("0");
						residueTimeResult
								.setResidueTime(residueTime);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(residueTimeResult);
						System.out.println("jsonObject  = "
								+ jsonObject);
						pushMsg(jsonObject+"@jdlx",sendMsgQueue);
					}
					break;

				default:
					break;
				}
				
				Date date1 = new Date();
				Timer timer = new Timer();
				TimerTask timerTask = new TimerTask() {
					@Override
					public void run() {
						furitRipe(user);
					}
				};
				// long timestamp = 600000;
				long timestamp = 1000;
				timer.schedule(timerTask, date1, timestamp);

				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * 
	 * @Description: 获取access_token后进行数据库查询是否有openid的用户
	 * @param user
	 * @return
	 */
	private boolean queryUser(User user, String openid) {
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
	private void pushMsg(String msg ,BlockingQueue<String> sendMsgQueue ) {
		try {
			sendMsgQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Description: 连接时查看最近是否有果实成熟
	 * @param user
	 */
	private void furitRipe(User user) {
		String jsonObject;
		FruitRipeResult fruitRipeResult = new FruitRipeResult();
		fruitRipeResult.setInfo("fruitRipe");
		FruitRipeResultData fruitRipeResultData = new FruitRipeResultData();
		Long landState4 = 4L;

		// 1.查询farm_land,获取状态为3的种子id
		List<Long> landIds = new ArrayList<Long>();
		String sqlLandIds = "select * from farm_land where landState=3 and userId="
				+ user.getUserId();
		ResultSet rsLandIds = JDBCUtil.executeQuery(sqlLandIds);
		try {
			while (rsLandIds.next()) {
				landIds.add(rsLandIds.getLong(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rsLandIds, JDBCUtil.getPs(),
					JDBCUtil.getConnection());
		}
		// 2.遍历landId集合查询land_seed表，获取seedId
		List<Long> seedIds = new ArrayList<Long>();
		for (Long landId : landIds) {
			String sqlSeedIds = "select * from land_seed where landId="
					+ landId;
			ResultSet rsSeedIds = JDBCUtil.executeQuery(sqlSeedIds);
			try {
				while (rsSeedIds.next()) {
					seedIds.add(rsSeedIds.getLong(2));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rsSeedIds, JDBCUtil.getPs(),
						JDBCUtil.getConnection());
			}

			// 3.遍历seedId集合，查询farm_seed表，获取种子的种植时间和生长时间
			List<RipeMessage> ripeMessages = new ArrayList<RipeMessage>();
			for (Long seedId : seedIds) {
				String sql = "select * from farm_seed where seedId="
						+ seedId;
				ResultSet rs = JDBCUtil.executeQuery(sql);
				try {
					while (rs.next()) {
						RipeMessage ripeMessage = new RipeMessage();
						ripeMessage.setSeedId(rs.getLong(1));
						ripeMessage.setSeedGrowthTime(rs.getLong(4));
						ripeMessage.setSeedPlantTime(rs.getLong(14));
						ripeMessages.add(ripeMessage);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
				}
			}

			for (int i = 0; i < ripeMessages.size(); i++) {
				RipeMessage ripeMessage = ripeMessages.get(i);
				Long plantTime = (ripeMessage.getSeedPlantTime()) / 1000; // 种植时间
				Long currentTime = (new Date().getTime()) / 1000; // 当前时间
				Long growthTime = (ripeMessage.getSeedGrowthTime()) / 1000; // 生长时间
				Long harvestTime = plantTime + growthTime; // 收获时间

				if ((harvestTime + 5) >= currentTime
						&& currentTime >= harvestTime) {
					Long landIdd = null;
					String sql1 = "select * from land_seed where seedId="
							+ ripeMessage.getSeedId();
					ResultSet rs1 = JDBCUtil.executeQuery(sql1);
					try {
						while (rs1.next()) {
							landIdd = rs1.getLong(1);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						JDBCUtil.close(rs1, JDBCUtil.getPs(),
								JDBCUtil.getConnection());
					}
					// 2.根据landId改变土地状态
					String sql3 = "UPDATE farm_land SET " + "landState="
							+ landState4 + " WHERE landId=" + landIdd + ";";
					try {
						JDBCUtil.executeUpdate(sql3);
					} catch (Exception e) {
						e.printStackTrace();
					}

					fruitRipeResultData.setLandId(landId);
					fruitRipeResultData.setLandState(4L);
					fruitRipeResultData.setUserId(ripeMessage.getUserId());
					fruitRipeResult
							.setFruitRipeResultData(fruitRipeResultData);
					fruitRipeResult.setCode("1");
					jsonObject = com.alibaba.fastjson.JSONObject
							.toJSONString(fruitRipeResult);
					System.out.println("----jsonObject  = " + jsonObject);
					pushMsg(jsonObject+"@jdlx",sendMsgQueue);

				} else {
					// 果实为成熟
					fruitRipeResult.setCode("果实未成熟");
					jsonObject = com.alibaba.fastjson.JSONObject
							.toJSONString(fruitRipeResult);
					// System.out.println("----jsonObject  ----= " +
					// jsonObject);
					// pushMsg(jsonObject+"@jdlx");
				}
			}

		}
	}

	
}
