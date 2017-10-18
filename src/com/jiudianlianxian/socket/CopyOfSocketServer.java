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

	private static final int SERVER_PORT = 8944; // ����˶˿�
	private static final String END_MARK = "quit"; // �Ͽ����ӱ�ʶ
	private static final String VIEW_USER = "viewuser"; // �鿴���ӿͻ����б�

	private static List<String> userList = new CopyOnWriteArrayList<String>();
	private static List<DisposeAffair> threadList = new ArrayList<DisposeAffair>(); // �������������̼߳���
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // �����Ϣ�Ķ���

	static CopyOfSocketServer server;

	private JDBCService jdbcService = new JDBCService();

	/**
	 * ���
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			server = new CopyOfSocketServer(); // ���������
			server.load();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CopyOfSocketServer() throws Exception {
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
								// ����ǵ�¼�������ȡ����¼���û��������룬�������ݿ��ѯ���ݣ��Ƿ��û�����������ȡ�������ȷ���򷵻ظ��û���¼�ɹ�����ʾ
								String code = json.getString("data");
								String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
										+ code
										+ "&grant_type=authorization_code";
								System.out.println("urlStr  = " + urlStr);

								UnionID unionID = new UnionID();
								unionID.setAccess_token("����ɹ�");
								unionID.setOpenid("oVVdbwvclc7NjI2xjhUxE-Gq_daU");
								LoginResult loginResult = new LoginResult();
								loginResult.setInfo(info);
								System.out
										.println("��ȡ�����û���openid     Access_token = "
												+ unionID.getAccess_token()
												+ "    getOpenid = "
												+ unionID.getOpenid());
								User user = new User();
								if (queryUser(user, unionID.getOpenid())) {
									System.out
											.println("���ݿ��в�ѯ����openid�û������ݣ����ظ��ͻ���");

									resultLoginData(loginResult, user);

								} else {
									System.out
											.println("���ݿ�δ��ѯ����openid�û������ݣ������������󣬻�ȡ��openid�û�������");

									// û�����ݣ����ݻ�ȡ����openidȥ΢�ŷ�������ȡ�û�����

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

							} else if ("buy".equals(info)) {
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

							} else if ("getlandmsg".equals(info)) {
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

							} else if ("plant".equals(info)) {
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
								// ��ѯ��ʵ������Ϣ
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
		private void resultLoginData(LoginResult loginResult, User user) {
			List<Land> lands = new ArrayList<Land>();
			List<Seed> landSeeds = new ArrayList<Seed>();
			String sql1 = "select * from farm_land where userId="
					+ user.getUserId();
			ResultSet rs1 = JDBCUtil.executeQuery(sql1);
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
				while (rs1.next()) {
					Land land = new Land();
					land.setLandId(rs1.getLong(1));
					land.setLandName(rs1.getString(2));
					land.setLandState(rs1.getString(3));

					// ��ȡ�����ص�״̬������״̬��3������id��ѯland_seed����ȡseedid����ѯfarm_seed����ȡ������Ϣ
					if (land.getLandState().equals("3")) {
						String sql2 = "select * from land_seed where landId="
								+ land.getLandId();
						ResultSet rs2 = JDBCUtil.executeQuery(sql2);
						try {
							// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
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
