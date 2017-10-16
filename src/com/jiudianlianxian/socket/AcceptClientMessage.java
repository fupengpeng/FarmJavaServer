package com.jiudianlianxian.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiudianlianxian.bean.BuySeedResult;
import com.jiudianlianxian.bean.FruitRipeResult;
import com.jiudianlianxian.bean.GetLandMsgResult;
import com.jiudianlianxian.bean.GetSeedMsgResult;
import com.jiudianlianxian.bean.HarvestResult;
import com.jiudianlianxian.bean.LoginResult;
import com.jiudianlianxian.bean.OpenWarehouseResult;
import com.jiudianlianxian.bean.PlantResult;
import com.jiudianlianxian.bean.SeedMsgAllResult;
import com.jiudianlianxian.data.BuySeedResultData;
import com.jiudianlianxian.data.FruitRipeResultData;
import com.jiudianlianxian.data.GetLandMsgResultData;
import com.jiudianlianxian.data.GetSeedMsgResultData;
import com.jiudianlianxian.data.HarvestResultData;
import com.jiudianlianxian.data.LoginResultData;
import com.jiudianlianxian.data.OpenWarehouseResultData;
import com.jiudianlianxian.data.PlantResultData;
import com.jiudianlianxian.data.SeedMsgAllResultData;
import com.jiudianlianxian.domain.RipeMessage;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.service.JDBCService;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudiannnnnnn.SqlTest;

/**
 * 
 * @Title: AcceptClientMessage
 * @Description: 接受用户信息
 * @Company: 济宁九点连线信息技术有限公司
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017年9月27日 上午8:48:09
 *
 */
public class AcceptClientMessage implements Runnable {
	private static final String END_MARK = "quit"; // 断开连接标识
	private static final String VIEW_USER = "viewuser"; // 查看连接客户端列表

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // 服务器已启用线程集合
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // 存放消息的队列

	private Socket socket;

	private BufferedReader buff;

	private Writer writer;

	private String userName; // 成员名称

	private JDBCService jdbcService = new JDBCService();

	String read = null;

	/**
	 * 构造函数<br>
	 * 处理客户端的消息，加入到在线成员列表中
	 * 
	 * @throws Exception
	 */
	public AcceptClientMessage(Socket socket,
			List<AcceptClientMessage> threadList, BlockingQueue<String> msgQueue) {
		this.socket = socket;
		this.threadList = threadList;
		this.msgQueue = msgQueue;
		this.userName = String.valueOf(socket.getPort());
		// msg = {info:login,data:011Wmx7r0Kf8Qq1ORS6r0eqg7r0Wmx7n}

		// try {
		// this.buff = new BufferedReader(new InputStreamReader(
		// socket.getInputStream(), "UTF-8"));
		// this.writer = new OutputStreamWriter(socket.getOutputStream(),
		// "UTF-8");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		try {
			// 获取客户端发来的数据
			InputStream is = socket.getInputStream();
			int len = is.available() + 1;
			System.out.println("len == " + len);
			byte[] buff = new byte[len];

			try {
				is.read(buff);
			} catch (SocketException e) {
				System.out.println("有客户断开连接~");
			}
			// 输出接收到的数据
			read = new String(buff);
			System.out.println("收到数据：" + read);
			// 给玩家发送数据
			String data = "恭喜你，连接成功啦~~";
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 检查是否有果实成熟，有的话给客户提示
		furitRipe();
		
		userList.add(this.userName);
		threadList.add(this);
		pushMsg("【" + this.userName + "已连接成功】");
		System.out.println("Form Cliect[port:" + socket.getPort() + "] "
				+ this.userName + "已进入房间可以开始游戏");
	}

