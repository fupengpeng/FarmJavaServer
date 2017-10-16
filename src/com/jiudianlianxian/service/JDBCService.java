package com.jiudianlianxian.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.RepaintManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jiudianlianxian.bean.FruitRipeResult;
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
import com.jiudianlianxian.domain.Seed;
import com.jiudianlianxian.domain.User;
import com.jiudianlianxian.util.JDBCUtil;
import com.jiudianlianxian.utils.HttpCallBackListener;
import com.jiudianlianxian.utils.HttpUtil;

/**
 * 
 * Title: UsersService Description: User�����Ĕ�����̎��� Company: �����ŵ�������Ϣ�������޹�˾
 * ProjectName: UsersManager
 * 
 * @author fupengpeng
 * @date 2017��7��19�� ����9:17:44
 *
 */
public class JDBCService {

	/**
	 * 
	 * @Description: �򿪲ֿ�
	 * @param userId
	 * @return
	 */
	public OpenWarehouseResultData openWarehouse(Long userId) {
		OpenWarehouseResultData openWarehouseResultData = new OpenWarehouseResultData();

		return openWarehouseResultData;
	}

	/**
	 * 
	 * @Description: �ջ�
	 * @param landId
	 * @param userId
	 * @return
	 */
	public HarvestResultData harvest(Long landId, Long userId) {
		HarvestResultData harvestResultData = new HarvestResultData();

		// 1.���ݴ�������id��ѯland_seed����ȡseedId
		Seed seed = new Seed();
		String sql1 = "select * from land_seed where landId=" + landId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seed.setSeedId(rs1.getLong(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.����seedId��ѯfarm_seed����ȡseed����(��Ϣ)
		String sql2 = "select * from farm_seed where seedId="
				+ seed.getSeedId();
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				seed.setSeedName(rs2.getString(2));
				seed.setSeedGrowthTime(rs2.getLong(4));
				seed.setSeedExperience(rs2.getLong(7));
				seed.setSeedYield(rs2.getLong(8));
				seed.setSeedFruitSellingPrice(rs2.getLong(9));
				seed.setSeedImage(rs2.getString(11));
				seed.setSeedPlantTime(rs2.getLong(14));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 3.���ݴ��� userId��ѯfarm_user����ȡ�û�����ֵ
		Long userExperience = null;
		String sql3 = "select * from farm_user where userId=" + userId;
		System.out.println("sql3 = " + sql3);
		ResultSet rs3 = JDBCUtil.executeQuery(sql3);
		try {
			while (rs3.next()) {
				userExperience = rs3.getLong(6);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs3, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 4.�жϹ�ʵ�Ƿ����
		if (new Date().getTime() >= (seed.getSeedPlantTime() + seed
				.getSeedGrowthTime())) {
			// ��ʵ�ѳ���
			// 4-1.ɾ��land_seed�������������ӹ�ϵ
			String deleteLandSeedRelation = "delete from land_seed WHERE landId="
					+ landId;
			try {
				JDBCUtil.executeUpdate(deleteLandSeedRelation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 4-2.ɾ�����ӱ�����ֲ�������ϵ�����
			String deleteSeedStateThree = "delete from farm_seed WHERE seedId="
					+ seed.getSeedId();
			// 4-3.�ı�����״̬
			String updateLandState = "update farm_land set landState='2' WHERE landId="
					+ landId;
			// 4-4.�ı��û�����ֵ
			String updateUserExperience = "update farm_user set userExperience="
					+ (userExperience + seed.getSeedExperience())
					+ " WHERE userId=" + userId;
			// 4-5.�ڹ�ʵ�б���ӹ�ʵ
			String insertFruit = "insert into farm_fruit (fruitName,fruitNumber,fruitSellingPrice,fruitImage,userId) VALUES ('"
					+ seed.getSeedName()
					+ "',"
					+ seed.getSeedYield()
					+ ","
					+ seed.getSeedFruitSellingPrice()
					+ ",'"
					+ seed.getSeedImage() + "'," + userId + ")";
			String[] sql = { deleteSeedStateThree, updateLandState,
					updateUserExperience, insertFruit };
			try {
				JDBCUtil.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}

			harvestResultData.setLandId(landId);
			harvestResultData.setLandState("2");
			harvestResultData.setSeedExperience(seed.getSeedExperience());
			harvestResultData.setUserId(userId);
			harvestResultData.setUserExperience(userExperience
					+ seed.getSeedExperience());
			harvestResultData.setFruitNumber(seed.getSeedYield());

			return harvestResultData;

		} else {
			// ��ʵδ����
			return harvestResultData;
		}

	}

	/**
	 * 
	 * @Description: ��ֲ
	 * @param userId
	 * @param seedId
	 * @param landId
	 */
	public PlantResultData plant(Long userId, Long seedId, Long landId) {
		PlantResultData plantResultData = new PlantResultData();
		String landState = "1";
		// 1.��ѯ�����б���ȡ��������Ϣ
		Seed seed = new Seed(); // ����seedId��������Ϣ
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seed.setSeedId(rs1.getLong(1));
				seed.setSeedNumber(rs1.getInt(12));
				System.out.println("����seedId����������    seedNumber = "
						+ seed.getSeedNumber());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 2.�жϴ����������Ƿ����0������ͻ���ʾ
		if (seed.getSeedNumber() < 0) { // û�д����ӣ���Ҫ����
			// ���ؿյ����ݣ�û�д�������Ҫ����
			return plantResultData;
		} else {
			// 3.��ѯ�����б���ȡ������״̬
			Land land = new Land(); // ����landId��������Ϣ
			String sql2 = "select * from farm_land where landId=" + landId;
			System.out.println("sql2 = " + sql2);
			ResultSet rs2 = JDBCUtil.executeQuery(sql2);
			try {
				while (rs2.next()) {
					land.setLandId(rs2.getLong(1));
					land.setLandName(rs2.getString(2));
					land.setLandState(rs2.getString(3));
					System.out.println("����landId������״̬    landState = "
							+ land.getLandState());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}

			// 4.�жϴ�����״̬�Ƿ�Ϊ2���ѿ��ѣ�δ��ֲ��������ͻ���ʾ
			if (landState.equals(land.getLandState())
					|| "3".equals(land.getLandState())) {
				// ����״̬Ϊ1����3������������ֲ����δ���ѣ���ʾ�û���������ֲ��
				return plantResultData;
			} else {
				// 5.��ֲ
				// 5-1.�ı䱳������������
				// 5-1-1.���ٴ���seedId��������
				String sql4 = "UPDATE farm_seed SET " + "seedNumber="
						+ (seed.getSeedNumber() - 1) + " " + "WHERE seedId="
						+ seedId + ";";
				System.out.println("�ı�idΪlandId������״̬        landState =  "
						+ land.getLandState());
				System.out.println("sql4 = " + sql4);
				try {
					JDBCUtil.executeUpdate(sql4);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-1-2.����û�Ϊ����userId��������ΪseedName������״̬Ϊ3�������Լ���ֲʱ�䣬����ȡ����seedId
				Long seedPlantTime = new Date().getTime();
				String sql5 = "INSERT INTO farm_seed("
						+ "seedName,seedState,seedGrowthTime,seedPlantTime,seedSellingPrice,seedImage,"
						+ "seedNumber)" + "VALUES('seedname','" + 3 + "',"
						+ seed.getSeedGrowthTime() + "," + seedPlantTime + ","
						+ seed.getSeedSellingPrice() + ",'"
						+ seed.getSeedImage() + "'," + 1 + ") ";
				System.out.println("sql5====" + sql5);
				long lastid = 0;
				try {
					lastid = JDBCUtil.executeUpdateGetId(sql5);
					System.out.println("�ող������ݵ�id ---  11 = " + lastid);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 5-1-3.��land_seed������ӻ�ȡ����seedId���Ӻʹ����landId���ع�ϵ
				boolean b = true;
				String sql = "INSERT INTO land_seed(" + "landId,seedId) "
						+ "VALUES(" + landId + "," + lastid + ") ;";
				System.out.println("sql====" + sql);
				try {
					JDBCUtil.executeUpdate(sql);
				} catch (Exception e) {
					b = false;
					e.printStackTrace();
				}
				// 5-2.�ı�����״̬
				String sql3 = "UPDATE farm_land SET " + "landState='"
						+ landState + "' " + "WHERE landId=" + landId + ";";
				System.out.println("�ı�idΪlandId������״̬        landState =  "
						+ land.getLandState());
				System.out.println("sql4 = " + sql3);
				try {
					JDBCUtil.executeUpdate(sql3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 5-3.�����������������ʵʱ���ͻ�������Ϣ��ÿ��1���Ӹ��ͻ��˷���һ����Ϣ
				// 5-4.���������ɹ�ʵ����ʾ�ͻ���ȡ��ʵ
				plantResultData.setSeedNumber(seed.getSeedNumber() - 1);
				land.setLandState("1");
				plantResultData.setLand(land);
				return plantResultData;
			}

		}

	}

	/**
	 * 
	 * @Description: �����û�id��ȡ�û�������Ϣ
	 * @param userId
	 * @return
	 */
	public GetLandMsgResultData getLandMsg(Long userId) {
		GetLandMsgResultData getLandMsgResultData = new GetLandMsgResultData();

		// ��ѯ������Ϣ���ظ��ͻ���
		String sql = "select * from farm_land where userId=" + userId;
		List<Land> lands = new ArrayList<Land>();
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		try {
			while (rs.next()) {
				Land land = new Land();
				land.setLandId(rs.getLong(1));
				land.setLandName(rs.getString(2));
				land.setLandState(rs.getString(3));
				lands.add(land);
				System.out.println("�û�������Ϣ    lands = " + lands);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		getLandMsgResultData.setLands(lands);
		return getLandMsgResultData;
	}

	/**
	 * 
	 * @Description: �������ӣ������ع�����û�����
	 * @param userId
	 * @param seedId
	 * @param seedNumber
	 * @return
	 */
	public BuySeedResultData getBuySeedResultData(String userId, String seedId,
			String seedNumber) {
		BuySeedResultData buySeedResultData = new BuySeedResultData();
		boolean b = true;
		// 1.��ѯ������Ϣ�����㹺��������
		int seedTotalPrices = 0; // �����ܼ�
		Seed seed = new Seed(); // ��Ҫ�����������Ϣ
		// ��ѯ���ӵ��ۣ����㹺������������������
		String sql1 = "select * from farm_seed where seedId=" + seedId;
		System.out.println("sql1 = " + sql1);
		ResultSet rs1 = JDBCUtil.executeQuery(sql1);
		try {
			while (rs1.next()) {
				seedTotalPrices = (int) (Integer.parseInt(seedNumber) * (rs1
						.getInt(5)));
				seed.setSeedName(rs1.getString(2));
				seed.setSeedGrowthTime(rs1.getLong(4));
				seed.setSeedBuyPrice(rs1.getLong(5));
				seed.setSeedSellingPrice(rs1.getLong(6));
				seed.setSeedExperience(rs1.getLong(7));
				seed.setSeedYield(rs1.getLong(8));
				seed.setSeedFruitSellingPrice(rs1.getLong(9));
				seed.setSeedType(rs1.getLong(10));
				seed.setSeedImage(rs1.getString(11));
				System.out.println("��������������    seedTotalPrices = "
						+ seedTotalPrices);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs1, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.��ѯ�û������Ϣ��
		int userGold = 0; // �û����еĽ������
		String sql2 = "select * from farm_user where userId=" + userId;
		System.out.println("sql2 = " + sql2);
		ResultSet rs2 = JDBCUtil.executeQuery(sql2);
		try {
			while (rs2.next()) {
				userGold = rs2.getInt(4);
				System.out.println("�û������    price2 = " + userGold);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs2, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 3. �ж��û�ʣ�����Ƿ������ӣ��粻��ֱ�ӷ�����ʾ�û�
		if (userGold > seedTotalPrices) {
			// 3.��ҹ������޸��û���������Ϣ�ͽ����Ϣ
			// 3-1.�޸��û��Ľ������
			String sql4 = "UPDATE farm_user SET " + "userGold='"
					+ (userGold - seedTotalPrices) + "' " + "WHERE userId='"
					+ userId + "';";
			System.out.println("�����û����ʣ���  price2-price1 = "
					+ (userGold - seedTotalPrices));
			System.out.println("sql4 = " + sql4);
			try {
				JDBCUtil.executeUpdate(sql4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 3-2.��ѯ���ӱ��û��Ƿ��������ΪseedName������
			Long seedIdStateTwo = null; // �ѹ��������id
			Long seedNumberStateTwo = null; // �ѹ������������
			String sql3 = "select * from farm_seed where userId=" + userId
					+ " and seedState='" + 2 + "' and seedName='"
					+ seed.getSeedName() + "'";
			System.out.println("sql3 = " + sql3);
			b = false;
			ResultSet rs3 = JDBCUtil.executeQuery(sql3);
			try {
				while (rs3.next()) {
					b = true;
					rs3.getInt(3);
					seedIdStateTwo = rs3.getLong(1);
					seedNumberStateTwo = rs3.getLong(12);
					System.out.println("��ѯ���û��Ƿ��и����ӣ�rs3.getInt(3) = "
							+ rs3.getInt(3) + "seedIdStateTwo = "
							+ seedIdStateTwo + "seedNumberStateTwo = "
							+ seedNumberStateTwo);
				}
			} catch (SQLException e) {
				b = false;
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs3, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}

			if (b) { // 3-2-1.�û����������ΪseedName������
				// �ı��û����еĸ���������

				String sql5 = "UPDATE farm_seed SET " + "seedNumber="
						+ (seedNumberStateTwo + seedNumber) + " WHERE seedId="
						+ seedIdStateTwo + ";";
				System.out.println("sql5 = " + sql5);
				try {
					JDBCUtil.executeUpdate(sql5);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// ���û����ع���ɹ�����Ϣ������
				// ���û���������
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			} else { // 3-2-2.�û�û�й��������ΪseedName�����ӣ�����Ӵ�����
				// 01.���û����������
				String sql7 = "insert into farm_seed ("
						+ "seedName,seedState,seedGrowthTime,"
						+ "seedBuyPrice,seedSellingPrice,seedExperience,"
						+ "seedYield,seedFruitSellingPrice,seedType,"
						+ "seedImage,seedNumber,userId" + ") values('"
						+ seed.getSeedName()
						+ "','"
						+ 2
						+ "',"
						+ seed.getSeedGrowthTime()
						+ ","
						+ seed.getSeedBuyPrice()
						+ ","
						+ seed.getSeedSellingPrice()
						+ ","
						+ seed.getSeedExperience()
						+ ","
						+ seed.getSeedYield()
						+ ","
						+ seed.getSeedFruitSellingPrice()
						+ ","
						+ seed.getSeedType()
						+ ",'"
						+ seed.getSeedImage()
						+ "'," + seedNumber + "," + userId + ")";
				System.out.println("sql7====" + sql7);

				// 02.��ӵ�������id
				long lastid = 0;
				try {
					lastid = JDBCUtil.executeUpdateGetId(sql7);
					System.out.println("�ող������ݵ�id ---  11 = " + lastid);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// ���û���������
				resultBuySeedResult(userId, buySeedResultData);
				return buySeedResultData;

			}
		} else {
			// 3.��Ҳ��������û���ʾ
			// ���û���������
			return buySeedResultData;

		}

	}

	/**
	 * 
	 * @Description: �������ӳɹ���ķ�����Ϣ
	 * @param userId
	 * @param buySeedResultData
	 */
	private void resultBuySeedResult(String userId,
			BuySeedResultData buySeedResultData) {
		// 1.��ѯ�û������Ϣ�����浽��������ʵ��
		String sql12 = "select * from farm_user where userId=" + userId;
		System.out.println("sql12 = " + sql12);
		ResultSet rs12 = JDBCUtil.executeQuery(sql12);
		long userGold = 0;
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs12.next()) {
				userGold = rs12.getLong(4);
				System.out.println("�û������    userGold = " + userGold);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs12, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}

		// 2.��ѯ�ѹ���������Ϣ�����浽��������ʵ����
		String sql = "select * from farm_seed where userId=" + userId
				+ " and seedState='2' ";
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		List<Seed> seeds = new ArrayList<Seed>();
		try {
			// ��ѯ���ݿ�,��ȡ����uid��Ӧ������

			while (rs.next()) {
				Seed seed = new Seed();
				seed.setSeedId(rs.getLong(1));
				seed.setSeedName(rs.getString(2));
				seed.setSeedState(rs.getString(3));
				seed.setSeedGrowthTime(rs.getLong(4));
				seed.setSeedBuyPrice(rs.getLong(5));
				seed.setSeedSellingPrice(rs.getLong(6));
				seed.setSeedExperience(rs.getLong(7));
				seed.setSeedYield(rs.getLong(8));
				seed.setSeedFruitSellingPrice(rs.getLong(9));
				seed.setSeedType(rs.getLong(10));
				seed.setSeedImage(rs.getString(11));

				seeds.add(seed);
			}
		} catch (SQLException e) {
			//
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		// 3.����ѯ�������ݱ��浽���ص�ʵ��������
		buySeedResultData.setUserGold(userGold);
		buySeedResultData.setUserSeeds(seeds);

	}

	/**
	 * 
	 * @Description: ��ȡ����������Ϣ
	 * @return
	 */
	public SeedMsgAllResultData getSeedMsgAll() {
		SeedMsgAllResultData seedMsgAllResultData = new SeedMsgAllResultData();
		List<Seed> seeds = new ArrayList<Seed>();
		for (int i = 0; i < 10000; i++) {
			Seed seed = new Seed();
			// ��ѯ���ݿ�,��ȡ״̬Ϊ 1 ����������
			String sql = "select * from farm_seed where seedState='1'";
			ResultSet rs = JDBCUtil.executeQuery(sql);
			try {
				while (rs.next()) {
					seed.setSeedId(rs.getLong(1));
					seed.setSeedName(rs.getString(2));
					seed.setSeedState(rs.getString(3));
					seed.setSeedGrowthTime(rs.getLong(4));
					seed.setSeedBuyPrice(rs.getLong(5));
					seed.setSeedSellingPrice(rs.getLong(6));
					seed.setSeedExperience(rs.getLong(7));
					seed.setSeedYield(rs.getLong(8));
					seed.setSeedFruitSellingPrice(rs.getLong(9));
					seed.setSeedType(rs.getLong(10));
					seed.setSeedImage(rs.getString(11));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			seeds.add(seed);
		}
		seedMsgAllResultData.setSeeds(seeds);
		return seedMsgAllResultData;
	}

	JSONObject jsonObject;
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
	 * 
	 * @Description: ΢�ŵ�¼�����������ݽ���
	 * @param json
	 */
	public void analysisGetAndRefreshAccess_tokenSuccess(String json) {
		try {
			jsonObject = new JSONObject(json);
			access_token = jsonObject.getString("access_token");
			expires_in = jsonObject.getInt("expires_in");
			refresh_token = jsonObject.getString("refresh_token");
			openid = jsonObject.getString("openid");
			scope = jsonObject.getString("scope");

		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: ΢�ŵ�¼�����������ݽ���
	 * @param json
	 */
	public void analysisDefeated(String json) {
		try {
			jsonObject = new JSONObject(json);
			errcode = jsonObject.getInt("errcode");
			errmsg = jsonObject.getString("errmsg");
		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: ΢�ŵ�¼�����������ݽ���
	 * @param json
	 */
	public void analysisGetUnionID(String json) {
		try {
			jsonObject = new JSONObject(json);
			openid = jsonObject.getString("openid");
			nickname = jsonObject.getString("nickname");
			sex = jsonObject.getInt("sex");
			province = jsonObject.getString("province");

			city = jsonObject.getString("city");
			country = jsonObject.getString("country");
			headimgurl = jsonObject.getString("headimgurl");
			jsonArray = jsonObject.getJSONArray("privilege");
			for (int i = 0; i < jsonArray.length(); i++) {
				privilege[i] = jsonArray.getString(i);
			}
			unionid = jsonObject.getString("unionid");

		} catch (JSONException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: ��¼�ж�
	 * @return
	 */
	public boolean login(String code) {
		boolean b = true;

		return b;
	}

	/**
	 * 
	 * @Description: ΢�ŵ�¼��������
	 * @param code
	 * @return
	 */
	public void WeiXinlogin(String code, User user) {
		System.out.println("����΢�ŵ�¼����WeiXinlogin");

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
				+ code + "&grant_type=authorization_code";
		
		HttpUtil.requestData(url, new HttpCallBackListener() {

			@Override
			public void onFinish(String respose) {

				// ��������
				if (respose != null) {
					System.out.println("��ȡaccess_token  json = " + respose);
					analysisGetAndRefreshAccess_tokenSuccess(respose);
					System.out.println("access_token = " + access_token);

					// ��ѯ���ݿ�
					// �����û��Ƿ��Ѿ�ʹ�ô�΢���˺ŵ�½������¼����ֱ��ʹ�����ݿ����ݣ�����������΢�ŷ���������ȡ�µ��û�����
					boolean bb = false;
					String sql = "select * from farm_user where openid='"
							+ openid + "'";
					System.out.println("sql = " + sql);
					ResultSet rs = JDBCUtil.executeQuery(sql);

					try {
						// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
						while (rs.next()) {
							System.out.println("�������ݿ��ѯ��������");
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

					if (bb) {
						System.out.println("���ݿ��ѯ���û��������ٴ�����ֱ�ӵõ��û� = " + user);
						
						

					} else {
						System.out.println("wenti   02  ----");
						String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
								+ access_token + "&openid=" + openid;
						HttpUtil.requestData(url, 
								new HttpCallBackListener() {
									@Override
									public void onFinish(String respose) {
										// ��������
										if (respose != null) {
											System.out
													.println("��ȡ���û���Ϣjson  = "
															+ respose);
											analysisGetUnionID(respose);
											System.out.println("nickname = "
													+ nickname);

											Long userGold = (long) 5000;
											Long userExperience = (long) 500;
											String sql3 = "INSERT INTO farm_user("
													+ "userNickName,userImage,userGold,openid,userExperience)"
													+ "VALUES('"
													+ nickname
													+ "','"
													+ headimgurl
													+ "',"
													+ userGold
													+ ",'"
													+ openid
													+ "',"
													+ userExperience
													+ ") ";
											System.out.println("sql3===="
													+ sql3);

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
											System.out.println("���������û�����  = "
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
		});

		System.out.println("�������ݣ������û�����---- = " + user);

	}

	public User weixin(String code) {
		
		
		
		
		
		
		System.out.println("����΢�ŵ�¼����WeiXinlogin");

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa8fb492572709521&secret=3117081dbe26f23ffbf84b5e96472f53&code="
				+ code + "&grant_type=authorization_code";
		User user = new User();
		
		
		HttpUtil.resquestData(url, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				if (response.toString() != null) {
					
					 System.out.println("��ȡaccess_token  json = " + response.toString());
					 analysisGetAndRefreshAccess_tokenSuccess(response.toString());
					 System.out.println("access_token = " + access_token);
					
					 // ��ѯ���ݿ�
					 // �����û��Ƿ��Ѿ�ʹ�ô�΢���˺ŵ�½������¼����ֱ��ʹ�����ݿ����ݣ�����������΢�ŷ���������ȡ�µ��û�����
					 boolean bb = false;
					 String sql = "select * from farm_user where openid='"
					 + openid + "'";
					 System.out.println("sql = " + sql);
					 ResultSet rs = JDBCUtil.executeQuery(sql);
					
					 try {
					 // ��ѯ���ݿ�,��ȡ����uid��Ӧ������
					 while (rs.next()) {
					 System.out.println("�������ݿ��ѯ��������");
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
					
					 if (bb) {
					 System.out.println("���ݿ��ѯ���û��������ٴ�����ֱ�ӵõ��û� = " + user);
					
					 } else {
					 System.out.println("���ݿ�δ��ѯ���û����ٴ�����ֱ�ӵõ��û� = " + user);
					
					 }
				}else {
					System.out.println("��������Ϊ��");
				}
				
				System.out.println("����ɹ�");
				
			}
			
			@Override
			public void onError(Exception e) {
				System.out.println("����ʧ��    == " + e);
				
			}
		});
					



		// // ��������
		// if (sb.toString() != null) {
		// System.out.println("��ȡaccess_token  json = " + sb.toString());
		// analysisGetAndRefreshAccess_tokenSuccess(sb.toString());
		// System.out.println("access_token = " + access_token);
		//
		// // ��ѯ���ݿ�
		// // �����û��Ƿ��Ѿ�ʹ�ô�΢���˺ŵ�½������¼����ֱ��ʹ�����ݿ����ݣ�����������΢�ŷ���������ȡ�µ��û�����
		// boolean bb = false;
		// String sql = "select * from farm_user where openid='"
		// + openid + "'";
		// System.out.println("sql = " + sql);
		// ResultSet rs = JDBCUtil.executeQuery(sql);
		//
		// try {
		// // ��ѯ���ݿ�,��ȡ����uid��Ӧ������
		// while (rs.next()) {
		// System.out.println("�������ݿ��ѯ��������");
		// user.setUserId(rs.getLong(1));
		// user.setUserNickName(rs.getString(2));
		// user.setUserImage(rs.getString(3));
		// user.setUserGold(rs.getLong(4));
		// user.setUserExperience(rs.getLong(6));
		// bb = true;
		//
		// }
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// System.out.println("wenti  01  ----");
		// e.printStackTrace();
		// } finally {
		// JDBCUtil.close(rs, JDBCUtil.getPs(),
		// JDBCUtil.getConnection());
		// }
		//
		// if (bb) {
		// System.out.println("���ݿ��ѯ���û��������ٴ�����ֱ�ӵõ��û� = " + user);
		//
		// } else {
		// System.out.println("���ݿ�δ��ѯ���û����ٴ�����ֱ�ӵõ��û� = " + user);
		//
		// }
		//
		// } else {
		// System.out.println("��ȡaccess_token ��jsonΪ�� = " + user);
		// }


		return user;
	}

	/**
	 * 
	 * @Description: ��¼�������ݴ���
	 * @param uid
	 * @return
	 */
	public LoginResultData loginResult(User user) {
		List<Land> lands = new ArrayList<Land>();
		// Set<Land> lands = new HashSet<Land>();
		for (int i = 0; i < 10000; i++) {
			Land land = new Land();
			String sql = "select * from farm_land where userId="
					+ user.getUserId();
			ResultSet rs = JDBCUtil.executeQuery(sql);
			try {
				// ��ѯ���ݿ�,��ȡ����uid��Ӧ������
				while (rs.next()) {
					land.setLandId(rs.getLong(1));
					land.setLandName(rs.getString(2));
					land.setLandState(rs.getString(3));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
			}
			lands.add(land);
		}
		LoginResultData loginResponseData = new LoginResultData();
		// user.setUserLands(lands);

		loginResponseData.setUser(user);
		loginResponseData.setLands(lands);
		String loginResultData = com.alibaba.fastjson.JSONObject
				.toJSONString(loginResponseData);
		System.out.println("loginResultData------------  = " + loginResultData);

		return loginResponseData;

	}

	/**
	 * 
	 * @Description: �����û�id��ȡ��������Ϣ
	 * @param userId
	 * @return
	 */
	public GetSeedMsgResultData getSeedMsg(Long userId) {
		GetSeedMsgResultData getSeedMsgResultData = new GetSeedMsgResultData();

		// 1.��ȡ
		String sql = "select * from farm_seed where seedState='2' userId="
				+ userId;
		List<Seed> seeds = new ArrayList<Seed>();
		System.out.println("sql = " + sql);
		ResultSet rs = JDBCUtil.executeQuery(sql);
		try {
			while (rs.next()) {
				Seed seed = new Seed();
				seed.setSeedId(rs.getLong(1));
				seed.setSeedName(rs.getString(2));
				seed.setSeedState(rs.getString(3));
				seed.setSeedGrowthTime(rs.getLong(4));
				seed.setSeedBuyPrice(rs.getLong(5));
				seed.setSeedSellingPrice(rs.getLong(6));
				seed.setSeedExperience(rs.getLong(7));
				seed.setSeedYield(rs.getLong(8));
				seed.setSeedFruitSellingPrice(rs.getLong(9));
				seed.setSeedType(rs.getLong(10));
				seed.setSeedImage(rs.getString(11));
				seed.setSeedNumber(rs.getInt(12));

				seeds.add(seed);
				System.out.println("�û�������Ϣ    lands = " + seed);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, JDBCUtil.getPs(), JDBCUtil.getConnection());
		}
		getSeedMsgResultData.setSeeds(seeds);
		return getSeedMsgResultData;
	}
}
