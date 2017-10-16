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
 * @Description: �����û���Ϣ
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��9��27�� ����8:48:09
 *
 */
public class AcceptClientMessage implements Runnable {
	private static final String END_MARK = "quit"; // �Ͽ����ӱ�ʶ
	private static final String VIEW_USER = "viewuser"; // �鿴���ӿͻ����б�

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); // �������������̼߳���
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // �����Ϣ�Ķ���

	private Socket socket;

	private BufferedReader buff;

	private Writer writer;

	private String userName; // ��Ա����

	private JDBCService jdbcService = new JDBCService();

	String read = null;

	/**
	 * ���캯��<br>
	 * ����ͻ��˵���Ϣ�����뵽���߳�Ա�б���
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
			// ��ȡ�ͻ��˷���������
			InputStream is = socket.getInputStream();
			int len = is.available() + 1;
			System.out.println("len == " + len);
			byte[] buff = new byte[len];

			try {
				is.read(buff);
			} catch (SocketException e) {
				System.out.println("�пͻ��Ͽ�����~");
			}
			// ������յ�������
			read = new String(buff);
			System.out.println("�յ����ݣ�" + read);
			// ����ҷ�������
			String data = "��ϲ�㣬���ӳɹ���~~";
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ����Ƿ��й�ʵ���죬�еĻ����ͻ���ʾ
		furitRipe();
		
		userList.add(this.userName);
		threadList.add(this);
		pushMsg("��" + this.userName + "�����ӳɹ���");
		System.out.println("Form Cliect[port:" + socket.getPort() + "] "
				+ this.userName + "�ѽ��뷿����Կ�ʼ��Ϸ");
	}

	@Override
	public void run() {
		try {
			while (true) {
				// String msg = buff.readLine();
				String msg = read;
				String jsonObject = "";
				System.out.println("���յ��û����͵�msg = " + msg);

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

				if ("login".equals(info)) { // �ж���ʲô����

					String code = jsonObject1.getString("data");
					System.out.println("data = " + code);
					LoginResult loginResult = new LoginResult();
					
					SqlTest sqlTest = new SqlTest();
					sqlTest.test15(code);
					
//					if (jdbcService.login(code)) { // ��¼�Ƿ�ɹ�
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

				} else if ("shop".equals(info)) { // ��ȡ����������Ϣ----�̵�
					
					// ��ѯ���ݿ������б���������
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
					// ��������������޸��û������������ͽ������
					String userId = data.getString("userId");
					String seedId = data.getString("seedId");
					String seedNumber = data.getString("seedNumber");
					// ����id�����û���Ϣ�����������������ͽ������
					// 1.��ѯ������Ϣ������۸�
					// 2.��ѯ�û������Ϣ������Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
					// 3.��ҹ������޸��û���������Ϣ�ͽ����Ϣ
					// 4.���µ��û����ݷ��ظ��û�
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
					// ��ѯ����������Ϣ
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
					// �޸���������������״̬������״̬�����������Ĺ��̼�ء���ʱ���ͻ��˷�����Ϣ
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
					// �޸�����״̬������״̬�����ɹ�ʵ�޸Ĺ�ʵ����
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
		
				}else if ("warehouse".equals(info)) {  //�򿪲ֿ�    �ջ�Ĺ�ʵ
					// ��ѯ��ʵ������Ϣ
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
					
					
				}else if ("parcel".equals(info)) {   //����  �ѹ��������
					// ��ѯ��ʵ������Ϣ
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

				if (VIEW_USER.equals(msg)) { // �鿴�����ӿͻ���
					sendMsg(onlineUsers());
				} else if (END_MARK.equals(msg)) { // �����˳���ʶʱ�ͽ����ÿͻ����˳�
					sendMsg(END_MARK);
					break;
				} else {
					// pushMsg(String.format("%1$s˵��%2$s", userName, msg)); //
					// ���ڸ��û�������Ϣ
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // �ر���Դ�������Ƴ����пͻ��˳�Ա
			try {
				writer.close();
				buff.close();
				socket.close();
			} catch (Exception e) {

			}
			userList.remove(userName);
			threadList.remove(this);
			pushMsg("��" + userName + "�Ͽ����ӡ�");
			System.out.println("Form Cliect[port:" + socket.getPort() + "] "
					+ userName + "�Ͽ�����");
		}
	}

	/**
	 * ׼�����͵���Ϣ�������
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
	 * ������Ϣ
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
	 * �������û��б�
	 * 
	 * @return
	 */
	private String onlineUsers() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("======== �����ӿͻ����б�(").append(userList.size())
				.append(") ========\015\012");
		for (int i = 0; i < userList.size(); i++) {
			sbf.append("[" + userList.get(i) + "]\015\012");
		}
		sbf.append("===============================");
		return sbf.toString();
	}
	
	/**
	 * 
	 * @Description: ÿ��socket�����Ժ󣬶���ִ�д˷���������Ƿ��й�ʵ���죬�еĻ����ͻ�������Ϣ
	 */
	private void furitRipe(){
		
		//1.��ѯfarm_seed����ȡseedStateΪ3�����Ӽ���
		
		//2.�������Ӽ��ϵõ�������Ϣ
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
				System.out.println("����landId������״̬    landState = "
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
						System.out.println("����landId������״̬    landState = "
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
				//  ��ʵΪ����
			}			
		}	
	}
	
	

}