	@Override
	public void run() {
		try {
			while (true) {
				// String msg = buff.readLine();
				String msg = read;
				String jsonObject = "";
				System.out.println("接收到用户发送的msg = " + msg);

				JSONObject jsonObject1 = null;
				String info = null;
				JSONObject data = null;
				try {
					jsonObject1 = new JSONObject(msg);
					info = jsonObject1.getString("info");
					
//					String code = jsonObject1.getString("data");
//					System.out.println("info = " + info + "    data = " + code);
					
					// data = jsonObject1.getJSONObject("data");
					// System.out.println("data  = " + data.toString());
					// System.out.println("username = " +
					// data.getString("username"));
					// System.out.println("sex = " + data.getString("sex"));
					// System.out.println("age = " + data.getString("age"));
					//
				} catch (JSONException e) {
					//
					e.printStackTrace();
				}

				if ("login".equals(info)) { // 判断是什么请求

					String code = jsonObject1.getString("data");
					System.out.println("data = " + code);
					LoginResult loginResult = new LoginResult();
					
					SqlTest sqlTest = new SqlTest();
					sqlTest.test15(code);
					
//					if (jdbcService.login(code)) { // 登录是否成功
//						User user = new User();
//						jdbcService.WeiXinlogin(code,user);
//						
//						if (user != null) {
//							LoginResultData loginResponseData = jdbcService
//									.loginResult(user);
//
//							
//
//							loginResult.setInfo(info);
//							loginResult.setCode("1");
//							loginResult.setLoginResponseData(loginResponseData);
//							jsonObject = com.alibaba.fastjson.JSONObject
//									.toJSONString(loginResult);
//							System.out.println("jsonObject  = " + jsonObject);
//							pushMsg(jsonObject);
//						}else {
//							
//							loginResult.setInfo(info);
//							loginResult.setCode("0");
//							jsonObject = com.alibaba.fastjson.JSONObject
//									.toJSONString(loginResult);
//							System.out.println("jsonObject  = " + jsonObject);
//							pushMsg(jsonObject);
//						}
//						
//
//					} else {
//						loginResult.setInfo(info);
//						loginResult.setCode("0");
//						jsonObject = com.alibaba.fastjson.JSONObject
//								.toJSONString(loginResult);
//						System.out.println("jsonObject  = " + jsonObject);
//						pushMsg(jsonObject);
//					}

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
						System.out.println("jsonObject  = " + jsonObject);
						pushMsg(jsonObject);

					} else {
						seedMsgAllResult.setCode("0");
						seedMsgAllResult
								.setSeedMsgAllResultData(seedMsgAllResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(seedMsgAllResult);
						System.out.println("jsonObject  = " + jsonObject);
						pushMsg(jsonObject);
					}

				} else if ("buy".equals(info)) {
					// 根据请求参数，修改用户的种子数量和金币数量
					String userId = data.getString("userId");
					String seedId = data.getString("seedId");
					String seedNumber = data.getString("seedNumber");
					// 根据id查找用户信息，更改其种子数量和金币数量
					// 1.查询种子信息，计算价格
					// 2.查询用户金币信息，金币是否购买种子，如不够直接返回提示用户
					// 3.金币够，则修改用户的种子信息和金币信息
					// 4.将新的用户数据返回给用户
					BuySeedResultData buySeedResultData = jdbcService
							.getBuySeedResultData(userId, seedId, seedNumber);
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
						System.out.println("jsonObject  = " + jsonObject);
						pushMsg(jsonObject);
					}

				} else if ("getlandmsg".equals(info)) {
					// 查询土地所有信息
					Long userId = (long) 1;
					
					
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
						System.out.println("jsonObject  = " + jsonObject);
						pushMsg(jsonObject);
					} else {
						getLandMsgResult.setCode("0");
						getLandMsgResult
								.setGetLandMsgResultData(getLandMsgResultData);
						jsonObject = com.alibaba.fastjson.JSONObject
								.toJSONString(getLandMsgResult);
						System.out.println("jsonObject  = " + jsonObject);
						pushMsg(jsonObject);
					}

				} else if ("plant".equals(info)) {
					// 修改种子数量，种子状态，土地状态，种子生长的过程监控。及时给客户端发送消息
					// userId seedId landId
					Long userId = (long) 1;
					Long seedId = (long) 1;
					Long landId = (long) 1;

					PlantResult plantResult = new PlantResult();
					PlantResultData plantResultData = jdbcService.plant(userId,
							seedId, landId);
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

				} else if ("harvest".equals(info)) {
					// 修改土地状态，种子状态，生成果实修改果实数量
					Long userId = (long) 1;
					Long landId = (long) 1;

					HarvestResult harvestResult = new HarvestResult();
					HarvestResultData harvestResultData = jdbcService.harvest(landId, userId);
					
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
		
				}else if ("warehouse".equals(info)) {  //打开仓库    收获的果实
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
					
					
				}else if ("parcel".equals(info)) {   //包裹  已购买的种子
					// 查询果实所有信息
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

				if (VIEW_USER.equals(msg)) { // 查看已连接客户端
					sendMsg(onlineUsers());
				} else if (END_MARK.equals(msg)) { // 遇到退出标识时就结束让客户端退出
					sendMsg(END_MARK);
					break;
				} else {
					// pushMsg(String.format("%1$s说：%2$s", userName, msg)); //
					// 用于给用户发送信息
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
			System.out.println("Form Cliect[port:" + socket.getPort() + "] "
					+ userName + "断开连接");
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
	public void sendMsg(String msg) {
		try {
			System.out.println("msg = sendMsg" + msg);
			writer.write(msg);
			writer.write("\015\012");
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 已连接用户列表
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
	 * @Description: 每次socket连接以后，都会执行此方法，检查是否有果实成熟，有的话给客户发送消息
	 */
	private void furitRipe(){
		
		//1.查询farm_seed表，获取seedState为3的种子集合
		
		//2.遍历种子集合得到种子信息
		List<RipeMessage> ripeMessages = new ArrayList<RipeMessage>();
		String sql = "select * from farm_seed where seedState='3'";
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
				System.out.println("传入landId的土地状态    landState = "
						);
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
			
			if (currentTime >= (plantTime + growthTime)) {
				Long landId = null;
				String sql1 = "select * from land_seed where seedId=" + ripeMessage.getSeedId();
				System.out.println("sql1 = " + sql1);
				ResultSet rs1 = JDBCUtil.executeQuery(sql);
				try {
					while (rs1.next()) {
						landId = rs1.getLong(1);
						System.out.println("传入landId的土地状态    landState = "
								);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
				}
				
				
				
				fruitRipeResultData.setLandId(landId);
				fruitRipeResultData.setLandState("1");
				fruitRipeResultData.setUserId(ripeMessage.getUserId());
				fruitRipeResult.setFruitRipeResultData(fruitRipeResultData);
				fruitRipeResult.setCode("1");
				String jsonObject = com.alibaba.fastjson.JSONObject
						.toJSONString(fruitRipeResult);
				System.out.println("jsonObject  = " + jsonObject);
				pushMsg(jsonObject);
				
			}else {
				//  果实为成熟
			}			
		}	
	}
	
	

}
