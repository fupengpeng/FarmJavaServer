package com.jiudiannnnnnn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.Test;

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
import com.jiudianlianxian.util.SqlHelper;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;

public class SqlTest {
	JDBCService jdbcService = new JDBCService();
	String info = "------";
	String jsonObject = "";

	JSONObject jsonObject1;
	String access_token = null;
	int expires_in = 0;
	String refresh_token = null;
	String openid = "oVVdbwvclc7NjI2xjhUxE-Gq_daU";
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

	public static void main(String[] args) {
		SqlTest sqlTest = new SqlTest();
		// sqlTest.buy();
		// sqlTest.plant();
		// sqlTest.login();
		// sqlTest.harvest();
		// sqlTest.warehouse();
		// sqlTest.parcel();
		// sqlTest.sell();
//		sqlTest.test14();
		sqlTest.planttime();

	}

	// ��ʱ���ͻ��̷��͹�ʵ�������Ϣ
	public void timing() {
		// �ж��û��Ƿ����ӣ�
		// �����û�id������״̬��ѯ���ݿ⣬��ȡ���ӳ�����Ϣ
		// �����ж������Ƿ���죬�������ͣ�δ�������ù�
	}
	
	public void planttime(){
		Long userId = 1L;
		Long landId = 2L;
		System.out.println("---------------" + new Date().getTime());
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
	private void furitRipe(User user) {
		String jsonObject;
		// 1.��ѯfarm_seed����ȡseedStateΪ3�����Ӽ���

		// 2.�������Ӽ��ϵõ�������Ϣ
		List<RipeMessage> ripeMessages = new ArrayList<RipeMessage>();
		String sql = "select * from farm_seed where seedState=3 and userId="
				+ user.getUserId();
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
			System.out.println("plantTime + growthTime - 3600000 = "
					+ (plantTime + growthTime - 3600000) + "  "
					+ new Date((plantTime + growthTime - 3600000)));

			System.out.println("currentTime = " + currentTime + "  "
					+ new Date(currentTime));
			if ((plantTime + growthTime - 3600000) >= currentTime
					&& currentTime >= (plantTime + growthTime + 3600000)) {
				Long landId = null;
				String sql1 = "select * from land_seed where seedId="
						+ ripeMessage.getSeedId();
				System.out.println("sql1 = " + sql1);
				ResultSet rs1 = JDBCUtil.executeQuery(sql);
				try {
					while (rs1.next()) {
						landId = rs1.getLong(1);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs1, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
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

			} else {
				// ��ʵΪ����
				fruitRipeResult.setCode("��ʵδ����");
				jsonObject = com.alibaba.fastjson.JSONObject
						.toJSONString(fruitRipeResult);
				System.out.println("----jsonObject  = " + jsonObject);
				pushMsg(jsonObject);
			}
		}
	}

	public void sell() {
		// ���ݴ����ʵid��ѯ��ʵʣ���������жϲ�ѯ���������Ƿ���ڴ�������������ڵĻ���ɾ�����ݿ��ʵ���ı��û����ý�ң����ظ��ͻ��˽�ң��ͳ��۹�ʵ��Ϣ
		Long userId = (long) 1;
		Long fruitId = (long) 1;
		Long fruitNumber = (long) 5;
		SellFruitResultData sellFruitResultData = jdbcService.sellFruit(userId,
				fruitId, fruitNumber);
		Date date = new Date();
		System.out.println("����ʱ�䣺" + date.getHours() + " ʱ   "
				+ date.getMinutes() + " ��    " + date.getSeconds() + " �� ");
		SellFruitResult sellFruitResult = new SellFruitResult();
		sellFruitResult.setInfo(info);
		if (sellFruitResultData != null) {
			sellFruitResult.setCode("1");
			sellFruitResult.setSellFruitResultData(sellFruitResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(sellFruitResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			sellFruitResult.setCode("0");
			sellFruitResult.setSellFruitResultData(sellFruitResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(sellFruitResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void parcel() {

		// ��ѯ��������������Ϣ
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

	public void warehouse() {

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
	}

	public void harvest() {
		// �޸�����״̬������״̬�����ɹ�ʵ�޸Ĺ�ʵ����
		Long userId = (long) 1;
		Long landId = (long) 1;

		HarvestResult harvestResult = new HarvestResult();
		HarvestResultData harvestResultData = jdbcService.harvest(landId,
				userId);

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
	}

	private boolean queryUser(User user, String openid) {
		// ��ѯ���ݿ�
		// �����û��Ƿ��Ѿ�ʹ�ô�΢���˺ŵ�½������¼����ֱ��ʹ�����ݿ����ݣ�����������΢�ŷ���������ȡ�µ��û�����
		boolean bb = false;
		String sql = "select * from farm_user where openid='" + openid + "'";
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

	public void plant() {

		// �޸���������������״̬������״̬�����������Ĺ��̼�ء���ʱ���ͻ��˷�����Ϣ
		// userId seedId landId
		Long userId = (long) 1;
		Long seedId = (long) 104;
		Long landId = (long) 3;

		PlantResult plantResult = new PlantResult();
		PlantResultData plantResultData = jdbcService.plant(userId, seedId,
				landId);
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
	}

	public void getlandmsg() {
		// ��ѯ����������Ϣ
		Long userId = (long) 1;
		GetLandMsgResultData getLandMsgResultData = jdbcService
				.getLandMsg(userId);
		GetLandMsgResult getLandMsgResult = new GetLandMsgResult();
		getLandMsgResult.setInfo(info);
		if (getLandMsgResultData != null) {
			getLandMsgResult.setCode("1");
			getLandMsgResult.setGetLandMsgResultData(getLandMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getLandMsgResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		} else {
			getLandMsgResult.setCode("0");
			getLandMsgResult.setGetLandMsgResultData(getLandMsgResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(getLandMsgResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}

	}

	public void buy() {
		// ��������������޸��û������������ͽ������
		Long userId = (long) 2;
		Long seedId = (long) 50;
		int seedNumber = 2;
		// ����id�����û���Ϣ�����������������ͽ������
		// 1.��ѯ������Ϣ������۸�
		// 2.��ѯ�û������Ϣ������Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
		// 3.��ҹ������޸��û���������Ϣ�ͽ����Ϣ
		// 4.���µ��û����ݷ��ظ��û�
		BuySeedResultData buySeedResultData = jdbcService.getBuySeedResultData(
				userId, seedId, seedNumber);
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
			System.out.println("jsonObject--  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void test17() {
		// ��ѯ���ݿ������б���������
		SeedMsgAllResultData seedMsgAllResultData = jdbcService.getSeedMsgAll();
		SeedMsgAllResult seedMsgAllResult = new SeedMsgAllResult();
		seedMsgAllResult.setInfo(info);
		if (seedMsgAllResultData != null) {
			seedMsgAllResult.setCode("1");
			seedMsgAllResult.setSeedMsgAllResultData(seedMsgAllResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(seedMsgAllResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);

		} else {
			seedMsgAllResult.setCode("0");
			seedMsgAllResult.setSeedMsgAllResultData(seedMsgAllResultData);
			jsonObject = com.alibaba.fastjson.JSONObject
					.toJSONString(seedMsgAllResult);
			System.out.println("jsonObject  = " + jsonObject);
			pushMsg(jsonObject);
		}
	}

	public void pushMsg(String str) {
		System.out.println("str  -----------------------==  " + str);
	}

	public void login() {

		// ����ǵ�¼�������ȡ����¼���û��������룬�������ݿ��ѯ���ݣ��Ƿ��û�����������ȡ�������ȷ���򷵻ظ��û���¼�ɹ�����ʾ
		String code = "data";
		String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
				+ code + "&grant_type=authorization_code";
		System.out.println("urlStr  = " + urlStr);

		UnionID unionID = new UnionID();
		unionID.setOpenid("zhangsan");
		LoginResult loginResult = new LoginResult();
		loginResult.setInfo(info);
		System.out.println("��ȡ�����û���openid     Access_token = "
				+ unionID.getAccess_token() + "    getOpenid = "
				+ unionID.getOpenid());
		User user = new User();
		if (queryUser(user, unionID.getOpenid())) {
			System.out.println("���ݿ��в�ѯ����openid�û������ݣ����ظ��ͻ���");

			resultLoginData(loginResult, user);

		} else {
			System.out.println("���ݿ�δ��ѯ����openid�û������ݣ������������󣬻�ȡ��openid�û�������");

			// û�����ݣ����ݻ�ȡ����openidȥ΢�ŷ�������ȡ�û�����
			String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
					+ unionID.getAccess_token() + "&openid="
					+ unionID.getOpenid();
			System.out.println("δ�����ݿ��ȡ�����ݣ���������������ݻ�ȡ----url  = " + url);
			HttpUtil.requestData(url, new HttpCallBackListener() {
				@Override
				public void onFinish(String respose) {
					// �ж����󵽵������Ƿ�Ϊ��
					if (respose != null) {
						System.out.println("��ȡ���û���Ϣjson  = " + respose);
						// ��Ϊ�գ����н������������������ݱ��浽�������ݿ���ȥ
						analysisInsertData(user, respose);
						// ����ȡ�������ݷ��ظ��ͻ���
						resultLoginData(loginResult, user);
						System.out.println("���������û�����  = " + user);
					} else {
						System.out.println("��ȡ���û���Ϣ��jsonΪ��  = " + user);
					}
				}

				@Override
				public void onError(Exception e) {
					// �����쳣
					System.out.println(" ��ȡ���û���Ϣ����������ʧ�� �����ͻ�����ʾ = " + user);

				}

				// ����΢���״ε�¼��ȡ�����û����ݣ������뵽�������ݿ�
				private void analysisInsertData(User user, String respose) {
					try {
						JSONObject jsonObject2 = new JSONObject(respose);
						user.setOpenid(jsonObject2.getString("openid"));
						user.setUserNickName(jsonObject2.getString("nickname"));
						System.out.println(jsonObject2.getString("nickname"));
						user.setSex(jsonObject2.getInt("sex"));
						user.setProvince(jsonObject2.getString("province"));
						user.setCity(jsonObject2.getString("city"));
						user.setCountry(jsonObject2.getString("country"));
						user.setUserImage(jsonObject2.getString("headimgurl"));
						JSONArray jsonArray = jsonObject2
								.getJSONArray("privilege");
						String[] privilege = null;
						for (int i = 0; i < jsonArray.length(); i++) {
							privilege[i] = jsonArray.getString(i);
						}
						user.setPrivilege(privilege);
						user.setUnionid(jsonObject2.getString("unionid"));

					} catch (JSONException e) {
						//
						e.printStackTrace();
					}
					System.out.println("nickname = " + user.getUserNickName());

					Long userGold = (long) 5000;
					Long userExperience = (long) 500;
					String sql3 = "INSERT INTO farm_user("
							+ "userNickName,userImage,userGold,openid,userExperience)"
							+ "VALUES('" + user.getUserNickName() + "','"
							+ user.getUserImage() + "'," + userGold + ",'"
							+ user.getOpenid() + "'," + userExperience + ") ";
					System.out.println("sql3====" + sql3);

					try {
						user.setUserId(JDBCUtil.executeUpdateGetId(sql3));
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

	}

	private void resultLoginData(LoginResult loginResult, User user) {
		List<Land> lands = new ArrayList<Land>();
		// for (int i = 0; i < 10000; i++) {

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

		// }
		// ����lands����ȡ��land�����landState��
		List<LandData> landDatas = new ArrayList<LandData>();
		for (Land land : lands) {
			LandData landData = new LandData();
			landData.setLandId(land.getLandId());
			landData.setLandName(land.getLandName());
			landData.setLandState(land.getLandState());
			if (land.getLandState().equals("3")) {
				// ״̬��3����ѯland_seed����ȡseedId
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
				String sql2 = "select * from farm_seed where seedId=" + seedId;

				System.out.println("sql2 = " + sql2);
				ResultSet rs2 = JDBCUtil.executeQuery(sql2);
				try {
					while (rs2.next()) {

						landData.setSeedName(rs2.getString(2));
						System.out.println("�û�������Ϣ  landData.getSeedName() = "
								+ landData.getSeedName());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					JDBCUtil.close(rs2, JDBCUtil.getPs(),
							JDBCUtil.getConnection());
				}

			} else {
				landData.setSeedName("");
			}
			landDatas.add(landData);
		}

		LoginResultData loginResponseData = new LoginResultData();
		loginResponseData.setUser(user);
		loginResponseData.setLandDatas(landDatas);
		String loginResultData = com.alibaba.fastjson.JSONObject
				.toJSONString(loginResponseData);
		System.out.println("loginResultData------------  = " + loginResultData);

		loginResult.setCode("1");
		loginResult.setLoginResponseData(loginResponseData);
		String jsonObject = com.alibaba.fastjson.JSONObject
				.toJSONString(loginResult);
		System.out.println("jsonObject  = " + jsonObject);
		pushMsg(jsonObject);
	}

	public void test13() {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		long between = 0;
		try {
			java.util.Date begin = dfs.parse("2009-07-10 10:22:21.214");
			java.util.Date end = dfs.parse("2009-07-20 11:24:49.145");
			between = (end.getTime() - begin.getTime());// �õ����ߵĺ�����
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		long day = between / (24 * 60 * 60 * 1000);
		long hour = (between / (60 * 60 * 1000) - day * 24);
		long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
				- min * 60 * 1000 - s * 1000);
		System.out.println(day + "��" + hour + "Сʱ" + min + "��" + s + "��" + ms
				+ "����");
	}

	public void test14() {
		User user = new User();
		user.setUserId(7L);
		Date date1 = new Date();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				furitRipe(user);
				System.out.println("ִ������ ");

			}
		};
		long timestamp = 5000;
		timer.schedule(timerTask, date1, timestamp);
	}

	public void test10() {
		Date date = new Date();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// System.out.println("ִ������ ");

			}
		};
		Long a = date.getTime() + 10000L;
		Date date1 = new Date(a);
		System.out.println("��ǰʱ��  = " + date.getTime() + "   ��ǰdate =  " + date
				+ "    ���Ӻ� = " + date1.getTime() + "   ���Ӻ�date1 = " + date1);

		long timestamp = 1000;
		timer.schedule(timerTask, date, timestamp);

	}

	/**
	 * ��ָ��URL����GET����������
	 * 
	 * @param url
	 *            ���������URL
	 * @param param
	 *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
	 * @return URL ������Զ����Դ����Ӧ���
	 */
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			URLConnection connection = realUrl.openConnection();
			// // ����ͨ�õ���������
			// connection.setRequestProperty("accept", "*/*");
			// connection.setRequestProperty("connection", "Keep-Alive");
			// connection.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// ����ʵ�ʵ�����
			// connection.connect();
			// ��ȡ������Ӧͷ�ֶ�
			// Map<String, List<String>> map = connection.getHeaderFields();
			// // �������е���Ӧͷ�ֶ�
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// ���� BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;

			}

		} catch (Exception e) {
			System.out.println("����GET��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر�������
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public void test09() {
		String uri = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
		StringBuilder builder = null;
		String json_access_token = null;
		try {
			URL url = new URL(uri);// ����в���������ַ��Я������
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			builder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			br.close();
			isr.close();
			is.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json_access_token = builder.toString();
		System.out.println(builder.toString());

		JSONObject jsonObject1;
		JSONObject data = null;
		try {
			jsonObject1 = new JSONObject(json_access_token);
			info = jsonObject1.getString("info");
			data = jsonObject1.getJSONObject("data");
			System.out.println("data  = " + data.toString());
			System.out.println("username = " + data.getString("username"));
			System.out.println("sex = " + data.getString("sex"));
			System.out.println("age = " + data.getString("age"));

		} catch (JSONException e) {
			//
			e.printStackTrace();
		}

	}

}
