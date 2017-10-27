package com.jiudianlianxian.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.util.TextUtils;
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
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.data.SellFruitResultData;
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.LandData;
import com.jiudianlianxian.domain.RipeMessage;
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.service.JDBCService;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;
import com.jiudiannnnnnn.UnionID;

public class CopyOfCopyOfSocketServer extends ServerSocket {

	private static final int SERVER_PORT = 8944; // 服务端端口
	private static final String END_MARK = "quit"; // 断开连接标识
	private static final String VIEW_USER = "viewuser"; // 查看连接客户端列表
	Long seedState1 = 1L;    //种子状态----商店
	Long seedState2 = 2L;    //种子状态----背包
	Long seedState3 = 3L;    //种子状态----种植
	Long landState1 = 1L;    //土地状态----未开垦
	Long landState2 = 2L;    //土地状态----未种植，已开垦
	Long landState3 = 3L;    //土地状态----已种植，此时土地持有一个种子对象
	Long landState4 = 4L;    //土地状态----果实成熟

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<DisposeAffair> threadList = new ArrayList<DisposeAffair>(); // 服务器已启用线程集合
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // 存放消息的队列

	static CopyOfCopyOfSocketServer server;

	private JDBCService jdbcService = new JDBCService();

	/**
	 * 入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			Date date1 = new Date();
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					furitRipe();
//					System.out.println("----执行任务 ");
				}
			};
//			long timestamp = 60000; 
			long timestamp = 1000; 
			timer.schedule(timerTask, date1,timestamp);
			
			server = new CopyOfCopyOfSocketServer(); // 启动服务端
			server.load();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void furitRipe() {
		//1.查询farm_seed表，获取seedState为3的种子集合
		
		/**
		 * 1.查询farm_land,获取状态为3的种子id
		 * 2.遍历landId集合查询land_seed表，获取seedId
		 * 3.遍历seedId集合，查询farm_land表，获取种子的种植时间和生长时间
		 * 4.遍历获取到的种子信息，判断种子是否长成果实，
		 * 5.是的话，根据此种子id，查询land_seed表，获取landid
		 * 6.否的h话，直接返回空对象
		 * 7.改变获取到的土地id的
		 */
		Long landState4 = 4L;
		//1.查询farm_land,获取状态为3的种子id
		List<Long> landIds = new ArrayList<Long>();
		String sqlLandIds = "select * from farm_land where landState=3";
		ResultSet rsLandIds = JDBCUtil.executeQuery(sqlLandIds);
		try {
			while (rsLandIds.next()) {
				landIds.add(rsLandIds.getLong(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rsLandIds, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 2.遍历landId集合查询land_seed表，获取seedId
		List<Long> seedIds = new ArrayList<Long>();
		for (Long landId : landIds) {
			String sqlSeedIds = "select * from land_seed where landId=" + landId;
			ResultSet rsSeedIds = JDBCUtil.executeQuery(sqlSeedIds);
			try {
				while (rsSeedIds.next()) {
					seedIds.add(rsSeedIds.getLong(2));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rsSeedIds, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			
			//3.遍历seedId集合，查询farm_seed表，获取种子的种植时间和生长时间
			List<RipeMessage> ripeMessages = new ArrayList<RipeMessage>();
			for (Long seedId : seedIds) {
				String sql = "select * from farm_seed where seedId=" + seedId;
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
					JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
				}
			}
			
			
			
			//4.遍历获取到的种子信息，判断种子是否长成果实，
			for (RipeMessage ripeMessage : ripeMessages) {
				
				Long plantTime = ripeMessage.getSeedPlantTime();   //种植时间
				Long currentTime = new Date().getTime();           //当前时间
				Long growthTime = ripeMessage.getSeedGrowthTime();  //生长时间
				Long harvestTime = plantTime + growthTime;          //收获时间
				
				if (currentTime >= harvestTime) {
					//1.根据seedId获取landId   
					Long landIdd = null;
					String sql1 = "select * from land_seed where seedId=" + ripeMessage.getSeedId();
					ResultSet rs1 = JDBCUtil.executeQuery(sql1);
					try {
						while (rs1.next()) {
							landIdd = rs1.getLong(1);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
					}
					//2.根据landId改变土地状态
					String sql3 = "UPDATE farm_land SET " + "landState="
							+ landState4 + " WHERE landId=" + landIdd + ";";
					System.out.println("----sql-------------3 = " + sql3);
					try {
						JDBCUtil.executeUpdate(sql3);
					} catch (Exception e) {
						e.printStackTrace();
					}	
				}else {
//					System.out.println("-=--------------------------");
				}	
			}
			
			
			
		}
		

	}


	public CopyOfCopyOfSocketServer() throws Exception {
		super(SERVER_PORT);
	}

	/**
	 * 处理客户端发来的消息线程类
	 */
	class DisposeAffair implements Runnable {

		private Socket socket;

		private BufferedReader buff;

		private InputStream is;

		private Writer writer;

		private String userName; // 成员名称

		/**
		 * 构造函数<br>
		 * 处理客户端的消息，加入到在线成员列表中
		 * 
		 * @throws Exception
		 */
		public DisposeAffair(Socket socket) {
			this.socket = socket;
			this.userName = String.valueOf(socket.getPort());
			try {
				// this.buff = new BufferedReader(new InputStreamReader(
				// socket.getInputStream(), "UTF-8"));
				this.is = socket.getInputStream();
				this.writer = new OutputStreamWriter(socket.getOutputStream(),
						"UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			userList.add(this.userName);
			threadList.add(this);
			System.out.println("Form Cliect[port:" + socket.getPort() + "] "
					+ this.userName + "已进入房间可以开始游戏");
		}

		public void requestMsg() {
			String msg = null;
			String jsonData = null;

			try {
				byte[] byteBuff = new byte[1024 * 8];

				int len;
				while ((len = is.read(byteBuff)) != -1) {

					jsonData = new String(Arrays.copyOf(byteBuff, len));
					System.out.println("-----buff-----" + jsonData);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {

				boolean issocket = socket.isConnected();
				while (issocket) {
					
					final User user = new User();
					
					
					
					socket.sendUrgentData(0);
					String msg = null;
					String jsonData = null;

					try {
						byte[] byteBuff = new byte[1024 * 8];

						int len;
						while ((len = is.read(byteBuff)) != -1) {

							jsonData = new String(Arrays.copyOf(byteBuff, len));
							System.out.println("-----buff-----" + jsonData);

							msg = jsonData.toString();
							System.out.println("-------------" + msg);
							if (TextUtils.isEmpty(msg))
								continue;
							System.out.println("------" + msg);

							JSONObject json = new JSONObject(msg);
							String info = json.getString("info");

							System.out.println("info  == " + info);
							String jsonObject;
							
							Date date = new Date();
							System.out.println("请求时间：" + date.getHours()  + " 时   " + date.getMinutes() + " 分    " + date.getSeconds() + " 秒 ");
				
							
							if ("login".equals(info)) {
								// 如果是登录请求，则获取到登录的用户名和密码，进行数据库查询数据，是否用户名和密码争取，如果正确，则返回个用户登录成功的提示
								String code = json.getString("data");
								String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
										+ code
										+ "&grant_type=authorization_code";
								System.out.println("urlStr  = " + urlStr);

								UnionID unionID = new UnionID();
								unionID.setOpenid("zhangsan");
								final LoginResult loginResult = new LoginResult();
								loginResult.setInfo(info);
								System.out
										.println("获取到了用户的openid     Access_token = "
												+ unionID.getAccess_token()
												+ "    getOpenid = "
												+ unionID.getOpenid());
								
								if (queryUser(user, unionID.getOpenid())) {
									System.out
											.println("数据库中查询到此openid用户的数据，返回给客户端");
									// TODO 待完善，种子成熟信息的发送
									
									


									resultLoginData(loginResult, user);

								} else {
									System.out
											.println("数据库未查询到此openid用户的数据，进行网络请求，获取此openid用户的数据");

									// 没有数据，根据获取到的openid去微信服务器获取用户数据
									String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
											+ unionID.getAccess_token()
											+ "&openid=" + unionID.getOpenid();
									System.out
											.println("未从数据库获取到数据，网络请求进行数据获取----url  = "
													+ url);
									HttpUtil.requestData(url,
											new HttpCallBackListener() {
												@Override
												public void onFinish(
														String respose) {
													// 判断请求到的数据是否为空
													if (respose != null) {
														System.out
																.println("获取到用户信息json  = "
																		+ respose);
														// 不为空，进行解析，并将解析的数据保存到本地数据库中去
														analysisInsertData(
																user, respose);
														// 将获取到的数据返回给客户端
														resultLoginData(
																loginResult,
																user);
														System.out
																.println("网络请求到用户数据  = "
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

												// 解析微信首次登录获取到的用户数据，并插入到本地数据库
												private void analysisInsertData(
														User user,
														String respose) {
													try {
														JSONObject jsonObject2 = new JSONObject(
																respose);
														user.setOpenid(jsonObject2
																.getString("openid"));
														user.setUserNickName(jsonObject2
																.getString("nickname"));
														System.out
																.println(jsonObject2
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
														}
														user.setPrivilege(privilege);
														user.setUnionid(jsonObject2
																.getString("unionid"));

													} catch (JSONException e) {
														//
														e.printStackTrace();
													}
													System.out.println("nickname = "
															+ user.getUserNickName());

													Long userGold = (long) 5000;
													Long userExperience = (long) 500;
													String sql3 = "INSERT INTO farm_user("
															+ "userNickName,userImage,userGold,openid,userExperience)"
															+ "VALUES('"
															+ user.getUserNickName()
															+ "','"
															+ user.getUserImage()
															+ "',"
															+ userGold
															+ ",'"
															+ user.getOpenid()
															+ "',"
															+ userExperience
															+ ") ";
													System.out
															.println("sql3===="
																	+ sql3);

													try {
														user.setUserId(JDBCUtil
																.executeUpdateGetId(sql3));
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

							} else if ("shop".equals(info)) { // 获取所有种子信息----商店

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

							} else if ("buy".equals(info)) { // 购买种子
								// 根据请求参数，修改用户的种子数量和金币数量
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

							} else if ("getlandmsg".equals(info)) { // 获取土地信息
								// 查询土地所有信息
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

							} else if ("plant".equals(info)) { // 种植
								// 修改种子数量，种子状态，土地状态，种子生长的过程监控。及时给客户端发送消息
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

							} else if ("harvest".equals(info)) { // 收获
								// 修改土地状态，种子状态，生成果实修改果实数量
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

							} else if ("warehouse".equals(info)) { // 打开仓库 收获的果实
								// 查询果实所有信息
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

							} else if ("parcel".equals(info)) { // 包裹 已购买的种子
								// 查询背包种子所有信息
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

							} else if ("sell".equals(info)) { // 出售果实
								// 根据传入果实id查询果实剩余数量，判断查询到的数量是否大于传入的数量，大于的话，删除数据库果实，改变用户所得金币，返回给客户端金币，和出售果实信息
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
							}else if ("planttime".equals(info)) {
								Long userId = Long.valueOf(json
										.getString("userId"));
								Long landId = Long.valueOf(json
										.getString("landId"));
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
							
							
							Date date1 = new Date();
							Timer timer = new Timer();
							TimerTask timerTask = new TimerTask() {
								@Override
								public void run() {
									furitRipe(user);
//									System.out.println("执行任务 ");
								}
							};
//							long timestamp = 600000;  
							long timestamp = 1000; 
							timer.schedule(timerTask, date1,timestamp);
							

							if (VIEW_USER.equals(msg)) { // 查看已连接客户端
								sendMsg(onlineUsers());
							} else if (END_MARK.equals(msg)) { // 遇到退出标识时就结束让客户端退出
								sendMsg(END_MARK);
								break;
							} else {
								// pushMsg(String.format("%1$s说：%2$s", userName,
								// msg)); // 用于给房间内用户发送聊天信息
							}

						}

						System.out.println("xiayibu");
					} catch (IOException e) {
						System.out.println("yichang");
						issocket = false;
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally { // 关闭资源，房间移除所有客户端成员
				try {
					writer.close();
					buff.close();
					socket.close();
				} catch (Exception e) {

				}
				userList.remove(userName);
				threadList.remove(this);
				pushMsg("【" + userName + "断开连接】");
				System.out.println("Form Cliect[port:" + socket.getPort()
						+ "] " + userName + "断开连接");
			}
		}

		private void furitRipe(User user) {
			String jsonObject;
			FruitRipeResult fruitRipeResult = new FruitRipeResult();
			fruitRipeResult.setInfo("fruitRipe");
			FruitRipeResultData fruitRipeResultData = new FruitRipeResultData();
			Long landState4 = 4L;
			
			//1.查询farm_land,获取状态为3的种子id
			List<Long> landIds = new ArrayList<Long>();
			String sqlLandIds = "select * from farm_land where landState=3 and userId=" + user.getUserId();
			ResultSet rsLandIds = JDBCUtil.executeQuery(sqlLandIds);
			try {
				while (rsLandIds.next()) {
					landIds.add(rsLandIds.getLong(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rsLandIds, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			// 2.遍历landId集合查询land_seed表，获取seedId
			List<Long> seedIds = new ArrayList<Long>();
			for (Long landId : landIds) {
				String sqlSeedIds = "select * from land_seed where landId=" + landId;
				ResultSet rsSeedIds = JDBCUtil.executeQuery(sqlSeedIds);
				try {
					while (rsSeedIds.next()) {
						seedIds.add(rsSeedIds.getLong(2));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rsSeedIds, JDBCUtil.getPs(), JDBCUtil.getConnection());
				}
				
				//3.遍历seedId集合，查询farm_seed表，获取种子的种植时间和生长时间
				List<RipeMessage> ripeMessages = new ArrayList<RipeMessage>();
				for (Long seedId : seedIds) {
					String sql = "select * from farm_seed where seedId=" + seedId;
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
						JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
					}
				}
				
			for (int i = 0; i < ripeMessages.size(); i++) {
				RipeMessage ripeMessage = ripeMessages.get(i);
				Long plantTime = (ripeMessage.getSeedPlantTime())/1000;   //种植时间
				Long currentTime = (new Date().getTime())/1000;           //当前时间
				Long growthTime = (ripeMessage.getSeedGrowthTime())/1000;  //生长时间
				Long harvestTime = plantTime + growthTime;          //收获时间
				
//				if ((harvestTime + 300000) >= currentTime && currentTime >= harvestTime) {
				if ((harvestTime + 10) >= currentTime && currentTime >= harvestTime) {
//				if (currentTime == harvestTime) {
					//1.根据seedId获取landId
					Long landIdd = null;
					String sql1 = "select * from land_seed where seedId=" + ripeMessage.getSeedId();
					System.out.println("sql1 = " + sql1);
					ResultSet rs1 = JDBCUtil.executeQuery(sql1);
					try {
						while (rs1.next()) {
							landIdd = rs1.getLong(1);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
					}
					//2.根据landId改变土地状态
					String sql3 = "UPDATE farm_land SET " + "landState="
							+ landState4 + " WHERE landId=" + landIdd + ";";
					System.out.println("sql3 = " + sql3);
					try {
						JDBCUtil.executeUpdate(sql3);
					} catch (Exception e) {
						e.printStackTrace();
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
					
					
					
				}else {
					//  果实为成熟
					fruitRipeResult.setCode("果实未成熟");
					jsonObject = com.alibaba.fastjson.JSONObject
							.toJSONString(fruitRipeResult);
//					System.out.println("----jsonObject  ----= " + jsonObject);
//					pushMsg(jsonObject);
				}			
			}
			
			}
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

		/**
		 * 发送消息
		 * 
		 * @param msg
		 */
		private void sendMsg(String msg) {
			try {
				writer.write(msg);
				writer.write("\015\012");
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 聊天室在线成员列表
		 * 
		 * @return
		 */
		private String onlineUsers() {
			StringBuffer sbf = new StringBuffer();
			sbf.append("======== 已连接客户端列表(").append(userList.size())
					.append(") ========\015\012");
			for (int i = 0; i < userList.size(); i++) {
				sbf.append("[" + userList.get(i) + "]\015\012");
			}
			sbf.append("===============================");
			return sbf.toString();
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
				// TODO Auto-generated catch block
				System.out.println("wenti  01  ----");
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			return bb;
		}

		/**
		 * 
		 * @Description: 获取到user，并将数据返回给客户端
		 * @param loginResult
		 * @param user
		 */
		// 获取到user，并将数据返回给客户端
		private void resultLoginData(LoginResult loginResult, User user) {
			//1.根据userId查询用户土地信息
			List<Land> lands = new ArrayList<Land>();
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

			// 2.遍历lands，获取其land对象的landState，
			List<LandData> landDatas = new ArrayList<LandData>();
			for (Land land : lands) {
				
				LandData landData = new LandData();
				
				landData.setLandId(land.getLandId());
				landData.setLandName(land.getLandName());
				landData.setLandState(land.getLandState());
				
				if (land.getLandState() == 3 || land.getLandState() == 4 ) {
					// 3.状态是3即种植状态，查询land_seed表，获取seedId
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
					String sql2 = "select * from farm_seed where seedId="
							+ seedId;

					System.out.println("sql2 = " + sql2);
					ResultSet rs2 = JDBCUtil.executeQuery(sql2);
					try {
						while (rs2.next()) {

							landData.setSeedName(rs2.getString(2));
							System.out
									.println("用户土地信息  landData.getSeedName() = "
											+ landData.getSeedName());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						JDBCUtil.close(rs2, JDBCUtil.getPs(),
								JDBCUtil.getConnection());
					}

				}else {
					landData.setSeedName("");
				}
				landDatas.add(landData);
			}

			LoginResultData loginResponseData = new LoginResultData();
			loginResponseData.setUser(user);
			loginResponseData.setLandDatas(landDatas);
			String loginResultData = com.alibaba.fastjson.JSONObject
					.toJSONString(loginResponseData);
			System.out.println("loginResultData------------  = "
					+ loginResultData);

			loginResult.setCode("1");
			loginResult.setLoginResponseData(loginResponseData);
			String jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(loginResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}

		
	

	}

	/**
	 * 启动向客户端发送消息的线程，使用线程处理每个客户端发来的消息
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception {
		new Thread(new PushMsgTask()).start(); // 开启向客户端发送消息的线程

		while (true) {
			// server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
			Socket socket = this.accept();
			System.out.println("客户端连接开启线程");
			/**
			 * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
			 * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
			 * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
			 */
			// 每接收到一个Socket就建立一个新的线程来处理它
			new Thread(new DisposeAffair(socket)).start();
		}
	}

	/**
	 * 从消息队列中取消息，再发送给已连接的所有客户端成员
	 */
	class PushMsgTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				String msg = null;
				try {
					msg = msgQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (msg != null) {
					for (DisposeAffair thread : threadList) {
						thread.sendMsg(msg);
					}
				}
			}
		}

	}

}
