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

import org.apache.http.util.TextUtils;
import org.json.JSONArray;
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
import com.jiudianlianxian.domain.Land;
import com.jiudianlianxian.domain.RipeMessage;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.service.JDBCService;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;
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

	//�������û�����
	private static List<String> userList = new CopyOnWriteArrayList<String>();
	// �������������̼߳���
	private static List<AcceptClientMessage> threadList = new ArrayList<AcceptClientMessage>(); 
	// �����Ϣ�Ķ���
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(20); 

	private Socket socket;

	private BufferedReader buff;

	private Writer writer;

	private String userName; // ��Ա����

	private JDBCService jdbcService = new JDBCService();

	String read = null;
	
	JSONObject jsonObject1;
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
			is.read(buff);
			// ������յ�������
			read = new String(buff);
			System.out.println("�յ����ݣ�" + read);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ����Ƿ��й�ʵ���죬�еĻ����ͻ���ʾ
//		furitRipe();
		
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
				if(TextUtils.isEmpty(msg)) continue;                   
                System.out.println("------"+msg);
				System.out.println("���յ��û����͵�msg = " + msg);

				JSONObject jsonObject1 = null;
				String info = null;
				JSONObject data = null;
				try {
					jsonObject1 = new JSONObject(msg);
					info = jsonObject1.getString("info");
					
//					String code = jsonObject1.getString("data");
//					System.out.println("info = " + info + "    data = " + code);
					
				} catch (JSONException e) {
					//
					e.printStackTrace();
				}

				if ("login".equals(info)) { // �ж���ʲô����

					String code = jsonObject1.getString("data");
					System.out.println("data = " + code);
					LoginResult loginResult = new LoginResult();
//					SqlTest sqltest = new SqlTest();
//					sqltest.test15(code);
					
					// ��¼ɸѡ
					User user = new User();
					System.out.println("����΢�ŵ�¼����WeiXinlogin");
					String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
							+ code + "&grant_type=authorization_code";
					System.out.println(" url  == " + url);
					
					
					// �ͻ��˷��͵�code����΢����������Ȼ��
					HttpUtil.requestData(url, new HttpCallBackListener() {

						@Override
						public void onFinish(String response) {
							System.out.println("onFinish  response  = "
									+ response.toString());
							// ��������
							if (response != null) {
								System.out.println("��ȡaccess_token  json = " + response);
								analysisAccessToken(response);
								System.out.println("access_token_______________ = "
										+ access_token + "    --  openid  = " + openid);

								boolean bb = queryUser(user);
								// �ж����ݿ��Ƿ������û�����
								if (bb) {
									System.out
											.println("--------��ȡ�����ݣ����----------------------");
									// �����ݣ���ֱ�ӷ��ظ��ͻ���
									resultLoginData(loginResult, user);
									System.out.println("���ݿ��ѯ���û��������ٴ�����ֱ�ӵõ��û� = " + user);

								} else {
									// û�����ݣ����ݻ�ȡ����openidȥ΢�ŷ�������ȡ�û�����
									String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
											+ access_token + "&openid=" + openid;
									System.out.println("δ�����ݿ��ȡ�����ݣ���������������ݻ�ȡ----url  = "
											+ url);
									HttpUtil.requestData(url, new HttpCallBackListener() {
										@Override
										public void onFinish(String respose) {
											// �ж����󵽵������Ƿ�Ϊ��
											if (respose != null) {
												System.out.println("��ȡ���û���Ϣjson  = "
														+ respose);
												// ��Ϊ�գ����н������������������ݱ��浽�������ݿ���ȥ
												analysisInsertData(user, respose);
												// ����ȡ�������ݷ��ظ��ͻ���
												resultLoginData(loginResult, user);
												System.out.println("���������û�����  = " + user);
											} else {
												System.out.println("��ȡ���û���Ϣ��jsonΪ��  = "
														+ user);
											}
										}

										@Override
										public void onError(Exception e) {
											// �����쳣
											System.out.println(" ��ȡ���û���Ϣ����������ʧ�� �����ͻ�����ʾ = "
													+ user);

										}
										// ����΢���״ε�¼��ȡ�����û����ݣ������뵽�������ݿ�
										private void analysisInsertData(User user,
												String respose) {
											try {
												JSONObject jsonObject2 = new JSONObject(
														respose);
												openid = jsonObject2.getString("openid");
												nickname = jsonObject2
														.getString("nickname");
												sex = jsonObject2.getInt("sex");
												province = jsonObject2
														.getString("province");

												city = jsonObject2.getString("city");
												country = jsonObject2.getString("country");
												headimgurl = jsonObject2
														.getString("headimgurl");
												jsonArray = jsonObject2
														.getJSONArray("privilege");
												for (int i = 0; i < jsonArray.length(); i++) {
													privilege[i] = jsonArray.getString(i);
												}
												unionid = jsonObject2.getString("unionid");

											} catch (JSONException e) {
												//
												e.printStackTrace();
											}
											System.out.println("nickname = " + nickname);

											Long userGold = (long) 5000;
											Long userExperience = (long) 500;
											String sql3 = "INSERT INTO farm_user("
													+ "userNickName,userImage,userGold,openid,userExperience)"
													+ "VALUES('" + nickname + "','"
													+ headimgurl + "'," + userGold + ",'"
													+ openid + "'," + userExperience + ") ";
											System.out.println("sql3====" + sql3);

											try {
												user.setUserId(JDBCUtil
														.executeUpdateGetId(sql3));
												user.setUserNickName(nickname);
												user.setUserImage(headimgurl);
												user.setUserGold(userGold);
												user.setOpenid(openid);
												user.setUserExperience(userExperience);
												System.out.println("�ող������ݵ�id ---  11 = "
														+ user.getUserId());

											} catch (Exception e) {
												e.printStackTrace();
											}
										}

									});
								}

							} else {
								System.out.println("��ȡaccess_token ��jsonΪ�� = " + user);
							}
						}

						@Override
						public void onError(Exception e) {
							// �����쳣
							System.out.println("��ȡaccess_token ����������ʧ��,���ͻ�����ʾ  = " + user);

						}
						
						// ��ȡ��user���������ݷ��ظ��ͻ���
						private void resultLoginData(LoginResult loginResult, User user) {
							List<Land> lands = new ArrayList<Land>();
							// Set<Land> lands = new HashSet<Land>();
							for (int i = 0; i < 10000; i++) {
								Land land = new Land();
								String sql1 = "select * from farm_land where userId="
										+ user.getUserId();
								ResultSet rs1 = JDBCUtil.executeQuery(sql1);
								try {
									// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
									while (rs1.next()) {
										land.setLandId(rs1.getLong(1));
										land.setLandName(rs1.getString(2));
										land.setLandState(rs1.getString(3));
									}
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									JDBCUtil.close(rs1, JDBCUtil.getPs(),
											JDBCUtil.getConnection());
								}
								lands.add(land);
							}
							LoginResultData loginResponseData = new LoginResultData();
							// user.setUserLands(lands);

							loginResponseData.setUser(user);
							loginResponseData.setLands(lands);
							String loginResultData = com.alibaba.fastjson.JSONObject
									.toJSONString(loginResponseData);
							System.out.println("loginResultData------------  = "
									+ loginResultData);

							loginResult.setInfo("info");
							loginResult.setCode("1");
							loginResult.setLoginResponseData(loginResponseData);
							String jsonObject = com.alibaba.fastjson.JSONObject
									.toJSONString(loginResult);
							System.out.println("jsonObject  = " + jsonObject);
							pushMsg(jsonObject);
						}

						// ����access_token�������������ݣ��õ�access_token��openid
						private void analysisAccessToken(String response) {
							try {
								JSONObject jsonObject1 = new JSONObject(response);
								access_token = jsonObject1.getString("access_token");
								expires_in = jsonObject1.getInt("expires_in");
								refresh_token = jsonObject1.getString("refresh_token");
								openid = jsonObject1.getString("openid");
								scope = jsonObject1.getString("scope");

							} catch (JSONException e) {
								//
								e.printStackTrace();
							}
						}

						// ��ȡaccess_token��������ݿ��ѯ�Ƿ���openid���û�
						private boolean queryUser(User user) {
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
								JDBCUtil.close(rs, JDBCUtil.getPs(),
										JDBCUtil.getConnection());
							}
							return bb;
						}

						

					});
					
					
					
					
					
					
					
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
					Long userId = Long.valueOf(jsonObject1.getString("userId"));
					Long seedId = Long.valueOf(jsonObject1.getString("seedId"));
					int seedNumber = Integer.valueOf(jsonObject1.getString("seedNumber"));
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
					Long userId = Long.valueOf(jsonObject1.getString("userId"));
					
					
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
					Long userId = Long.valueOf(jsonObject1.getString("userId"));
					Long seedId = Long.valueOf(jsonObject1.getString("seedId"));
					Long landId = Long.valueOf(jsonObject1.getString("landId"));

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
					Long userId = Long.valueOf(jsonObject1.getString("userId"));
					Long landId = Long.valueOf(jsonObject1.getString("landId"));

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
					Long userId = Long.valueOf(jsonObject1.getString("userId"));
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
					Long userId = Long.valueOf(jsonObject1.getString("userId"));
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
//			try {
//				writer.close();
//				buff.close();
//				socket.close();
//			} catch (Exception e) {
//
//			}
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
