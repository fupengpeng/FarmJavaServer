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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.jiudianlianxian.bean.GetLandMsgResult;
import com.jiudianlianxian.bean.GetSeedMsgResult;
import com.jiudianlianxian.bean.HarvestResult;
import com.jiudianlianxian.bean.LoginResult;
import com.jiudianlianxian.bean.OpenWarehouseResult;
import com.jiudianlianxian.bean.PlantResult;
import com.jiudianlianxian.bean.SeedMsgAllResult;
import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.HarvestResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.OpenWarehouseResultData;
import com.jiudianlianxian.data.PlantResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.service.JDBCService;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;
import com.jiudiannnnnnn.UnionID;

public class CopyOfSocketServer extends ServerSocket {

	private static final int SERVER_PORT = 8944; // 服务端端口
	private static final String END_MARK = "quit"; // 断开连接标识
	private static final String VIEW_USER = "viewuser"; // 查看连接客户端列表

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<DisposeAffair> threadList = new ArrayList<DisposeAffair>(); // 服务器已启用线程集合
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // 存放消息的队列

	static CopyOfSocketServer server;

	private JDBCService jdbcService = new JDBCService();

	/**
	 * 入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			server = new CopyOfSocketServer(); // 启动服务端
			server.load();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CopyOfSocketServer() throws Exception {
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

		@Override
		public void run() {
			try {
				while (true) {

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

							String data = json.getString("data");
							System.out.println("info  == " + info
									+ "    data  = " + data);
							String jsonObject;
							if ("login".equals(info)) {
								// 如果是登录请求，则获取到登录的用户名和密码，进行数据库查询数据，是否用户名和密码争取，如果正确，则返回个用户登录成功的提示
								String code = json.getString("data");
								String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
										+ code
										+ "&grant_type=authorization_code";
								System.out.println("urlStr  = " + urlStr);

								UnionID unionID = new UnionID();
								unionID.setAccess_token("请求成功");
								unionID.setOpenid("oVVdbwvclc7NjI2xjhUxE-Gq_daU");
								LoginResult loginResult = new LoginResult();
								loginResult.setInfo(info);
								System.out
										.println("获取到了用户的openid     Access_token = "
												+ unionID.getAccess_token()
												+ "    getOpenid = "
												+ unionID.getOpenid());
								User user = new User();
								if (queryUser(user, unionID.getOpenid())) {
									System.out
											.println("数据库中查询到此openid用户的数据，返回给客户端");

									resultLoginData(loginResult, user);

								} else {
									System.out
											.println("数据库未查询到此openid用户的数据，进行网络请求，获取此openid用户的数据");

									// 没有数据，根据获取到的openid去微信服务器获取用户数据

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

							} else if ("buy".equals(info)) {
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

							} else if ("getlandmsg".equals(info)) {
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

							} else if ("plant".equals(info)) {
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

							} else if ("harvest".equals(info)) {
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
								// 查询果实所有信息
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
		private void resultLoginData(LoginResult loginResult, User user) {
			List<Land> lands = new ArrayList<Land>();
			List<Seed> landSeeds = new ArrayList<Seed>();
			String sql1 = "select * from farm_land where userId="
					+ user.getUserId();
			ResultSet rs1 = JDBCUtil.executeQuery(sql1);
			try {
				// 查询数据库,获取上述uid对应的数据
				while (rs1.next()) {
					Land land = new Land();
					land.setLandId(rs1.getLong(1));
					land.setLandName(rs1.getString(2));
					land.setLandState(rs1.getString(3));

					// 获取到土地的状态，根据状态是3，土地id查询land_seed表，获取seedid，查询farm_seed表，获取种子信息
					if (land.getLandState().equals("3")) {
						String sql2 = "select * from land_seed where landId="
								+ land.getLandId();
						ResultSet rs2 = JDBCUtil.executeQuery(sql2);
						try {
							// 查询数据库,获取上述uid对应的数据
							while (rs2.next()) {
								Seed seed = new Seed();
								land.setLandId(rs2.getLong(1));
								land.setLandName(rs2.getString(2));
								land.setLandState(rs2.getString(3));
								landSeeds.add(seed);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							JDBCUtil.close(rs2, JDBCUtil.getPs(),
									JDBCUtil.getConnection());
						}
					}

					lands.add(land);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}

			LoginResultData loginResponseData = new LoginResultData();
			// user.setUserLands(lands);

			loginResponseData.setUser(user);
			loginResponseData.setLands(lands);
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
