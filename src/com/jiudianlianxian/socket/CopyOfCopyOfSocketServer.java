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

	private static final int SERVER_PORT = 8944; // ����˶˿�
	private static final String END_MARK = "quit"; // �Ͽ����ӱ�ʶ
	private static final String VIEW_USER = "viewuser"; // �鿴���ӿͻ����б�
	Long seedState1 = 1L;    //����״̬----�̵�
	Long seedState2 = 2L;    //����״̬----����
	Long seedState3 = 3L;    //����״̬----��ֲ
	Long landState1 = 1L;    //����״̬----δ����
	Long landState2 = 2L;    //����״̬----δ��ֲ���ѿ���
	Long landState3 = 3L;    //����״̬----����ֲ����ʱ���س���һ�����Ӷ���
	Long landState4 = 4L;    //����״̬----��ʵ����

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<DisposeAffair> threadList = new ArrayList<DisposeAffair>(); // �������������̼߳���
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // �����Ϣ�Ķ���

	static CopyOfCopyOfSocketServer server;

	private JDBCService jdbcService = new JDBCService();

	/**
	 * ���
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
//					System.out.println("----ִ������ ");
				}
			};
//			long timestamp = 60000; 
			long timestamp = 1000; 
			timer.schedule(timerTask, date1,timestamp);
			
			server = new CopyOfCopyOfSocketServer(); // ���������
			server.load();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void furitRipe() {
		//1.��ѯfarm_seed����ȡseedStateΪ3�����Ӽ���
		
		/**
		 * 1.��ѯfarm_land,��ȡ״̬Ϊ3������id
		 * 2.����landId���ϲ�ѯland_seed����ȡseedId
		 * 3.����seedId���ϣ���ѯfarm_land����ȡ���ӵ���ֲʱ�������ʱ��
		 * 4.������ȡ����������Ϣ���ж������Ƿ񳤳ɹ�ʵ��
		 * 5.�ǵĻ������ݴ�����id����ѯland_seed����ȡlandid
		 * 6.���h����ֱ�ӷ��ؿն���
		 * 7.�ı��ȡ��������id��
		 */
		Long landState4 = 4L;
		//1.��ѯfarm_land,��ȡ״̬Ϊ3������id
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
		// 2.����landId���ϲ�ѯland_seed����ȡseedId
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
			
			//3.����seedId���ϣ���ѯfarm_seed����ȡ���ӵ���ֲʱ�������ʱ��
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
			
			
			
			//4.������ȡ����������Ϣ���ж������Ƿ񳤳ɹ�ʵ��
			for (RipeMessage ripeMessage : ripeMessages) {
				
				Long plantTime = ripeMessage.getSeedPlantTime();   //��ֲʱ��
				Long currentTime = new Date().getTime();           //��ǰʱ��
				Long growthTime = ripeMessage.getSeedGrowthTime();  //����ʱ��
				Long harvestTime = plantTime + growthTime;          //�ջ�ʱ��
				
				if (currentTime >= harvestTime) {
					//1.����seedId��ȡlandId   
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
					//2.����landId�ı�����״̬
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
	 * ����ͻ��˷�������Ϣ�߳���
	 */
	class DisposeAffair implements Runnable {

		private Socket socket;

		private BufferedReader buff;

		private InputStream is;

		private Writer writer;

		private String userName; // ��Ա����

		/**
		 * ���캯��<br>
		 * ����ͻ��˵���Ϣ�����뵽���߳�Ա�б���
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
					+ this.userName + "�ѽ��뷿����Կ�ʼ��Ϸ");
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
							System.out.println("����ʱ�䣺" + date.getHours()  + " ʱ   " + date.getMinutes() + " ��    " + date.getSeconds() + " �� ");
				
							
							if ("login".equals(info)) {
								// ����ǵ�¼�������ȡ����¼���û��������룬�������ݿ��ѯ���ݣ��Ƿ��û�����������ȡ�������ȷ���򷵻ظ��û���¼�ɹ�����ʾ
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
										.println("��ȡ�����û���openid     Access_token = "
												+ unionID.getAccess_token()
												+ "    getOpenid = "
												+ unionID.getOpenid());
								
								if (queryUser(user, unionID.getOpenid())) {
									System.out
											.println("���ݿ��в�ѯ����openid�û������ݣ����ظ��ͻ���");
									// TODO �����ƣ����ӳ�����Ϣ�ķ���
									
									


									resultLoginData(loginResult, user);

								} else {
									System.out
											.println("���ݿ�δ��ѯ����openid�û������ݣ������������󣬻�ȡ��openid�û�������");

									// û�����ݣ����ݻ�ȡ����openidȥ΢�ŷ�������ȡ�û�����
									String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
											+ unionID.getAccess_token()
											+ "&openid=" + unionID.getOpenid();
									System.out
											.println("δ�����ݿ��ȡ�����ݣ���������������ݻ�ȡ----url  = "
													+ url);
									HttpUtil.requestData(url,
											new HttpCallBackListener() {
												@Override
												public void onFinish(
														String respose) {
													// �ж����󵽵������Ƿ�Ϊ��
													if (respose != null) {
														System.out
																.println("��ȡ���û���Ϣjson  = "
																		+ respose);
														// ��Ϊ�գ����н������������������ݱ��浽�������ݿ���ȥ
														analysisInsertData(
																user, respose);
														// ����ȡ�������ݷ��ظ��ͻ���
														resultLoginData(
																loginResult,
																user);
														System.out
																.println("���������û�����  = "
																		+ user);
													} else {
														System.out
																.println("��ȡ���û���Ϣ��jsonΪ��  = "
																		+ user);
													}
												}

												@Override
												public void onError(Exception e) {
													// �����쳣
													System.out
															.println(" ��ȡ���û���Ϣ����������ʧ�� �����ͻ�����ʾ = "
																	+ user);

												}

												// ����΢���״ε�¼��ȡ�����û����ݣ������뵽�������ݿ�
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
														System.out.println("�ող������ݵ�id ---  11 = "
																+ user.getUserId());

													} catch (Exception e) {
														e.printStackTrace();
													}
												}

											});

								}

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

							} else if ("buy".equals(info)) { // ��������
								// ��������������޸��û������������ͽ������
								Long userId = Long.valueOf(json
										.getString("userId"));
								Long seedId = Long.valueOf(json
										.getString("seedId"));
								int seedNumber = Integer.valueOf(json
										.getString("seedNumber"));
								// ����id�����û���Ϣ�����������������ͽ������
								// 1.��ѯ������Ϣ������۸�
								// 2.��ѯ�û������Ϣ������Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
								// 3.��ҹ������޸��û���������Ϣ�ͽ����Ϣ
								// 4.���µ��û����ݷ��ظ��û�
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

							} else if ("getlandmsg".equals(info)) { // ��ȡ������Ϣ
								// ��ѯ����������Ϣ
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

							} else if ("plant".equals(info)) { // ��ֲ
								// �޸���������������״̬������״̬�����������Ĺ��̼�ء���ʱ���ͻ��˷�����Ϣ
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

							} else if ("harvest".equals(info)) { // �ջ�
								// �޸�����״̬������״̬�����ɹ�ʵ�޸Ĺ�ʵ����
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

							} else if ("warehouse".equals(info)) { // �򿪲ֿ� �ջ�Ĺ�ʵ
								// ��ѯ��ʵ������Ϣ
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

							} else if ("parcel".equals(info)) { // ���� �ѹ��������
								// ��ѯ��������������Ϣ
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

							} else if ("sell".equals(info)) { // ���۹�ʵ
								// ���ݴ����ʵid��ѯ��ʵʣ���������жϲ�ѯ���������Ƿ���ڴ�������������ڵĻ���ɾ�����ݿ��ʵ���ı��û����ý�ң����ظ��ͻ��˽�ң��ͳ��۹�ʵ��Ϣ
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
								//��������ʱ��
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
//									System.out.println("ִ������ ");
								}
							};
//							long timestamp = 600000;  
							long timestamp = 1000; 
							timer.schedule(timerTask, date1,timestamp);
							

							if (VIEW_USER.equals(msg)) { // �鿴�����ӿͻ���
								sendMsg(onlineUsers());
							} else if (END_MARK.equals(msg)) { // �����˳���ʶʱ�ͽ����ÿͻ����˳�
								sendMsg(END_MARK);
								break;
							} else {
								// pushMsg(String.format("%1$s˵��%2$s", userName,
								// msg)); // ���ڸ��������û�����������Ϣ
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
				System.out.println("Form Cliect[port:" + socket.getPort()
						+ "] " + userName + "�Ͽ�����");
			}
		}

		private void furitRipe(User user) {
			String jsonObject;
			FruitRipeResult fruitRipeResult = new FruitRipeResult();
			fruitRipeResult.setInfo("fruitRipe");
			FruitRipeResultData fruitRipeResultData = new FruitRipeResultData();
			Long landState4 = 4L;
			
			//1.��ѯfarm_land,��ȡ״̬Ϊ3������id
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
			// 2.����landId���ϲ�ѯland_seed����ȡseedId
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
				
				//3.����seedId���ϣ���ѯfarm_seed����ȡ���ӵ���ֲʱ�������ʱ��
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
				Long plantTime = (ripeMessage.getSeedPlantTime())/1000;   //��ֲʱ��
				Long currentTime = (new Date().getTime())/1000;           //��ǰʱ��
				Long growthTime = (ripeMessage.getSeedGrowthTime())/1000;  //����ʱ��
				Long harvestTime = plantTime + growthTime;          //�ջ�ʱ��
				
//				if ((harvestTime + 300000) >= currentTime && currentTime >= harvestTime) {
				if ((harvestTime + 10) >= currentTime && currentTime >= harvestTime) {
//				if (currentTime == harvestTime) {
					//1.����seedId��ȡlandId
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
					//2.����landId�ı�����״̬
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
					//  ��ʵΪ����
					fruitRipeResult.setCode("��ʵδ����");
					jsonObject = com.alibaba.fastjson.JSONObject
							.toJSONString(fruitRipeResult);
//					System.out.println("----jsonObject  ----= " + jsonObject);
//					pushMsg(jsonObject);
				}			
			}
			
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
		 * ���������߳�Ա�б�
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
		 * @Description: ��ȡaccess_token��������ݿ��ѯ�Ƿ���openid���û�
		 * @param user
		 * @return
		 */
		private boolean queryUser(User user, String openid) {
			// ��ѯ���ݿ�
			// �����û��Ƿ��Ѿ�ʹ�ô�΢���˺ŵ�½������¼����ֱ��ʹ�����ݿ����ݣ�����������΢�ŷ���������ȡ�µ��û�����
			boolean bb = false;
			String sql = "select * from farm_user where openid='" + openid
					+ "'";
			System.out.println("sql = " + sql);
			ResultSet rs = JDBCUtil.executeQuery(sql);

			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
				while (rs.next()) {
					System.out.println("�������ݿ��ѯ������");
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
		 * @Description: ��ȡ��user���������ݷ��ظ��ͻ���
		 * @param loginResult
		 * @param user
		 */
		// ��ȡ��user���������ݷ��ظ��ͻ���
		private void resultLoginData(LoginResult loginResult, User user) {
			//1.����userId��ѯ�û�������Ϣ
			List<Land> lands = new ArrayList<Land>();
			String sql1 = "select * from farm_land where userId="
					+ user.getUserId();
			System.out.println("sql1 = " + sql1);
			ResultSet rs1 = JDBCUtil.executeQuery(sql1);
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
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

			// 2.����lands����ȡ��land�����landState��
			List<LandData> landDatas = new ArrayList<LandData>();
			for (Land land : lands) {
				
				LandData landData = new LandData();
				
				landData.setLandId(land.getLandId());
				landData.setLandName(land.getLandName());
				landData.setLandState(land.getLandState());
				
				if (land.getLandState() == 3 || land.getLandState() == 4 ) {
					// 3.״̬��3����ֲ״̬����ѯland_seed����ȡseedId
					Long seedId = null;
					String sql = "select * from land_seed where landId="
							+ land.getLandId();

					System.out.println("sql = " + sql);
					ResultSet rs = JDBCUtil.executeQuery(sql);
					try {
						while (rs.next()) {
							seedId = rs.getLong(2);
							System.out.println("�û�������Ϣ    seedId = " + seedId);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						JDBCUtil.close(rs, JDBCUtil.getPs(),
								JDBCUtil.getConnection());
					}
					// ���ݻ�ȡ����seedId����ѯfarm_seed����ȡ��seedName
					String sql2 = "select * from farm_seed where seedId="
							+ seedId;

					System.out.println("sql2 = " + sql2);
					ResultSet rs2 = JDBCUtil.executeQuery(sql2);
					try {
						while (rs2.next()) {

							landData.setSeedName(rs2.getString(2));
							System.out
									.println("�û�������Ϣ  landData.getSeedName() = "
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
	 * ������ͻ��˷�����Ϣ���̣߳�ʹ���̴߳���ÿ���ͻ��˷�������Ϣ
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception {
		new Thread(new PushMsgTask()).start(); // ������ͻ��˷�����Ϣ���߳�

		while (true) {
			// server���Խ�������Socket����������server��accept����������ʽ��
			Socket socket = this.accept();
			System.out.println("�ͻ������ӿ����߳�");
			/**
			 * ���ǵķ���˴���ͻ��˵�����������ͬ�����еģ� ÿ�ν��յ����Կͻ��˵����������
			 * ��Ҫ�ȸ���ǰ�Ŀͻ���ͨ����֮������ٴ�����һ���������� ���ڲ����Ƚ϶������»�����Ӱ���������ܣ�
			 * Ϊ�ˣ����ǿ��԰�����Ϊ���������첽������ͻ���ͨ�ŵķ�ʽ
			 */
			// ÿ���յ�һ��Socket�ͽ���һ���µ��߳���������
			new Thread(new DisposeAffair(socket)).start();
		}
	}

	/**
	 * ����Ϣ������ȡ��Ϣ���ٷ��͸������ӵ����пͻ��˳�Ա
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
