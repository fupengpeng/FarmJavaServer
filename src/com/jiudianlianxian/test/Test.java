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
 * @Description: �ӿڲ���
 * @Company: �����ŵ�������Ϣ�������޹�˾
 * @ProjectName: FarmJavaServer
 * @author fupengpeng
 * @date 2017��11��2�� ����4:43:02
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
		System.out.println("��ȡ�ַ����ĳ���    "  + cString.length());    //13
		System.out.println("��ĸ'o'���ַ����г��γ��ֵ�λ��   " + cString.indexOf("a"));    //4
		
		String[] bStrings = bString.split("fff",2);  // ����1�Ƿָ���ţ�������ʲô�ָҲ����ʹ��������ʽ������2ָ���ָ�ɼ����ַ���
		System.out.println(bStrings[0]);
		for (String string : bStrings) {
		    System.out.println("�ָ����ַ���--�޶�����=" + string);
		}
		
	}
	private static BlockingQueue<String> msgQueue = new ArrayBlockingQueue<String>(
			20); // �����Ϣ�Ķ���
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
			} else if ("shop".equals(info)) { // ��ȡ����������Ϣ----�̵�
				
				shop(jdbcService);

			} else if ("buy".equals(info)) { // ��������
				buy(jdbcService);

			} else if ("getlandmsg".equals(info)) { // ��ȡ������Ϣ
				getlandmsg(jdbcService);

			} else if ("plant".equals(info)) { // ��ֲ
				plant(jdbcService);

			} else if ("harvest".equals(info)) { // �ջ�
				harvest(jdbcService);

			} else if ("warehouse".equals(info)) { // �򿪲ֿ� �ջ�Ĺ�ʵ
				warehouse(jdbcService);

			} else if ("parcel".equals(info)) { // ���� �ѹ��������
				parcel(jdbcService);

			} else if ("sell".equals(info)) { // ���۹�ʵ
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
		// ����ǵ�¼�������ȡ����¼���û��������룬�������ݿ��ѯ���ݣ��Ƿ��û�����������ȡ�������ȷ���򷵻ظ��û���¼�ɹ�����ʾ
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
							// �����������󵽵�openid��ѯ���ݿ⣬��ȡ�û���Ϣ��������Ϣ

							if (queryUser(user,
									unionID.getOpenid())) {
								// ��ȡ���ؼ���
								GetLandMsgResultData getLandMsgResultData = jdbcService
										.getLandMsg(user
												.getUserId());
								// �������ؼ��ϣ���ȡ��ֲ�������ϵ���������
								LoginResultData loginResultData = jdbcService
										.getLoginResultData(getLandMsgResultData
												.getLands());
								// �������ݣ����ظ��ͻ���
								loginResultData
										.setUser(user);

								loginResult
										.setCode("1");
								loginResult
										.setLoginResponseData(loginResultData);
								String jsonObject = com.alibaba.fastjson.JSONObject
										.toJSONString(loginResult);
								Date date1 = new Date();
								System.out.println("����ʱ�䣺" + date1.getHours()
										+ " ʱ   " + date1.getMinutes() + " ��    "
										+ date1.getSeconds() + " ��        "  + date1.getTime());
								System.out
										.println("jsonObject  = "
												+ jsonObject);
								pushMsg(jsonObject);

							} else {
								// û�����ݣ����ݻ�ȡ����openidȥ΢�ŷ�������ȡ�û�����
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
												// �ж����󵽵������Ƿ�Ϊ��
												if (respose != null) {
													// ��Ϊ�գ����н������������������ݱ��浽�������ݿ���ȥ
													analysisInsertData(
															user,
															respose);

													// ��ȡ���ؼ���
													GetLandMsgResultData getLandMsgResultData = jdbcService
															.getLandMsg(user
																	.getUserId());
													// �������ؼ��ϣ���ȡ��ֲ�������ϵ���������
													LoginResultData loginResultData = jdbcService
															.getLoginResultData(getLandMsgResultData
																	.getLands());
													// �������ݣ����ظ��ͻ���
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
															.println("��ȡ���û���Ϣ��jsonΪ��  = "
																	+ user);
												}
											}

											@Override
											public void onError(
													Exception e) {
												// �����쳣
												System.out
														.println(" ��ȡ���û���Ϣ����������ʧ�� �����ͻ�����ʾ = "
																+ user);

											}

											// ����΢���״ε�¼��ȡ�����û����ݣ������뵽�������ݿ�
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
								// ��������

								// ��������

								// ��������

							}
						} else {
							System.out
									.println("response  ��������Ϊ��");
						}
					}

					@Override
					public void onError(Exception e) {
						// TODO
						System.out
								.println("��ȡassess_token��������ʧ��  ==  "
										+ e);
					}
				});
	}




	private void shop(final JDBCService jdbcService) {
		String jsonObject;
		//  shop".equals(info)) { // ��ȡ����������Ϣ----�̵�
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
	}




	private void buy(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		// ��������������޸��û������������ͽ������           buy      // ��������
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
	}




	private void planttime(final JDBCService jdbcService) throws JSONException {
		String jsonObject;
		//  planttime   ��ֲʱ��
		Long userId = Long.valueOf(json
				.getString("userId"));
		Long landId = Long.valueOf(json
				.getString("landId"));
		// ��������ʱ��
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
		// ���ݴ����ʵid��ѯ��ʵʣ���������жϲ�ѯ���������Ƿ���ڴ�������������ڵĻ���ɾ�����ݿ��ʵ���ı��û����ý�ң����ظ��ͻ��˽�ң��ͳ��۹�ʵ��Ϣ    
		// sell   // ���۹�ʵ
		
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
		// ��ѯ��������������Ϣ         parcel        // ���� �ѹ��������
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
		// ��ѯ��ʵ������Ϣ          warehouse �򿪲ֿ� �ջ�Ĺ�ʵ
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
		// �޸�����״̬������״̬�����ɹ�ʵ�޸Ĺ�ʵ����  harvest  �ջ�
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
		// �޸���������������״̬������״̬�����������Ĺ��̼�ء���ʱ���ͻ��˷�����Ϣ         plant
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
		// ��ѯ����������Ϣ    getlandmsg
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
	 * @Description: ��ȡaccess_token��������ݿ��ѯ�Ƿ���openid���û�
	 * @param user
	 * @return
	 */
	private boolean queryUser(User user,String openid){
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
}
